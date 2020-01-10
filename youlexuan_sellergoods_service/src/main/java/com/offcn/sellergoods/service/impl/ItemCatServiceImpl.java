package com.offcn.sellergoods.service.impl;
import java.util.List;

import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.group.GoodsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.pojo.TbItemCat;
import com.offcn.pojo.TbItemCatExample;
import com.offcn.pojo.TbItemCatExample.Criteria;
import com.offcn.sellergoods.service.ItemCatService;
import com.github.pagehelper.PageInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品类目服务实现层
 * @author Administrator
 *
 */
@Transactional
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageInfo findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		List<TbItemCat> list=    itemCatMapper.selectByExample(null);
		return new PageInfo(list);
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {

		itemCatMapper.insert(itemCat);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageInfo findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}	
		}
		
		List<TbItemCat> list= itemCatMapper.selectByExample(example);
		return new PageInfo(list);
	}

	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		List<TbItemCat> catsList = findAll();
		//find查询方式是增删改数据后必须执行的方法,作为缓存初始化的位置及时更新
		for (TbItemCat tbItemCat : catsList) {
			redisTemplate.boundHashOps("cateItem").put(tbItemCat.getName(),tbItemCat.getTypeId());
		}
		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		return  itemCatMapper.selectByExample(tbItemCatExample);

	}

}
