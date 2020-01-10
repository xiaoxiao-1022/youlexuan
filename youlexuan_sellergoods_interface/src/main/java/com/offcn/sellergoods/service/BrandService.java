package com.offcn.sellergoods.service;


import com.github.pagehelper.PageInfo;
import com.offcn.pojo.TbBrand;

import java.util.List;

/**
 * @Auther: wyan
 * @Date: 2019-11-19 15:39
 * @Description:
 */
public interface BrandService {

    public List<TbBrand> findAll();
    public PageInfo<TbBrand> findPage(int pageNum, int pafgeSize);
    public PageInfo<TbBrand> search(int pageNum, int pafgeSize,TbBrand tbBrand);

    Boolean add(TbBrand tbBrand);

    Boolean update(TbBrand tbBrand);

    TbBrand find(Long id);

    boolean dele(Long[] ids);
}
