package com.ljj.crawler;

import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.scheduler.QueueScheduler;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.extract.download.DownloadHandler;
import com.ljj.crawler.data.DataMongoHandler;
import com.ljj.crawler.extract.handler.ExtractHandler;
import com.ljj.crawler.extract.handler.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 爬虫整体容器的初始化
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Component
@Slf4j
public class CrawlerInitial {
    private TaskHandler taskHandler;
    private ExtractHandler extractHandler;
    private DownloadHandler downloadHandler;
    private DataMongoHandler dataMongoHandler;
    private Scheduler scheduler = new QueueScheduler();

    @Autowired
    public CrawlerInitial(TaskHandler taskHandler, ExtractHandler extractHandler, DownloadHandler downloadHandler, DataMongoHandler dataMongoHandler) {
        this.taskHandler = taskHandler;
        this.extractHandler = extractHandler;
        this.downloadHandler = downloadHandler;
        this.dataMongoHandler = dataMongoHandler;
    }

    public void start() {
        start(null);
    }


    /**
     * 各部分模块开始运行
     * taskHandler通过人为触发或者通过调度器进行触发
     *
     * @param sc
     */
    public void start(Scheduler sc) {
        this.scheduler = sc == null ? this.scheduler : sc;
        log.info("init crawler container >>> scheduler={}", this.scheduler);
        taskHandler.init(this.scheduler);


        /**
         * 下载
         */
        Thread downloadThread = new Thread(() -> {
            downloadHandler.init(this.scheduler);
            while (true) {
                Task request = this.scheduler.pollRequest();
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

        /**
         * 解析
         */
        Thread extractThread = new Thread(() -> {
            extractHandler.init(this.scheduler);
            while (true) {
                Task extractInfo = this.scheduler.pollExtract();
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
        Thread dataSaveThread = new Thread(() -> {
            while (true) {
                Task extractInfo = this.scheduler.pollData();
                if (extractInfo == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else {
                    dataMongoHandler.handler(extractInfo);
                }
            }
        });

        downloadThread.setName("downloader thread");
        extractThread.setName("extract thread");
        dataSaveThread.setName("data mongo thread");
        downloadThread.start();
        log.info("downloader start >>> successful");
        extractThread.start();
        log.info("extract handler start >>> successful");

        dataSaveThread.start();
        log.info("data mongo handler start >>> successful");


    }
}
