package com.offcn.task;

import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillGoodsTask {

    @Autowired
    private TbSeckillGoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 读取数据库中新增加的商品 增量更新到数据库
    * */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void dbToRedis(){
        System.out.println("begin update redis");
        //获取数据库中需要被添加到缓存中的商品
        //通过三个条件查询数据库 放入缓存
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStartTimeLessThan(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        criteria.andStockCountGreaterThan(0);
        List ids = null;
        try {
            //得到所有缓存中商品的id 数据
            ids = new ArrayList<>(redisTemplate.boundHashOps("seckillGoods").keys());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ids);
        criteria.andIdNotIn(ids);
        List<TbSeckillGoods> goodsList=goodsMapper.selectByExample(example);
        if(null!=goodsList&&goodsList.size()>0){
            for (TbSeckillGoods seckillGood : goodsList) {
                //将得到的商品数据存入缓存中  使用id作为key 对象作为value
                redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId()+"",seckillGood);
            }
            //向缓存中更新数据
            System.out.println("update redis success");
        }

    }

    /*
    * 读取数据库中结束时间过期的商品 通过id移除缓存中的数据
    * */
    /*
     * 读取数据库中新增加的商品 增量更新到数据库
     * */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void removeFromRedis(){
        System.out.println("begin delete redis");
        //获取数据库中需要被添加到缓存中的商品
        //通过三个条件查询数据库 放入缓存
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andEndTimeLessThan(new Date());
        criteria.andStockCountGreaterThan(0);
        List<TbSeckillGoods> goodsList=goodsMapper.selectByExample(example);
        if(null!=goodsList&&goodsList.size()>0){
            for (TbSeckillGoods seckillGood : goodsList) {
                //将得到的商品数据存入缓存中  使用id作为key 对象作为value
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId()+"");
            }
            //向缓存中更新数据
            System.out.println("delete redis success");
        }

    }
}
