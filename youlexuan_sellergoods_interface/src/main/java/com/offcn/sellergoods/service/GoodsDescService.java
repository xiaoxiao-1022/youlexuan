package com.offcn.sellergoods.service;
import java.util.List;
import com.offcn.pojo.TbGoodsDesc;
import com.github.pagehelper.PageInfo;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsDescService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoodsDesc> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageInfo findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbGoodsDesc goods_desc);
	
	
	/**
	 * 修改
	 */
	public void update(TbGoodsDesc goods_desc);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbGoodsDesc findOne(Long goodsId);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] goodsIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageInfo findPage(TbGoodsDesc goods_desc, int pageNum, int pageSize);
	
}
