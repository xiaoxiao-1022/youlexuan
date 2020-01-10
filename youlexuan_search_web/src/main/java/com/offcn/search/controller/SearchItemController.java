package com.offcn.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.SearchItemService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchItemController {

    @Reference
    private SearchItemService itemService;

    @RequestMapping("itemList")
    public Map findItemList(@RequestBody Map searchMap){
        return  itemService.searchItemList(searchMap);
    }
}
