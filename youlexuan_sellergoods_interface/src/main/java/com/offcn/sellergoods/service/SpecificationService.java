package com.offcn.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.offcn.pojo.TbSpecification;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.group.SpecificationGroup;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageInfo findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SpecificationGroup group);
	
	
	/**
	 * 修改
	 */
	public void update(SpecificationGroup group);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SpecificationGroup findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageInfo findPage(TbSpecification specification, int pageNum, int pageSize);

    List<Map> findSpecOptions();
}
