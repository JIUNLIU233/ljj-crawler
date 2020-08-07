package com.ljj.crawler.contant;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于接收划分组的常量
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
public class CReceive {
    public static final String taskHandlerKey = "task";
    public static final String downloadHandlerKey = "download";
    public static final String extractHandlerKey = "extract";
    public static final String dataHandlerKey = "data";


    public static List<String> cReceives = new ArrayList<String>() {{
        add(taskHandlerKey);
        add(downloadHandlerKey);
        add(extractHandlerKey);
        add(dataHandlerKey);
    }};
}
