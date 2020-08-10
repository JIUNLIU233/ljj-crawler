package com.ljj.crawler.core.scheduler.impl;

import com.ljj.crawler.EndpointApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/7 21:20
 */
@SpringBootTest(classes = EndpointApplication.class)
public class KafkaProducerTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void test() {
        String data = "{\"receive\":\"task\",\"data\":{\"id\":4},\"dataType\":\"task\"}";
        kafkaTemplate.send("ljj_test", data);
    }
}
