package com.offcn.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class indexController {

    @Reference
    private ContentService contentService;
    @RequestMapping("findBannerByCategoryId")
    public List<TbContent> findBannerByCategoryId(Long cateId){
        return contentService.findBannerByCategoryId(cateId);
    }

}
