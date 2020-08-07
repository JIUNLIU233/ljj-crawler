package com.ljj.crawler.core.scheduler.impl;


import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 功能：
 *  单机版本的一个数据调度管理。
 *
 *  这里有一个问题，如何给taskInfo中配置任务信息。
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:08
 */
@Slf4j
public class LocalQueueScheduler implements Scheduler {


    LinkedBlockingQueue<Task> taskInfos = new LinkedBlockingQueue<>();      // 任务队列
    LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<>();    // 请求队列
    LinkedBlockingQueue<Task> extractInfos = new LinkedBlockingQueue<>();   // 解析队列
    LinkedBlockingQueue<Task> dataInfos = new LinkedBlockingQueue<>();      // 数据队列


    @Override
    public void pushTask(Task taskInfo) {
        log.debug("receive taskInfo >>> {}", JSON.toJSONString(taskInfo));
        try {
            taskInfos.put(taskInfo);
        } catch (InterruptedException e) {
            log.error("push taskInfo error >>> e:", e);
        }
    }

    @Override
    public Task pollTask() {
        return taskInfos.poll();
    }

    @Override
    public void pushRequest(Task request) {
        log.debug("receive request >>> {}", JSON.toJSONString(request));
        try {
            requests.put((Request) request);
        } catch (InterruptedException e) {
            log.error("push request error >>> e:", e);
        }
    }

    @Override
    public Task pollRequest() {
        return requests.poll();
    }

    @Override
    public void pushExtract(Task task) {
        log.debug("receive request >>> {}", JSON.toJSONString(task));
        try {
            extractInfos.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task pollExtract() {
        return extractInfos.poll();
    }

    @Override
    public void pushData(Task task) {
        ExtractInfo extractInfo = (ExtractInfo) task;
        log.info("receive data traceId={}, mount={},data={}", extractInfo.getTraceId(), extractInfo.getMount(), extractInfo.getResult());
        try {
            dataInfos.put(extractInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExtractInfo pollData() {
        Task poll = dataInfos.poll();
        return (ExtractInfo) poll;
    }
}
