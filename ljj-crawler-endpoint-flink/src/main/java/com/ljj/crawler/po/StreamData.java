package com.ljj.crawler.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamData implements Serializable {
    private String receive;     // 定义哪个模块处理
    private String data;        // 实际的数据信息
    private String dataType;    // 数据的类型
}
