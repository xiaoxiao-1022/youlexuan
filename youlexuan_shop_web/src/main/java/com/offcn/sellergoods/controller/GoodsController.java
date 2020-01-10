package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.offcn.fdfs.FastDFSClient;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.entity.Result;
import com.offcn.pojo.group.GoodsGroup;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 100000)
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){

		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageInfo findPage(int pageNum, int pageSize){
		return goodsService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsGroup goodsGroup){
		try {
			//获取当前登陆的商家id
			String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
			goodsGroup.getGoods().setSellerId(sellerId);
			goodsService.add(goodsGroup);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsGroup group){
		try {
			//判断传递goods中的seller和当前登录的seller是否是一个
			String sellerid = SecurityContextHolder.getContext().getAuthentication().getName();
			if(!group.getGoods().getSellerId().equals(sellerid))
			{
				return new Result(false,"非法操作");
			}
			goodsService.update(group);
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
	public GoodsGroup findOne(Long id){

		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search")
	public PageInfo search(@RequestBody TbGoods goods, int pageNum, int pageSize  ){
		goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return goodsService.findPage(goods, pageNum, pageSize);
	}

}
