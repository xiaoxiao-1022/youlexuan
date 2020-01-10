package com.offcn.sellergoods.controller;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
/*import com.offcn.page.service.ItemPageService;*/
import com.offcn.pojo.TbItem;
import com.offcn.pojo.group.GoodsGroup;
/*import com.offcn.search.service.SearchItemService;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbGoods;
import com.offcn.sellergoods.service.GoodsService;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

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
	/*@Reference(timeout = 100000)
	private SearchItemService searchItemService;*/
	/*@Reference
	private ItemPageService itemPageService;*/
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination solr_queue;
	@Autowired
	private Destination solr_delete_queue;
	@Autowired
	private Destination page_topic;
	@Autowired
	private Destination page_delete_topic;



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
	public PageInfo  findPage(int pageNum,int pageSize){
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
			//调用search模块中的删除方法
			//searchItemService.deleteByIds(Arrays.asList(ids));
			jmsTemplate.send(solr_delete_queue, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					String deleteIds = JSON.toJSONString(ids);
					return session.createTextMessage(deleteIds);
				}
			});
			jmsTemplate.send(page_delete_topic, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					String deleteIds = JSON.toJSONString(ids);
					return session.createTextMessage(deleteIds);
				}
			});
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
	public PageInfo search(@RequestBody TbGoods goods, int pageNum, int pageSize  ){
		return goodsService.findPage(goods, pageNum, pageSize);
	}
	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 */
	@RequestMapping("updateStatus")
	public Result updateStatus(Long [] ids,String status){
		try {
			goodsService.updateStatus(ids,status);

			//根据所有的id得到刚刚审核通过后的所有商品 存储到solr
			if(status.equals("1")){
				List<TbItem> itemList = goodsService.findByIds(ids);
				//searchItemService.importDataToSolr(itemList);
				jmsTemplate.send(solr_queue, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						//将所有需要被传输的文本集合 转成文字传输
						String itemListStr = JSON.toJSONString(itemList);
						return session.createTextMessage(itemListStr);
					}
				});
				//生成每个商品的详情静态页面
				jmsTemplate.send(page_topic, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						//将所有需要被传输的文本集合 转成文字传输
						String idstr = JSON.toJSONString(ids);
						return session.createTextMessage(idstr);
					}
				});
				//生成每个商品的静态模块
			/*	for (Long id : ids) {
					boolean b = itemPageService.genItemHtml(id);
					System.out.println(b);
				}*/

			}
			return new Result(true,"操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"操作失败");
		}
	}
	/*@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		boolean b = itemPageService.genItemHtml(goodsId);
		System.out.println(b);

	}*/

}
