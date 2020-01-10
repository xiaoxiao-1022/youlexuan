package com.offcn.sms.service;


import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
@Service
public class smsListener implements MessageListener {

    @Autowired
    private SmsSendUtil smsSendUtil;

    @Override
    public void onMessage(Message message) {
        //从message里获取短信服务的四个信息
        ActiveMQMapMessage mapMessage = (ActiveMQMapMessage) message;
        try {
            String phone = mapMessage.getString("phone");
            String signName = mapMessage.getString("signName");
            String template = mapMessage.getString("template");
            String param = mapMessage.getString("param");
            //smsSendUtil.sendMsg(phone,signName,template,param);
        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("=============already send=======");
        }
    }
}
