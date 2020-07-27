package com.ljj.crawler.core.scheduler;


import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:08
 */
@Slf4j
public class QueueScheduler implements Scheduler {


    LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Task> extractInfos = new LinkedBlockingQueue<>();


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
    }

    @Override
    public ExtractInfo pollData() {
        return null;
    }
}
