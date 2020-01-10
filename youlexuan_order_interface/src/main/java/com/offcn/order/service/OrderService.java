package com.offcn.order.service;
import java.util.List;
import com.offcn.pojo.TbOrder;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageInfo findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long orderId);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] orderIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageInfo findPage(TbOrder order, int pageNum, int pageSize);

	void submitOrder(String userId, TbOrder order);

    TbPayLog findPayLogFromRedis(String userId);

    void updatePayLogStatus(String orderNo);
}
