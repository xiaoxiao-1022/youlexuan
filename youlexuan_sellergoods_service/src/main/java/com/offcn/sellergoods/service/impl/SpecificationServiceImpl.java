package com.offcn.sellergoods.service.impl;
import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.pojo.group.SpecificationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.sellergoods.service.SpecificationService;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Transactional
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageInfo findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		List<TbSpecification> list=    specificationMapper.selectByExample(null);
		return new PageInfo(list);
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SpecificationGroup group) {

		specificationMapper.insert(group.getSpecification());
		List<TbSpecificationOption> options = group.getOptionList();
		for (TbSpecificationOption option:options) {
			option.setSpecId(group.getSpecification().getId());
			optionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(SpecificationGroup group){

		specificationMapper.updateByPrimaryKey(group.getSpecification());
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(group.getSpecification().getId());
		optionMapper.deleteByExample(example);
		List<TbSpecificationOption> optionList = group.getOptionList();
		for (TbSpecificationOption option:optionList) {
			option.setSpecId(group.getSpecification().getId());
			optionMapper.insert(option);
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationGroup findOne(Long id){
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getId());
		List<TbSpecificationOption> options = optionMapper.selectByExample(example);
		return new SpecificationGroup(specification,options);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageInfo findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		List<TbSpecification> list= specificationMapper.selectByExample(example);
		return new PageInfo(list);
	}

	@Override
	public List<Map> findSpecOptions() {
		return specificationMapper.findSpecOptions();
	}

}
