package com.ljj.crawler.admin.extract.handler;

import com.ljj.crawler.admin.extract.po.TaskInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 17:40
 */
@SpringBootTest
class ExtractHandlerTest {

    @Autowired
    private ExtractHandler extractHandler;

    @Test
    void handler() {
        extractHandler.handler(new TaskInfo() {{
            setId(2);
        }});
    }
}