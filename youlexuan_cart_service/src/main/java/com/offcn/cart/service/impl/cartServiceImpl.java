package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import com.offcn.pojo.group.CartGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import javax.lang.model.util.ElementScanner6;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class cartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private  RedisTemplate redisTemplate;

    @Override
    public List<CartGroup> addItemToCartList(List<CartGroup> cartList, Long itemId, Integer num) {
        //第一步判断itemId sku对应的商家是否存在
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        CartGroup cartGroup = findCartGroupBySellerId(cartList, item.getSellerId());
        if(null!=cartGroup) {
            //如果原始商家存在,判断商家的itemList是否包含当前的item
            List<TbOrderItem> itemList = cartGroup.getItemList();
            TbOrderItem orderItem = findTbOrderItemByItemId(itemList, itemId);
            if (null != orderItem) {
                //更改数量
                orderItem.setNum(orderItem.getNum() + num);
                //更改总价
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*num));
                //更改数量后判断数量是否有效 如果<=0 从list集合中移除
                System.out.println(orderItem.getNum()+"=================");
                if (orderItem.getNum() <= 0) {
                    itemList.remove(orderItem);
                }
                //如果itemlist的元素数量为空 那么从cartList中移除当前的cartgroup对象
                if (itemList.size() <= 0) {
                    cartList.remove(cartGroup);
                }

            } else {
                //使用数据的sku item 和数量创建订单的orderItem
                orderItem = createOrderItemByItem(item, num);
                itemList.add(orderItem);
            }
        }else {
            //如果不存在 直接创建对象添加
            cartGroup = new CartGroup();
            cartGroup.setSellerId(item.getSellerId());
            cartGroup.setSellerName(item.getSeller());
            List<TbOrderItem> orderItems = new ArrayList<>();
            TbOrderItem orderItem = createOrderItemByItem(item,num);
            orderItems.add(orderItem);
            cartGroup.setItemList(orderItems);
            cartList.add(cartGroup);
        }
        return cartList;
    }
    //从缓存中读取cartList数据
    @Override
    public List<CartGroup> findCartListFromRedis(String username) {
        //根据用户的账号作为key获取
       List<CartGroup> list = ((List<CartGroup>) redisTemplate.boundHashOps("cartList").get(username));
        System.out.println(list);
        System.out.println("find list from redis");
        if (null == list) {
            list = new ArrayList<>();
        }
        return list;
    }
    /*
    * 将cooKies中的list集合合并到redis中
    * 实现思路 循环遍历cookie的list 得到每一个itemId 和数量num
    * 调用addItemToCartList*/
    @Override
    public List<CartGroup> mergeRedisAndCookie(List<CartGroup> redisCartList, List<CartGroup> cartList) {
        for (CartGroup cartGroup : cartList) {
            List<TbOrderItem> itemList = cartGroup.getItemList();
            for (TbOrderItem item : itemList) {
                Long itemId = item.getItemId();
                Integer num = item.getNum();
                addItemToCartList(redisCartList,itemId,num);

            }
        }
        return redisCartList;
    }

    @Override
    public void saveListToRedis(String username, List<CartGroup> redisCartList) {
        System.out.println("save list from redis");
        redisTemplate.boundHashOps("cartList").put(username,redisCartList);
    }

    private TbOrderItem createOrderItemByItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        orderItem.setNum(num);
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        return orderItem;
    }

    private TbOrderItem findTbOrderItemByItemId(List<TbOrderItem> itemList, Long itemId) {
        for (TbOrderItem item : itemList) {
            if (itemId.longValue()==item.getItemId().longValue()){
                return item;
            }
        }
        return null;
    }

    private CartGroup findCartGroupBySellerId(List<CartGroup> cartList, String sellerId) {
        //循环cartList判断sellerId是否一致
        for (CartGroup cartGroup : cartList) {
            //如果原始集合中存在当前的商家cart对象 返回
            if (sellerId.equals(cartGroup.getSellerId())) {
                return cartGroup;
            }
        }
        return null;
    }
}
