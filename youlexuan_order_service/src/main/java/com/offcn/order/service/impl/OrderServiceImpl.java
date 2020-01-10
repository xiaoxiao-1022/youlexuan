package com.offcn.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.offcn.mapper.TbOrderItemMapper;
import com.offcn.mapper.TbPayLogMapper;
import com.offcn.pojo.TbOrderItem;
import com.offcn.pojo.TbPayLog;
import com.offcn.pojo.group.CartGroup;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbOrderMapper;
import com.offcn.pojo.TbOrder;
import com.offcn.pojo.TbOrderExample;
import com.offcn.pojo.TbOrderExample.Criteria;
import com.offcn.order.service.OrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper itemMapper;

	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageInfo findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbOrder> list=    orderMapper.selectByExample(null);
		return new PageInfo(list);
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		orderMapper.insert(order);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * @param orderId
	 * @return
	 */
	@Override
	public TbOrder findOne(Long orderId){
		return orderMapper.selectByPrimaryKey(orderId);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] orderIds) {
		for(Long orderId:orderIds){
			orderMapper.deleteByPrimaryKey(orderId);
		}
	}


	@Override
	public PageInfo findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();

		if(order!=null){
			if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
		}

		List<TbOrder> list= orderMapper.selectByExample(example);
		return new PageInfo(list);
	}

	@Override
	public void submitOrder(String userId, TbOrder order) {
		TbPayLog tbPayLog = new TbPayLog();
		List<String> orderList = new ArrayList<>();
		//前端传过来的order订单类只包含了一些订单基本信息
		//实际存储到数据库的订单 根据购物车中商家的数量决定订单类的存储数量
		//根据用户id得到缓存中订单的购物车数据 根据cart的集合循环创建订单
		List<CartGroup> cartList = (List<CartGroup>) redisTemplate.boundHashOps("cartList").get(userId);
		double totalMoney = 0.00;
		for(CartGroup cart:cartList){
			Long orderId = idWorker.nextId();
			orderList.add(orderId+"");
			//创建订单TbOrder
			TbOrder tbOrder = new TbOrder();
			tbOrder.setSourceType(order.getSourceType());
			tbOrder.setCreateTime(new Date());
			tbOrder.setOrderId(orderId);
			//每个商家订单的金额根据itemList循环计算得到
			Double orderMoney = 0.00;
			List<TbOrderItem> itemList = cart.getItemList();
			for(TbOrderItem item:itemList){
				orderMoney+=item.getTotalFee().doubleValue();
				item.setId(idWorker.nextId());
				item.setOrderId(orderId);
				itemMapper.insert(item);
			}
			totalMoney+=orderMoney;
			tbOrder.setPayment(new BigDecimal(orderMoney));
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setReceiver(order.getReceiver());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			tbOrder.setSellerId(cart.getSellerId());
			tbOrder.setStatus("1");
			tbOrder.setUserId(userId);
			orderMapper.insert(tbOrder);
			System.out.println("order save success");
		}
		//存储订单的支付记录表
		tbPayLog.setCreateTime(new Date());
		String orderStr = orderList.toString().replace("[","").replace("]","").replace(" ","");
		tbPayLog.setOrderList(orderStr);
		tbPayLog.setOutTradeNo(idWorker.nextId()+"");
		tbPayLog.setPayType("1");
		BigDecimal total_money1 = BigDecimal.valueOf(totalMoney);
		BigDecimal cj = BigDecimal.valueOf(100d);
		//高精度乘法
		BigDecimal bigDecimal = total_money1.multiply(cj);
		System.out.println("高精度处理:"+bigDecimal.toBigInteger().longValue());
		tbPayLog.setTotalFee(bigDecimal.toBigInteger().longValue());
		tbPayLog.setTradeState("0");
		tbPayLog.setUserId(userId);
		payLogMapper.insert(tbPayLog);
		//存储入数据库
		//清楚redis缓存中购物车的数据
		redisTemplate.boundHashOps("cartList").delete(userId);
		redisTemplate.boundHashOps("patLogList").put(userId,tbPayLog);
		System.out.println("下单成功 购物车数据清空....");

	}

	@Override
	public TbPayLog findPayLogFromRedis(String userId) {

		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("patLogList").get(userId);
		return payLog;
	}


	@Override
	public void updatePayLogStatus(String orderNo) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(orderNo);
		payLog.setTradeState("1");
		payLog.setPayTime(new Date());
		//更改支付日志表
		payLogMapper.updateByPrimaryKey(payLog);
		//更改对应的订单表
		String orderIdStr = payLog.getOrderList();
		String[] ids = orderIdStr.split(",");
		for (String id : ids) {
			Long orderId= Long.parseLong(id);
			TbOrder order = orderMapper.selectByPrimaryKey(orderId);
			order.setStatus("2");
			order.setPaymentTime(new Date());
			order.setUpdateTime(new Date());
			orderMapper.updateByPrimaryKey(order);
		}
		//删除缓存中的用户订单数据
		redisTemplate.boundHashOps("patLogList").delete(payLog.getUserId());
	}
}
