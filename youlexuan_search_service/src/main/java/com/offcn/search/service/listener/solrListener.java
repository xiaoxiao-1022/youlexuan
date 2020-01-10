package com.offcn.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class solrListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        try {
            String itemStr = ((ActiveMQTextMessage) message).getText();
            List<TbItem> itemList = JSON.parseArray(itemStr, TbItem.class);
            //遍历集合存储到solr索引库
            for (TbItem item : itemList) {
                Map newMap = new HashMap();
                //循环的每个item中的spec字符串 转换成map用于生成多个动态域
                Map map = JSON.parseObject(item.getSpec(), Map.class);
                //获取规格map中的key 中文
                Set<String> set = map.keySet();
                for (String key : set) {
                    newMap.put(Pinyin.toPinyin(key,"").toLowerCase(),map.get(key));
                }
                item.setSpecMap(newMap);
            }
            System.out.println(itemList);
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
            System.out.println("=======solr-queue=========save success========");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
