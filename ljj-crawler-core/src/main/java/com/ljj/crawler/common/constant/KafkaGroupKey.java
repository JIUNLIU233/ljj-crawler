package com.ljj.crawler.common.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
public class KafkaGroupKey {
    public static final String taskKey = "task";
    public static final String downloadKey = "download";
    public static final String extractKey = "extract";
    public static final String dataKey = "data";

    public static final List<String> getAllKey(){
        return new ArrayList<String >(){{
            add(taskKey);
            add(downloadKey);
            add(extractKey);
            add(dataKey);
        }};
    }
}
