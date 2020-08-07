package com.ljj.crawler.core.service;

import com.alibaba.fastjson.JSONArray;
import com.ljj.crawler.service.ExtractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/4
 **/
@SpringBootTest
class ExtractServiceTest {

    @Autowired
    private ExtractService extractService;

    @Test
    void exportByTid() {
        JSONArray objects = extractService.exportByTid(1);
        System.out.println(objects);
    }

    @Test
    void importConfig() {
        String config = "[{\"contentType\":3,\"content\":\"http://www.czccb.cn/news/gywm/index.html\",\"child\":[{\"selector\":\".yw-content_zw\",\"mount\":\"bank_base_info.traceId.intro\"}]},{\"contentType\":3,\"content\":\"http://www.czccb.cn/templets/czbank/images/logo.gif\",\"child\":[{\"contentType\":5,\"mount\":\"bank_base_info.traceId.logo\"}]},{\"contentType\":4,\"mount\":\"bank_base_info.traceId.siteUrl\",\"content\":\"http://www.czccb.cn/\"}]";
        extractService.importConfig(JSONArray.parseArray(config), 5);
    }
}