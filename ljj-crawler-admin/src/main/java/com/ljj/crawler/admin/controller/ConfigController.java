package com.ljj.crawler.admin.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用作任务配置的controller
 * Create by JIUN·LIU
 * Create time 2020/8/5
 **/
@CrossOrigin
@RestController
@RequestMapping("/config")
public class ConfigController {

    @PostMapping(value = "add")
    public ResponseEntity<JSONObject> config(@RequestBody String data) {
        System.err.println(data);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
