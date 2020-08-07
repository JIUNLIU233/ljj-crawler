package com.ljj.crawler.core.scheduler;

import com.ljj.crawler.core.Task;
import com.ljj.crawler.webspider.http.Request;

/**
 * 功能：
 * 整个爬虫过程中，task request response extractInfo 的数据流动
 * <p>
 * 可通过不同的实现类，进行一个单机版本和分布式版本的抓取。
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:00
 */
public interface Scheduler {


    /**
     * 触发一个任务的执行，将任务发送到触发队列中。
     *
     * @param taskInfo
     */
    void pushTask(Task taskInfo);

    /**
     * 获取一个任务
     *
     * @return
     */
    Task pollTask();

    /**
     * push request 到request 管理容器中
     * 用于下载器
     *
     * @param request
     */
    void pushRequest(Task request);

    /**
     * 从request 管理容器中 poll一个request 下来
     * 用于下载器
     *
     * @return
     */
    Task pollRequest();


    /**
     * 将解析push到解析管理容器中
     *
     * @param task
     */
    void pushExtract(Task task);

    /**
     * 解析器获取解析任务
     *
     * @return
     */
    Task pollExtract();


    /**
     * 将数据push到数据管理容器中
     *
     * @param extractInfo
     */
    void pushData(Task extractInfo);

    /**
     * 数据中心获取数据，进行存储，
     *
     * @return
     */
    Task pollData();
}
