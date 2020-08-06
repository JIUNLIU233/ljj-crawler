package com.ljj.crawler.core.service;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.core.mapper.TaskMapper;
import com.ljj.crawler.core.po.TaskInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/5 21:52
 */
@Service
public class TaskService {

    @Resource
    private TaskMapper taskMapper;


    public TaskInfo importTask(JSONObject task) {
        TaskInfo taskInfo = task.toJavaObject(TaskInfo.class);
        taskMapper.insertOne(taskInfo);
        return taskInfo;
    }
}
