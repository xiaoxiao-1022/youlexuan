package com.offcn.page.service;
/**
 * 商品详细页接口
 * @author Administrator
 *
 */

public interface ItemPageService {
    //生成商品详细页
    public boolean createItemHtml(Long goodsId);

    void deleteItemHtml(Long id);
}
