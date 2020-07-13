package com.ljj.crawler.core.utils;

import java.util.UUID;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/13
 **/
public class TraceUtil {

    public static String traceId() {
        return UUID.randomUUID().toString();
    }
}
