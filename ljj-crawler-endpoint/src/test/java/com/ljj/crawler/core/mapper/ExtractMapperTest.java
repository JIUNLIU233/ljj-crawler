package com.ljj.crawler.core.mapper;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.ExtractInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@SpringBootTest
class ExtractMapperTest {
    @Resource
    ExtractMapper extractMapper;

    @Test
    void findByTid() {
        List<ExtractInfo> byTid = extractMapper.findByTid(1);
        System.out.println(JSON.toJSONString(byTid));
    }
    @Test
    void findByPid(){
        List<ExtractInfo> byPid = extractMapper.findByPid(1);
        System.out.println(JSON.toJSONString(byPid));
    }
}