package com.ljj.crawler.endpoint.extract.downloader;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.model.TaskInfo;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public class Request implements Task {

    public static Request create(TaskInfo taskInfo) {
        return null;
    }

    @Override
    public String getTaskId() {
        return null;
    }

    @Override
    public void setTaskId(String tid) {

    }

    @Override
    public String getTraceId() {
        return null;
    }

    @Override
    public void setTraceId(String traceId) {

    }

    @Override
    public List<String> getParentTraceId() {
        return null;
    }

    @Override
    public void addParentTraceId(String traceId) {

    }
}
