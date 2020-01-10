package com.offcn.page.service.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.page.service.ItemPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;


@Service
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService pageService;


    @Override
    public void onMessage(Message message) {
        //获取集合的字符串
        try {
            String idStr = ((ActiveMQTextMessage)message).getText();
            List<Long> ids = JSON.parseArray(idStr,Long.class);
            for (Long id : ids) {
                pageService.createItemHtml(id);
            }
            System.out.println("-------page--html-----create success-------");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
