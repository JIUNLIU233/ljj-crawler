package com.ljj.crawler.endpoint.extract.handler;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.webspider.WRequest;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Component
public class DownloadHandler implements Handler {

    private Scheduler scheduler;

    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Async
    @Override
    public void handler(Task task) {
        if (task instanceof Request) {
            Request request = (Request) task;
            Response execute = WRequest.create(request).execute();

            /**
             * request 的生成方式
             * 1、通过 task_info 规则来生成
             *      利用 task_info 生成request的时候，request 含有 taskId，
             *      但是不具备 extract_info 的pid信息，此时的request只能给设置task_id
             *
             * 2、通过 extract_info 解析过程中生成
             */
            // TODO  init 一个 extract 通过这个task
            ExtractInfo extractInfo = new ExtractInfo();
            extractInfo.setContent(execute.getResponseBody());

            this.scheduler.pushExtract(extractInfo);
        }
    }
}
