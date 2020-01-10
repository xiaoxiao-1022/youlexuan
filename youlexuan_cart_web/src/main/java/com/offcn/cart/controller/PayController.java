package com.offcn.cart.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.order.service.OrderService;
import com.offcn.pay.service.PayService;
import com.offcn.pojo.TbPayLog;
import com.offcn.pojo.entity.Result;
import com.offcn.pojo.group.CartGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("pay")
public class PayController {

    @Reference(timeout = 10000)
    private PayService payService;

    @Reference
    private OrderService orderService;


    //生成二维码的方法
    @RequestMapping("createPayUrl")
    public Map createPayUrl(){
        //获取当前登陆用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.findPayLogFromRedis(userId);
        //转换下单金额按照元
        long total =payLog.getTotalFee();
        BigDecimal bigTotal = BigDecimal.valueOf(total);
        BigDecimal cs = BigDecimal.valueOf(100d);
        BigDecimal bigYuan = bigTotal.divide(cs);
        System.out.println("预下单金额:"+bigYuan.doubleValue()+"");
        return payService.createPayUrl(Long.parseLong(payLog.getOutTradeNo()),bigYuan.doubleValue()+"");
    }
    //通过订单号查询交易状态
    @RequestMapping("queryOrderStatus")
    public Result queryOrderStatus(String orderNo){
        System.out.println("orderNo="+orderNo);
        int count =0;
        while(true){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            //得到调用接口的map数据
            Map<String,String> map = payService.queryOrderStatus(orderNo);
            if(map!=null){
                System.out.println(JSON.toJSONString(map));
                //根据返回的交易状态返回不同的result
                String status =map.get("orderStatus");
                if(StringUtils.isNotEmpty(status)){
                    if(status.equals("TRADE_SUCCESS")){
                        //调用payService的方法 将支付的订单更改
                        orderService.updatePayLogStatus(orderNo);
                        return new Result(true,"付款成功");
                    }else if(status.equals("TRADE_CLOSED")){
                        return new Result(false,"交易关闭");
                    }else if(status.equals("TRADE_FINISHED")){
                        return new Result(true,"交易结束");
                    }
                }
            }
            if(count==10){
                return new Result(false,"支付超时");
            }
        }

    }
}
