package com.offcn.cart.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.pojo.entity.Result;
import com.offcn.pojo.group.CartGroup;
import com.offcn.utils.CookieUtil;
import org.opensaml.ws.wstrust.impl.RequestedAttachedReferenceUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 100000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
        public List<CartGroup> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie中获取list集合数据
        //获取数据通过cookie名称 和字符编码 默认utf-8
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
        //如果cookie中未存储数据 给个默认空集合
        if (StringUtils.isEmpty(cartListStr)) {
            cartListStr = "[]";
        }
        //将字符串转换成list集合返回数据
        List<CartGroup> cartList = JSON.parseArray(cartListStr, CartGroup.class);
        if (username.equals("anonymousUser")) {
            return cartList;
        }else{
            //从缓存中获取list集合返回
            List<CartGroup> redisCartList = cartService.findCartListFromRedis(username);
            //执行两个集合中数据的合并
            redisCartList = cartService.mergeRedisAndCookie(redisCartList,cartList);
            //清除cookie中的数据
            CookieUtil.deleteCookie(request,response,"cartList");
            //将合并后的redis集合存储到redis缓存中
            cartService.saveListToRedis(username,redisCartList);
            return redisCartList;
        }




    }
    @RequestMapping("/addItemToCartList")
    //允许跨域访问
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
       // response.setHeader("Access-Control-Allow-Origin","*");
        try {
            //先获取cookie中原始的list集合
            List<CartGroup> cartList = findCartList();
            //使用两个参数调用service的方法向原始集合中添加数据
            cartList=cartService.addItemToCartList(cartList,itemId,num);
            //将添加的cartList存储到cookie中,key 使用cartList
            String cartListStr = JSON.toJSONString(cartList);
            //如果用户已经登录存储到redis中否则存储cookie
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username.equals("anonymousUser")) {
                CookieUtil.setCookie(request,response,"cartList",cartListStr,3600*24,true);
            }else {

                cartService.saveListToRedis(username,cartList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
        return new Result(true,"添加成功");
    }
}
