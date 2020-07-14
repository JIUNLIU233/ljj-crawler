package com.ljj.crawler.endpoint.extract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 13:26
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskRule {
    private Integer id; // 主键id
    private Integer taskId; // 对应的爬虫任务的id信息
    private String paramName; // task url规则，url中的参数
    private Integer ruleType; // 参数对应的格则类型，比如：0:递增。
    private String ruleParam; // 规则str

}
