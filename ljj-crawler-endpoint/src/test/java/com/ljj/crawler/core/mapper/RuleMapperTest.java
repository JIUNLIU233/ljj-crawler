package com.ljj.crawler.core.mapper;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.TaskRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@SpringBootTest
public class RuleMapperTest {
    @Resource
    RuleMapper ruleMapper;

    @Test
    public void findByTid() {
        List<TaskRule> byTid = ruleMapper.findByTid(1);
        System.out.println(JSON.toJSONString(byTid));
    }
}