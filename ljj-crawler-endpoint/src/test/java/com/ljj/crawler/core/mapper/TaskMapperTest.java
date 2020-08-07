package com.ljj.crawler.core.mapper;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@SpringBootTest
class TaskMapperTest {

    @Resource
    TaskMapper taskMapper;

    @Test
    void findById() {
        TaskInfo byId = taskMapper.findById(1);
        System.out.println(JSON.toJSONString(byId));
    }
}