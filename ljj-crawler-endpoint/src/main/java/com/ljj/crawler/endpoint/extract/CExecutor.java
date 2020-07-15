package com.ljj.crawler.endpoint.extract;

import com.ljj.crawler.endpoint.core.AppContext;
import com.ljj.crawler.endpoint.extract.handler.ExtractHandler;
import com.ljj.crawler.endpoint.extract.handler.TaskInfoHandler;
import com.ljj.crawler.endpoint.extract.scheduler.QueueScheduler;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;

/**
 * 功能：
 * 整体的启动类
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 20:56
 */
public class CExecutor implements Runnable {
    private Scheduler scheduler = new QueueScheduler();
    private TaskInfoHandler taskInfoHandler;
    private ExtractHandler extractHandler;

    public void init() {
        taskInfoHandler = AppContext.getBean(TaskInfoHandler.class);
        taskInfoHandler.init(scheduler);
        extractHandler = AppContext.getBean(ExtractHandler.class);
    }

    @Override
    public void run() {
        /**
         * 下载器执行
         */
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                Response response = scheduler.pollResponse();
                extractHandler.handler(response);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
