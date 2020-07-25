package com.ljj.crawler.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/22 20:41
 */
@RestController
public class TestControlelr {

    @RequestMapping("test")
    public String test(){
        return "test";
    }
}
