package com.offcn.solr.util;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: wyan
 * @Date: 2019-11-28 13:39
 * @Description:
 */
@Component("importData")
public class ImportDataBaseToSolr {


    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    public void importData(){
        //查询所有已经审核通过的商品 存储solr的索引库
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> list = itemMapper.selectByExample(example);
        for(TbItem item:list){
            Map newMap = new HashMap();
            //循环的每个item种的spec字符串 转换成map用于生成多个动态域字段
            Map map = JSON.parseObject(item.getSpec(),Map.class);
            //获取规格map种的key 中文
            Set<String> set = map.keySet();
            for(String key:set){
                newMap.put(Pinyin.toPinyin(key,"").toLowerCase(),map.get(key));
            }
            item.setSpecMap(newMap);
            System.out.println(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
    @Test
    public void test1() {
        //初始化spring容器
        ApplicationContext context =
                new ClassPathXmlApplicationContext
                        ("classpath*:spring/applicationContext-*.xml");
        ImportDataBaseToSolr importUtil = context.getBean(ImportDataBaseToSolr.class);
        importUtil.importData();
    }
    @Test
    public void delete(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
