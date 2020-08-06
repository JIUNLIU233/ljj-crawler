package com.ljj.crawler.core.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 *  任务规则实体
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:42
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskRule {
    private Integer id;         // 主键id
    private Integer tid;        // 任务id
    private String field;       // 有规则的字段
    private Integer ruleType;   // 规则类型 1：递增，
    private String ruleParam;   // 用于规则的参数
}
