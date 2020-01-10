package com.offcn.sellergoods.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.group.GoodsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;
import javax.swing.*;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Transactional
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageInfo findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbGoods> list = goodsMapper.selectByExample(null);
        return new PageInfo(list);
    }

    /**
     * 增加
     */
    @Override
    public void add(GoodsGroup goodsGroup) {
        goodsGroup.getGoods().setAuditStatus("0");
        goodsMapper.insert(goodsGroup.getGoods());
        goodsGroup.getGoodsDesc().setGoodsId(goodsGroup.getGoods().getId());
        tbGoodsDescMapper.insert(goodsGroup.getGoodsDesc());
        doSaveItem(goodsGroup);
    }

    private void doSaveItem(GoodsGroup goodsGroup) {
        //根据goods中是否启用规格选项值
        if (goodsGroup.getGoods().getIsEnableSpec().equals("1")) {
            //获取组合类中的所有的sku item列表对象 循环存储
            List<TbItem> itemList = goodsGroup.getItemList();
            for (TbItem item : itemList) {
                //获取当前item中的spec选项,拼接商品spu名称,作为title
                //{"spec":{"网络":"移动3G","机身内存":"16G"}:
                Map spec = JSON.parseObject(item.getSpec(), Map.class);
                String title = goodsGroup.getGoods().getGoodsName() + " ";
                Set<String> set = spec.keySet();
                for (String s : set) {
                    title += spec.get(s);
                }
                item.setTitle(title);
                saveItem(goodsGroup, item);
            }
        } else {
            TbItem item = new TbItem();
            item.setPrice(goodsGroup.getGoods().getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setStatus("1");
            item.setSpec("{}");
            item.setTitle(goodsGroup.getGoods().getGoodsName());
            saveItem(goodsGroup, item);
        }
    }

    private void saveItem(GoodsGroup goodsGroup, TbItem item) {
        item.setSellerId(goodsGroup.getGoods().getSellerId());
        //根据sellerId得到商家的对象 获取名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goodsGroup.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //存储描述中的首图
        List<Map> images = JSON.parseArray(goodsGroup.getGoodsDesc().getItemImages(), Map.class);
        if (null != images && images.size() > 0) {
            Map m = images.get(0);
            item.setImage((String) m.get("url"));
        }
        item.setGoodsId(goodsGroup.getGoods().getId());
        item.setCategoryid(goodsGroup.getGoods().getCategory3Id());
        //通过分类id查询得到分类对象 获取分类名称
        TbItemCat cat = itemCatMapper.selectByPrimaryKey(goodsGroup.getGoods().getCategory3Id());
        item.setCategory(cat.getName());//分类名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goodsGroup.getGoods().getBrandId());
        item.setBrand(tbBrand.getName());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        itemMapper.insert(item);
    }

    /**
     * 修改
     */
    @Override
    public void update(GoodsGroup group) {
        //如果修改后商品状态为待审核
        group.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(group.getGoods());
        //修改详情表
        tbGoodsDescMapper.updateByPrimaryKey(group.getGoodsDesc());
        //修改itemList表 先删除数据库item所有记录 添加传递的所有记录
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(group.getGoods().getId());
        itemMapper.deleteByExample(example);
        //存储itemList集合
        doSaveItem(group);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public GoodsGroup findOne(Long id) {
        TbGoods goods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
        TbItemExample example = new TbItemExample();

        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> items = itemMapper.selectByExample(example);
        GoodsGroup group = new GoodsGroup();
        group.setGoods(goods);
        group.setGoodsDesc(goodsDesc);
        group.setItemList(items);
        return group;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //修改对象的isDelete属性
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageInfo findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //遍历sku集合
            for (TbItem item : itemList) {
                //修改状态
                item.setStatus("1");
                itemMapper.updateByPrimaryKey(item);
            }

        }
    }

    @Override
    public List<TbItem> findByIds(Long[] ids) {
        List<TbItem> allItems = new ArrayList<TbItem>();
        //ids是所有被审核通过的商品的id数组
        for (Long goodsid : ids) {
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsid);
            //通过每一个商品id都得到对应的所有sku列表集合
            List<TbItem> itemList = itemMapper.selectByExample(example);
            allItems.addAll(itemList);
        }
        return allItems;

    }

}
