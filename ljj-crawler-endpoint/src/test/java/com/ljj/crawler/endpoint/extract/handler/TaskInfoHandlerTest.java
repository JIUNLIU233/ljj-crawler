package com.ljj.crawler.endpoint.extract.handler;

import com.ljj.crawler.endpoint.extract.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
@SpringBootTest
class TaskInfoHandlerTest {

    @Autowired
    TaskInfoHandler taskInfoHandler;

    @Test
    void handler() {
        taskInfoHandler.handler(new Task() {
            @Override
            public String getTaskId() {
                return "2";
            }

            @Override
            public void setTaskId(String tid) {

            }

            @Override
            public String getTraceId() {
                return null;
            }

            @Override
            public void setTraceId(String traceId) {

            }

            @Override
            public List<String> getParentTraceId() {
                return null;
            }

            @Override
            public void addParentTraceId(String traceId) {

            }
        });
    }
}