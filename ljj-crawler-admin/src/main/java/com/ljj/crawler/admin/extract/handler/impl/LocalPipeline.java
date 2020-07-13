package com.ljj.crawler.admin.extract.handler.impl;

import com.ljj.crawler.admin.extract.handler.Pipeline;
import com.ljj.crawler.admin.extract.po.ExtractInfo;
import com.ljj.crawler.admin.extract.po.TaskInfo;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/13
 **/
public class LocalPipeline implements Pipeline {

    public static LinkedBlockingDeque<TaskInfo> taskInfos = new LinkedBlockingDeque<>();
    public static LinkedBlockingDeque<ExtractInfo> extractInfos = new LinkedBlockingDeque<>();

    @Override
    public void pushTask(TaskInfo taskInfo) {
        taskInfos.push(taskInfo);
    }

    @Override
    public TaskInfo pullTask() {
        return taskInfos.poll();
    }

    @Override
    public void pushExtract(ExtractInfo extractInfo) {
        extractInfos.push(extractInfo);
    }

    @Override
    public ExtractInfo pullExtract() {
        return extractInfos.poll();
    }
}
