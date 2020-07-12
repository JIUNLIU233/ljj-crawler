package com.ljj.crawler.admin.extract.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 13:25
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo {
    private Integer id; // 主键id
    private String name; // 爬虫任务名称
    private String startUrl; // 爬虫起始url
    @Builder.Default
    private Integer haveRule = 0; // 爬虫起始url是否有规则 0:无规则，1：有规则
    @Builder.Default
    private Integer status = 0; //0:未运行， 1:正在初始化，2：正在运行，3：挂起状态


    public TaskInfo(TaskInfo taskInfo) {
        this.id = taskInfo.getId();
        this.name = taskInfo.getName();
        this.haveRule = taskInfo.getHaveRule();
        this.status = taskInfo.getStatus();
        this.startUrl = taskInfo.getStartUrl();
    }
}
