package com.offcn.search.service.listener;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
@Service
public class solrDeleteListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //获取集合的字符串
        try {
            String idStr = ((ActiveMQTextMessage) message).getText();
            System.out.println("============="+idStr);
            List<Long> ids = JSON.parseArray(idStr, Long.class);
            //根据集合id调用模板的删除索引库数据
            SimpleQuery query = new SimpleQuery();
            //创建根据id查询的条件
            Criteria criteria = new Criteria("item_goodsid").in(ids);
            //将id为条件的查询对象交给模板执行删除
            query.addCriteria(criteria);
            solrTemplate.delete(query);
            solrTemplate.commit();
            System.out.println("========solr queue==========delete success========");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
