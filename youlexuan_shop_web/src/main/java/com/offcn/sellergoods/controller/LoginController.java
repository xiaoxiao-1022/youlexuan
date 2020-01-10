package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wyan
 * @Date: 2019-11-19 15:43
 * @Description:
 */
@RestController
@RequestMapping("login")
public class LoginController {


    @Reference
    private BrandService brandService;
    //接受请求返回brand品牌的json
    @RequestMapping("getName")
    public Map getName(){
        Map<String,String> map = new HashMap();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("name",name);
        return  map;
    }
}

