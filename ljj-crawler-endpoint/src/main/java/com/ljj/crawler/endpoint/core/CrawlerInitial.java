package com.ljj.crawler.endpoint.core;

import com.ljj.crawler.endpoint.extract.handler.DownloadHandler;
import com.ljj.crawler.endpoint.extract.handler.ExtractHandler;
import com.ljj.crawler.endpoint.extract.handler.TaskInfoHandler;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.scheduler.QueueScheduler;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 爬虫整体容器的初始化
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Component
public class CrawlerInitial {

    private TaskInfoHandler taskInfoHandler;
    private ExtractHandler extractHandler;
    private DownloadHandler downloadHandler;
    private Scheduler scheduler = new QueueScheduler();

    @Autowired
    public CrawlerInitial(TaskInfoHandler taskInfoHandler, ExtractHandler extractHandler, DownloadHandler downloadHandler) {
        this.taskInfoHandler = taskInfoHandler;
        this.extractHandler = extractHandler;
        this.downloadHandler = downloadHandler;
    }


    /**
     * 各部分模块开始运行
     * taskHandler通过人为触发或者通过调度器进行触发
     *
     * @param sc
     */
    public void start(Scheduler sc) {
        this.scheduler = sc == null ? this.scheduler : sc;
        taskInfoHandler.init(this.scheduler);


        Thread downloadThread = new Thread(() -> {
            downloadHandler.init(this.scheduler);
            while (true) {
                Request request = this.scheduler.pollRequest();
                if (request == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else {
                    downloadHandler.handler(request);
                }
            }
        });

        Thread extractThread = new Thread(() -> {
            extractHandler.init(this.scheduler);
            while (true) {
                ExtractInfo extractInfo = this.scheduler.pollExtract();
                if (extractInfo == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else {
                    extractHandler.handler(extractInfo);
                }
            }
        });

        downloadThread.setName("downloader thread");
        extractThread.setName("extract thread");
        downloadThread.start();
        extractThread.start();

    }
}
