package com.ljj.crawler.core.mapper;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.TaskInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

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