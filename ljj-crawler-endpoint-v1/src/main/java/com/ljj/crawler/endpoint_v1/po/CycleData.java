package com.ljj.crawler.endpoint_v1.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleData {
    private long offset;        //  kafka offset
    private int partition;      //  kafka partition
    private String type;        //  数据业务类型
    private String data;        //  数据
    private String dataType;    //  数据的实际类型
}
