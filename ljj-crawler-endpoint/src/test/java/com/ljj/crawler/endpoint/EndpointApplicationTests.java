package com.ljj.crawler.endpoint;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.handler.ExtractHandler;
import com.ljj.crawler.endpoint.extract.handler.TaskInfoHandler;
import com.ljj.crawler.endpoint.extract.scheduler.QueueScheduler;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.webspider.WRequest;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class EndpointApplicationTests {


    ExecutorService executorService = Executors.newFixedThreadPool(300);

    @Autowired
    private TaskInfoHandler taskInfoHandler;
    @Autowired
    private ExtractHandler extractHandler;

    private Scheduler scheduler = new QueueScheduler();

    public void init() {
        taskInfoHandler.init(scheduler);
        extractHandler.init(scheduler);
    }

    @Test
    void contextLoads() {


        init();

        start(4);
//        startOne(4);

        try {
            Thread.sleep(1000 * 60 * 60 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void start(int taskId) {
        // 启动任务
        taskInfoHandler.handler(new Task() {
            @Override
            public String getTaskId() {
                return String.valueOf(taskId);
            }

            @Override
            public void setTaskId(String tid) {

            }

            @Override
            public String getTraceId() {
                return null;
            }

            @Override
            public void setTraceId(String traceId) {

            }

            @Override
            public List<String> getParentTraceId() {
                return null;
            }

            @Override
            public void addParentTraceId(String traceId) {

            }
        });
        // 启动下载器

        new Thread(() -> {
            while (true) {
                Request request = scheduler.pollRequest();
                if (request == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                executorService.execute(() -> {
                    Response execute = WRequest.create(request).state(false).execute();
                    scheduler.pushResponse(execute);
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 启动解析器

        new Thread(() -> {
            while (true) {
                Response response = scheduler.pollResponse();
                if (response == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                extractHandler.handler(response);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startOne(int taskId) {
        taskInfoHandler.handler(new Task() {
            @Override
            public String getTaskId() {
                return String.valueOf(taskId);
            }

            @Override
            public void setTaskId(String tid) {

            }

            @Override
            public String getTraceId() {
                return null;
            }

            @Override
            public void setTraceId(String traceId) {

            }

            @Override
            public List<String> getParentTraceId() {
                return null;
            }

            @Override
            public void addParentTraceId(String traceId) {

            }
        });
        Request request = scheduler.pollRequest();
        Response response = WRequest.create(request).state(false).execute();
        extractHandler.handler(response);
        Request request1 = scheduler.pollRequest();
        response = WRequest.create(request1).state(false).execute();
        extractHandler.handler(response);
    }

}
