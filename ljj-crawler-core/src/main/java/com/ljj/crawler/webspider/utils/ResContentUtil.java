package com.ljj.crawler.webspider.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取content的工具
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
public class ResContentUtil {

    private static final Pattern JSONP = Pattern.compile(".*?(\\{.*\\}).");

    /**
     * 从response jsonp 数据形式中，获取json数据
     *
     * @param content
     * @return
     */
    public static String JSONPData(String content) {
        Matcher matcher = JSONP.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return content;
    }
}
