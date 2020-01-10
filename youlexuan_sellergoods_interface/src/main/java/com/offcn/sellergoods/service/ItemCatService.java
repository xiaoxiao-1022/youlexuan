package com.offcn.sellergoods.service;
import java.util.List;

import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemCat;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.group.GoodsGroup;

/**
 * 商品类目服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageInfo findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbItemCat itemCat);
	
	
	/**
	 * 修改
	 */
	public void update(TbItemCat item_cat);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbItemCat findOne(Long id);
	
	
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
	public PageInfo findPage(TbItemCat item_cat, int pageNum, int pageSize);

	List<TbItemCat> findByParentId(Long parentId);
}
