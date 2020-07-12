package com.ljj.crawler.admin.extract.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 16:24
 */
@SpringBootTest
class TaskHandlerTest {

    @Autowired
    private TaskHandler taskHandler;

    @Test
    void initHandler() {
        taskHandler.initHandler(1);
    }
}