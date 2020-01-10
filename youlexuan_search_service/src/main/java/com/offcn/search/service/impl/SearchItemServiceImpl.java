package com.offcn.search.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.swing.*;
import java.util.*;

@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map searchItemList(Map searchMap) {
        //关键字空格处理

        searchMap.put("keyWords",  ((String) searchMap.get("keyWords")).replace(" ",""));
        Map  resultMap = new HashMap();
        //得到商品的列表
         HighlightPage<TbItem> highlightPage = getItems(searchMap);
        //得到分类的列表
        List<String> cateList = getCateList(searchMap);
        resultMap.put("itemList",highlightPage.getContent());
        resultMap.put("total",highlightPage.getTotalElements());
        resultMap.put("totalPage",highlightPage.getTotalPages());
        resultMap.put("cateList",cateList);
        //循环赋予title之后 获取itme的list集合
        //查询得到的品牌列表
        Map map = null;
        if(StringUtils.isNotEmpty((String)searchMap.get("category"))){
            map= getBrandListAndSpecList((String)searchMap.get("category"));
            System.out.println("品牌和规格集合:"+map);
        }else{
            map = getBrandListAndSpecList(cateList.get(0));
        }
        //返回查询得到的 规格列表
        resultMap.putAll(map);
        return resultMap;
    }
    private Map getBrandListAndSpecList(String cateName) {
        //通过分类名称先从缓存中得到模板id
        Long typeId = (Long) redisTemplate.boundHashOps("cateItem").get(cateName);
        //通过模板id从缓存中获取对应的品牌列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId+"");
        //通过模板id从缓存中获取对应的品牌列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId+"");

        Map map =new HashMap();
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    private List<String> getCateList(Map searchMap) {
        List<String> cateList = new ArrayList<>();

        String keyWords = (String) searchMap.get("keyWords");
        Criteria criteria = new Criteria("item_keywords").is(keyWords);
        //创建查询 使用simple
        SimpleQuery query = new SimpleQuery("*:*");
        //设置分组的查询对象
        GroupOptions options = new GroupOptions();
        options.addGroupByField("item_category");
        //将分组对象赋予query查询对象
        query.setGroupOptions(options);
        query.addCriteria(criteria);
        //使用模板调用分组查询
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //得到分组后的结果对象
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //获取分组集合的入口对象
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //contents 里面的content对象包含分组的groupValue
        List<GroupEntry<TbItem>> contents = groupEntries.getContent();
        for (GroupEntry<TbItem> content : contents) {
            cateList.add(content.getGroupValue());
        }
        return cateList;
    }

    private HighlightPage getItems(Map searchMap) {
        String keyWords = (String) searchMap.get("keyWords");
        //创建支持高亮的查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //创建一个用于设置高亮参数的options对象
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<em style='color:blue'>");
        options.setSimplePostfix("</em>");
        query.setHighlightOptions(options);
        //创建查询条件 执行高亮查询
        Criteria criteria  = new Criteria("item_keywords").is(keyWords);
       //组装得到查询的条件对象
        criteria = getSearchCriteria(searchMap, criteria);
        //增加分页使用的两个设置
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
        query.addCriteria(criteria);
        //组装排序的参数
        //获取排序的域字段
        String sortField = (String) searchMap.get("sortField");
        //获取排序的依据
        String sortOrder = (String) searchMap.get("sortOrder");
        if (StringUtils.isNotEmpty(sortField)&&StringUtils.isNotEmpty(sortOrder)) {
            Sort sort = null;
            if (sortOrder.equals("ASC")) {
                sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            }else {
                sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            }
            query.addSort(sort);
        }
        //使用模板调用高亮查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        System.out.println(JSON.toJSONString(highlightPage));
        //包含查询结果集合和高亮结果集合的对象
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        //highligtEntry包含每个item和每个item高亮设置的对象
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            TbItem item = highlightEntry.getEntity();
            //判断item对应的高亮数组是否存在 如果存在再一层层取出来
            if(highlightEntry.getHighlights()!=null&highlightEntry.getHighlights().size()>0){
                if(highlightEntry.getHighlights().get(0).getSnipplets()!=null&&
                        highlightEntry.getHighlights().get(0).getSnipplets().size()>0){
                    item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
        }

        //高亮查询结束
        return highlightPage;
    }
//抽取组装查询条件的方法
    private Criteria getSearchCriteria(Map searchMap, Criteria criteria) {
        //判断是否能存在品牌条件
        if (StringUtils.isNotEmpty((String) searchMap.get("brand"))) {
            criteria = criteria.and("item_brand").is(searchMap.get("brand"));
        }
        //判断是否存在分类条件
        if (StringUtils.isNotEmpty((String) searchMap.get("category"))) {
            criteria = criteria.and("item_category").is(searchMap.get("category"));
        }

        //判断是否存在规格的筛选数据
        if (searchMap.get("spec") != null) {
            Map map = (Map) searchMap.get("spec");
            System.out.println(JSON.toJSONString(map));
            Set<String> set = map.keySet();
            for (String key : set) {
                //根据传递的key值变成英文组装查询的动态域字段  网络  机身内存
                criteria = criteria.and("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(map.get(key));
                System.out.println("查询的yu ziduan wei :=========" + "item_spec_" + Pinyin.toPinyin(key, "").toLowerCase());
            }
        }
        //判断是否存在价格区间
        if (StringUtils.isNotEmpty((String) searchMap.get("price"))) {
            //获取价钱的字符串 0 - 500
            String priceStr = (String) searchMap.get("price");
            String[] prices = priceStr.split("-");
            //使用起始价钱和结束价钱拼接查询条件
            //如果开始不是0 添加大于的判断 否则直接小于结束价钱
            if (!"0".equals(prices[0])) {
                criteria = criteria.and("item_price").greaterThan(prices[0]);
            }
            //如果末尾不是*才需要加小于的判断
            if (!"*".equals(prices[1])) {
                criteria = criteria.and("item_price").lessThan(prices[1]);
            }
        }
        return criteria;
    }
    //接收集合存储到solr索引库
    @Override
    public void importDataToSolr(List<TbItem> itemList) {

        for(TbItem item:itemList){
            Map newMap = new HashMap();
            //循环的每个item种的spec字符串 转换成map用于生成多个动态域字段 {网络：2G,内存 15g}
            Map map = JSON.parseObject(item.getSpec(),Map.class);
            //获取规格map种的key 中文
            Set<String> set = map.keySet();
            for(String key:set){
                newMap.put(Pinyin.toPinyin(key,"").toLowerCase(),map.get(key));
            }
            item.setSpecMap(newMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //根据集合id调用模板的删除删除索引库数据
        SimpleQuery query = new SimpleQuery();
        //创建根据id查询的条件
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        //将id为条件的查询对象交给模板执行删除
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
        System.out.println("删除solr数据成功");
    }
}
