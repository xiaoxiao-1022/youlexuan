package com.offcn.pay.service;

import java.util.Map;

public interface PayService {

    Map createPayUrl(long orderNo, String s);

    Map queryOrderStatus(String orderNo);

}
