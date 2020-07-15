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

@SpringBootTest
class EndpointApplicationTests {

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
        taskInfoHandler.handler(new Task() {
            @Override
            public String getTaskId() {
                return "3";
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
        Response response = WRequest.create(request).execute();
        extractHandler.handler(response);


        System.out.println("");
    }

}
