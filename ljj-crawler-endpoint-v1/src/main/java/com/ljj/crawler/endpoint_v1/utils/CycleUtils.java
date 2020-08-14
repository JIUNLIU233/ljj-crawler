package com.ljj.crawler.endpoint_v1.utils;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.endpoint_v1.po.CycleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Component
public class CycleUtils {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${crawler.topic}")
    private String crawlerTopic;

    public void cycle(String type, String data, String dataType) {
        CycleData cycleData = new CycleData();
        cycleData.setType(type);
        cycleData.setData(data);
        cycleData.setDataType(dataType);
        kafkaTemplate.send(crawlerTopic, JSONObject.toJSONString(cycleData));
    }
}
