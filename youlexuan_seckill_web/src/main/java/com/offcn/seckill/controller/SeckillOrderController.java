package com.offcn.seckill.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.seckill.service.SeckillOrderService;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillOrder> findAll(){			
		return seckillOrderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageInfo  findPage(int pageNum,int pageSize){
		return seckillOrderService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 增加
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.add(seckillOrder);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.update(seckillOrder);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeckillOrder findOne(Long id){
		return seckillOrderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			seckillOrderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search")
	public PageInfo search(@RequestBody TbSeckillOrder seckillOrder, int pageNum, int pageSize  ){
		return seckillOrderService.findPage(seckillOrder, pageNum, pageSize);
	}
	//接收秒杀商品的id
	@RequestMapping("/submitOrder")
	public Result submitOrder(Long seckillId  ){
		//判断用户是否登录
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userId.equals("anonymousUser")) {
			return new Result(false,"用户未登录");
		}

		try {
			//保存用户秒杀的订单
			seckillOrderService.submitOrder(userId,seckillId);
			return new Result(true,"秒杀成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,e.getMessage());
		}

	}
	//接收秒杀商品的id
	@RequestMapping("/submitOrderTest")
	public Result submitOrder(String userId,Long seckillId  ){
		try {
			//保存用户秒杀的订单
			seckillOrderService.submitOrder(userId,seckillId);
			return new Result(true,"秒杀成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,e.getMessage());
		}

	}
}
