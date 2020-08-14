package com.ljj.crawler.webspider.utils;

import org.junit.jupiter.api.Test;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
class ResContentUtilTest {
    @Test
    public void testJSONP() throws Exception {
        String content = "call_back({\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"},\"links\":[[\"Google\",\"http://www.google.com\"],[\"Baidu\",\"http://www.baidu.com\"],[\"SoSo\",\"http://www.SoSo.com\"]]});";
        String s = ResContentUtil.JSONPData(content);
        System.out.println(s);
    }

}