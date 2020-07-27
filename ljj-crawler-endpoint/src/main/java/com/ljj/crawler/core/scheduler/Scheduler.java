package com.ljj.crawler.core.scheduler;

import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.webspider.http.Request;

/**
 * 功能：
 * 整个爬虫过程中，task request response extractInfo 的数据流动
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:00
 */
public interface Scheduler {

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


    void pushExtract(Task task);

    Task pollExtract();


    void pushData(Task extractInfo);

    Task pollData();
}
