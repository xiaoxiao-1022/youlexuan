package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface SearchItemService {

    Map searchItemList(Map searchMap);

    void importDataToSolr(List<TbItem> itemList);

    void deleteByIds(List<Long> asList);
}
