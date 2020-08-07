package com.ljj.crawler.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.constant.ConfigConstant;
import com.ljj.crawler.core.po.TaskInfo;

import com.ljj.crawler.service.ExtractService;
import com.ljj.crawler.service.TaskRuleService;
import com.ljj.crawler.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用作任务配置的controller
 * Create by JIUN·LIU
 * Create time 2020/8/5
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/config")
public class ConfigController {

    private TaskService taskService;
    private TaskRuleService ruleService;
    private ExtractService extractService;

    @Autowired
    public ConfigController(TaskService taskService, TaskRuleService ruleService, ExtractService extractService) {
        this.taskService = taskService;
        this.ruleService = ruleService;
        this.extractService = extractService;
    }

    @PostMapping(value = "add")
    public ResponseEntity<JSONObject> config(@RequestBody String data) {
        log.info("config >>> request:{}", data);
        JSONObject configObj = JSONObject.parseObject(data);
        JSONObject taskObj = configObj.getJSONObject(ConfigConstant.taskConfigKey);
        TaskInfo taskInfo = taskService.importTask(taskObj);
        JSONArray ruleArray = configObj.getJSONArray(ConfigConstant.ruleConfigKey);
        ruleService.importRule(ruleArray, taskInfo.getId());
        JSONArray extractArray = configObj.getJSONArray(ConfigConstant.extractConfigKey);
        extractService.importConfig(extractArray, taskInfo.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
