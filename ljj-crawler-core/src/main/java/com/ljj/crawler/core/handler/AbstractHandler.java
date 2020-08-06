package com.ljj.crawler.core.handler;

import com.ljj.crawler.core.Task;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:59
 */
public interface AbstractHandler {

    /**
     * 处理一个任务，同样返回一个任务信息
     * 所有的任务基础信息，解析信息，待存储信息都属于任务
     *
     * @param task
     * @return
     */
    void handler(Task task);
}
