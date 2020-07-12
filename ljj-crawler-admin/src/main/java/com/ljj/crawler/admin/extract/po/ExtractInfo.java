package com.ljj.crawler.admin.extract.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 13:29
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExtractInfo {
    private Integer id; // 主键id
    private Integer taskId; // 解析绑定一个爬虫页面
    private String fieldName;// 要解析的字段，存储时的字段名称
    private String extractType;// 解析方式
    private String extractParam;//用于解析器的参数
    private String resultType;// 返回类型
    private String saveType;// 保存方式
    @Builder.Default
    private Integer extractFlag = 0;// 是否对其进行下一步解析 0:不需要，1需要
    private String extractUrlRule;// 进行下一步解析时，用于解析连接的规则

}
