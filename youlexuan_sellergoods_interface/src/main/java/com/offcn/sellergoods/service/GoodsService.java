package com.offcn.sellergoods.service;
import java.util.List;
import com.offcn.pojo.TbGoods;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.group.GoodsGroup;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageInfo findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(GoodsGroup goodsGroup);
	
	
	/**
	 * 修改
	 */
	public void update(GoodsGroup group);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsGroup findOne(Long id);
	
	
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
	public PageInfo findPage(TbGoods goods, int pageNum, int pageSize);

    void updateStatus(Long[] ids, String status);

    List<TbItem> findByIds(Long[] ids);
}
