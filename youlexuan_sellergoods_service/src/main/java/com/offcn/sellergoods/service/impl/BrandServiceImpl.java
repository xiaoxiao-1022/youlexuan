package com.offcn.sellergoods.service.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: wyan
 * @Date: 2019-11-19 15:40
 * @Description:
 */
@Transactional
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    //查询数据库所有的品牌数据
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(new TbBrandExample());
    }

    @Override
    public PageInfo<TbBrand> findPage(int pageNum, int pafgeSize) {
        PageHelper.startPage(pageNum,pafgeSize);
        List list = (Page<TbBrand>) brandMapper.selectByExample(new TbBrandExample());
        return new PageInfo<TbBrand>(list);
    }

    @Override
    public PageInfo<TbBrand> search(int pageNum, int pafgeSize, TbBrand tbBrand) {
        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        if(tbBrand != null){
            if (StringUtils.isNotEmpty(tbBrand.getName())){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (StringUtils.isNotEmpty(tbBrand.getFirstChar())){
               criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }
        PageHelper.startPage(pageNum,pafgeSize);
        List list =  brandMapper.selectByExample(tbBrandExample);
        return new PageInfo<TbBrand>(list);
    }

    @Override
    public Boolean add(TbBrand tbBrand) {
        return brandMapper.insertSelective(tbBrand)>0;
    }

    @Override
    public Boolean update(TbBrand tbBrand) {
        return brandMapper.updateByPrimaryKeySelective(tbBrand)>0;
    }

    @Override
    public TbBrand find(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean dele(Long[] ids) {
        Integer count = 0;
       if(ids!=null){
           for (Long id:ids) {
               brandMapper.deleteByPrimaryKey(id);
               count++;
           }
       }
        if (count>0) {
            return true;
        }else {
            return false;
        }
    }
}
