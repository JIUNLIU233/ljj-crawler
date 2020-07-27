package com.ljj.crawler.extract.download;

import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.handler.AbstractHandler;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.webspider.WRequest;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 17:57
 */
@Component
public class DownloadHandler implements AbstractHandler {
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
            ExtractInfo extractInfo = ExtractInfo.create(task);
            extractInfo.setResult(execute.getResponseBody());
            extractInfo.setResultBytes(execute.getResponseBytes());
            this.scheduler.pushExtract(extractInfo);
        }
    }
}
