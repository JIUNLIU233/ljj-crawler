package com.ljj.crawler.extract;

import com.ljj.crawler.CrawlerInitial;
import com.ljj.crawler.EndpointApplication;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.extract.handler.TaskHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@SpringBootTest(classes = EndpointApplication.class)
class CrawlerInitialTest {

    @Autowired
    CrawlerInitial crawlerInitial;
    @Autowired
    TaskHandler taskHandler;

    @Test
    void start() throws Exception {
        crawlerInitial.start();
        taskHandler.handler(new Task() {
            @Override
            public String getTid() {
                return "2";
            }

            @Override
            public String getPId() {
                return null;
            }

            @Override
            public String getTraceId() {
                return null;
            }

            @Override
            public List<String> getPTraceId() {
                return null;
            }
        });

        Thread.sleep(1000 * 60 * 60);
    }
}