package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
@Service
public class payServiceImpl implements PayService {

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public Map createPayUrl(long orderNo, String totalFee) {
        //响应前端的数据
        Map map = null;
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+orderNo+"\"," +//商户订单号
                "    \"total_amount\":\""+totalFee+"\"," +
                "    \"subject\":\"优乐选测试\"," +
                "    \"store_id\":\"NJ_001\"," +
                "    \"timeout_express\":\"90m\"}");//订单允许的最晚付款时间
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
            System.out.print("=======response.getbody:"+response.getBody());

            //返回支付的url地址
            if(response.getCode().equals("10000")){
                map = new HashMap();
                //返回订单的金额
                map.put("money",totalFee);
                //返回订单的编号
                map.put("orderNo",orderNo+"");
                map.put("qrCode",response.getQrCode());
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;
    }

    //根据订单查询支付宝的接口得到状态
    @Override
    public Map queryOrderStatus(String orderNo) {
        Map<String,String> map = new HashMap();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\""+orderNo+"\"" +
                "  }");
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            System.out.println(JSON.toJSONString(response));
            System.out.println(response.getBody()+"------------query status");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("execute success");
            System.out.println(response);
            map.put("orderStatus",response.getTradeStatus());
            return map;
        } else {
            System.out.println("调用失败");
        }
        return map;
    }
}
