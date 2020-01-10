package com.offcn.cart.service;

import com.offcn.pojo.group.CartGroup;

import java.util.List;

public interface CartService {


    List<CartGroup> addItemToCartList(List<CartGroup> cartList, Long itemId, Integer num);

    List<CartGroup> findCartListFromRedis(String username);

    List<CartGroup> mergeRedisAndCookie(List<CartGroup> redisCartList, List<CartGroup> cartList);

    void saveListToRedis(String username, List<CartGroup> redisCartList);
}
