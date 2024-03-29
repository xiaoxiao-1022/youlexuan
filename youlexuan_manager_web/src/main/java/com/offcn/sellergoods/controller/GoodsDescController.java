package com.offcn.sellergoods.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.sellergoods.service.GoodsDescService;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goodsDesc")
public class GoodsDescController {

	@Reference
	private GoodsDescService goodsDescService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoodsDesc> findAll(){			
		return goodsDescService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageInfo  findPage(int pageNum,int pageSize){
		return goodsDescService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 增加
	 * @param goodsDesc
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoodsDesc goodsDesc){
		try {
			goodsDescService.add(goodsDesc);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goodsDesc
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbGoodsDesc goodsDesc){
		try {
			goodsDescService.update(goodsDesc);
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
	public TbGoodsDesc findOne(Long goodsId){
		return goodsDescService.findOne(goodsId);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] goodsIds){
		try {
			goodsDescService.delete(goodsIds);
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
	public PageInfo search(@RequestBody TbGoodsDesc goodsDesc, int pageNum, int pageSize  ){
		return goodsDescService.findPage(goodsDesc, pageNum, pageSize);
	}
	
}
