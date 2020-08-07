package com.ljj.crawler.core.scheduler.impl;

import com.ljj.crawler.EndpointApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Slf4j
@SpringBootTest(classes = EndpointApplication.class)
class KafkaSchedulerTest {

    @Autowired
    private KafkaScheduler kafkaScheduler;

    @Test
    public void test() throws Exception {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

        for (int i = 0; i < 3; i++) {
            log.info("put start>>> {}", i);
            queue.put(i + "");
            log.info("put end>>> {}", i);
        }

    }

    @Test
    public void listener() {

        System.out.println("");
    }
}