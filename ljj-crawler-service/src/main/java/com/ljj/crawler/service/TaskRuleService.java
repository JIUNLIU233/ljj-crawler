package com.ljj.crawler.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.core.po.TaskRule;
import com.ljj.crawler.mapper.RuleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/5 21:57
 */
@Service
public class TaskRuleService {

    @Resource
    private RuleMapper ruleMapper;


    public void importRule(JSONArray rules, Integer tid) {
        if (rules != null && rules.size() > 0)
            for (int i = 0; i < rules.size(); i++) {
                JSONObject ruleObj = rules.getJSONObject(i);
                TaskRule taskRule = ruleObj.toJavaObject(TaskRule.class);
                taskRule.setTid(tid);
                ruleMapper.insertOne(taskRule);
            }
    }

    public List<TaskRule> findByTid(int tid){
        return ruleMapper.findByTid(tid);
    }
}
