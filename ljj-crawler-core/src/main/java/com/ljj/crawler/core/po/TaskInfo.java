package com.ljj.crawler.core.po;

import com.ljj.crawler.core.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能：任务信息配置
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:39
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo implements Task {
    private Integer id;         // 主键id
    private String traceId;     // 流程id
    private String name;        // 爬虫任务名称
    private String startUrl;    //
    private String comment;     // 爬虫任务描述



    public TaskInfo(TaskInfo taskInfo) {
        this.id = taskInfo.getId();
        this.traceId = taskInfo.getTraceId();
        this.name = taskInfo.getName();
        this.startUrl = taskInfo.getStartUrl();
        this.comment = taskInfo.getComment();
    }

    @Override
    public String getTid() {
        return String.valueOf(id);
    }

    @Override
    public String getPId() {
        return null;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public List<String> getPTraceId() {
        return null;
    }
}
