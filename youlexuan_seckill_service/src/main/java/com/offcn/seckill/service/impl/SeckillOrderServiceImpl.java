package com.offcn.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.group.GoodsGroup;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbSeckillOrderMapper;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.pojo.TbSeckillOrderExample;
import com.offcn.pojo.TbSeckillOrderExample.Criteria;
import com.offcn.seckill.service.SeckillOrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import javax.annotation.Resource;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbSeckillGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageInfo findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		List<TbSeckillOrder> list=    seckillOrderMapper.selectByExample(null);
		return new PageInfo(list);
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageInfo findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}	
		}
		
		List<TbSeckillOrder> list= seckillOrderMapper.selectByExample(example);
		return new PageInfo(list);
	}

	@Override
	public void submitOrder(String userId, Long seckillId) {
		//开启redis对事务的支持
		redisTemplate.setEnableTransactionSupport(true);
		//复写执行任务的方法
		redisTemplate.execute(new SessionCallback() {
			@Override
			public Object execute(RedisOperations redisOperations) throws DataAccessException {
				//开启事务对数据的监控
				redisOperations.watch("seckillGoods");


				//获取缓存中秒杀商品
				TbSeckillGoods goods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId + "");
				if (null == goods) {
					throw new RuntimeException("商品不存在");
				}
				if (goods.getStockCount()<1) {
					throw new RuntimeException("被抢光");
				}
				//开启事务
				redisOperations.multi();
				//需要必要的空查询
				redisOperations.boundHashOps("seckillGoods").get(seckillId+"");
				TbSeckillOrder order = new TbSeckillOrder();
				order.setId(idWorker.nextId());
				order.setSellerId(goods.getSellerId());
				order.setStatus("0");
				order.setUserId(userId);
				order.setSeckillId(seckillId);
				order.setMoney(goods.getCostPrice());
				//组装订单数据 存入缓存
				redisTemplate.boundHashOps("seckillOrders").put(userId,order);
				//更新商品库存
				goods.setStockCount(goods.getStockCount()-1);
				redisTemplate.boundHashOps("seckillGoods").put(seckillId+"",goods);
				if (goods.getStockCount()<1) {
					redisTemplate.boundHashOps("seckillGoods").delete(seckillId+"");

				}
				return redisOperations.exec();
			}
		});



	}

	@Override
	public TbSeckillOrder findOrderFromRedis(String userId) {
		return ((TbSeckillOrder) redisTemplate.boundHashOps("seckillOrders").get(userId));
	}

	@Override
	public void saveOrderFromRedis(String userId, String orderNo) {
		//将缓存中存储的订单写入mysql数据库
		TbSeckillOrder order = ((TbSeckillOrder) redisTemplate.boundHashOps("seckillOrders").get(userId));
		if (null != order) {
			order.setStatus("1");
			order.setPayTime(new Date());
			seckillOrderMapper.insert(order);
			//mysql数据库中秒杀商品的数量的修改
			TbSeckillGoods goods = goodsMapper.selectByPrimaryKey(order.getSeckillId());
			goods.setStockCount(goods.getStockCount()-1);
			goodsMapper.updateByPrimaryKey(goods);
			//redis缓存中的订单删除掉
			redisTemplate.boundHashOps("seckillOrders").delete(userId);
		}
	}

	@Override
	public void deleteOrderFromRedis(String userId, String orderNo) {
		TbSeckillOrder order = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrders").get(userId);
		if(null!=order){
			//获取订单对应的秒杀商品
			TbSeckillGoods goods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(order.getSeckillId()+"");
			goods.setStockCount(goods.getStockCount()+1);
			redisTemplate.boundHashOps("seckillGoods").put(goods.getId()+"",goods);
			//redis缓存中的订单删除掉
			redisTemplate.boundHashOps("seckillOrders").delete(userId);
		}
	}


}
