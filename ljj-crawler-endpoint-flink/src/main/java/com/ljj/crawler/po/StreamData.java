package com.ljj.crawler.po;

import lombok.Data;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Data
public class StreamData {
    private String receive;     // 定义哪个模块处理
    private String data;        // 实际的数据信息
    private String dataType;    // 数据的类型
}
