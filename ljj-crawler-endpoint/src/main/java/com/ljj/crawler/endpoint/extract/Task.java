package com.ljj.crawler.endpoint.extract;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public interface Task {

    /**
     * 唯一的，爬虫任务规则的id
     *
     * @return
     */
    String getTaskId();

    /**
     * 唯一的，爬虫任务规则的id
     *
     * @param tid
     */
    void setTaskId(String tid);

    /**
     * 执行流程中，用于绑定关系的流程id
     *
     * @return
     */
    String getTraceId();

    void setTraceId(String traceId);

    /**
     * 子任务中，存储父任务的流程id信息
     *
     * @return
     */
    List<String> getParentTraceId();

    void addParentTraceId(String traceId);
}
