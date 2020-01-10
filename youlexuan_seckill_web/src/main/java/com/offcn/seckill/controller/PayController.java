package com.offcn.seckill.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.pay.service.PayService;
import com.offcn.pojo.TbPayLog;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.pojo.entity.Result;
import com.offcn.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("pay")
public class PayController {

    @Reference(timeout = 10000)
    private PayService payService;
    @Reference(timeout = 10000)
    private SeckillOrderService orderService;



    //生成二维码的方法
    @RequestMapping("createPayUrl")
    public Map createPayUrl(){
        //获取当前登陆用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //从缓存中获取订单
        TbSeckillOrder order = orderService.findOrderFromRedis(userId);
        System.out.println(payService);
        if (null!=order) {
            System.out.println(order.getId());
            return payService.createPayUrl(order.getId(),order.getMoney()+"");
        }else{
            return new HashMap();
        }

    }
    //通过订单号查询交易状态
    @RequestMapping("queryOrderStatus")
    public Result queryOrderStatus(String orderNo){
        int count =0;
        while(true){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            //得到调用接口的map数据
            Map<String,String> map = payService.queryOrderStatus(orderNo);
            if(map!=null){
                System.out.println(JSON.toJSONString(map));
                //根据返回的交易状态返回不同的result
                String status =map.get("orderStatus");
                if(StringUtils.isNotEmpty(status)){
                    if(status.equals("TRADE_SUCCESS")){
                        //调用payService的方法 将缓存订单入库
                        orderService.saveOrderFromRedis(userId,orderNo);
                        return new Result(true,"付款成功");
                    }else if(status.equals("TRADE_CLOSED")){
                        return new Result(false,"交易关闭");
                    }else if(status.equals("TRADE_FINISHED")){
                        return new Result(true,"交易结束");
                    }
                }
            }
            if(count==10){
                orderService.deleteOrderFromRedis(userId,orderNo);
                return new Result(false,"支付超时");
            }
        }

    }
}
