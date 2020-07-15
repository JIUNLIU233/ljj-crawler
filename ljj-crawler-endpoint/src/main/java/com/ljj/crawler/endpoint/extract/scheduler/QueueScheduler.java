package com.ljj.crawler.endpoint.extract.scheduler;

import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:08
 */
public class QueueScheduler implements Scheduler {


    private BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<ExtractInfo> extractQueue = new LinkedBlockingQueue<>();


    @Override
    public void pushRequest(Request request) {
        requestQueue.add(request);
    }

    @Override
    public Request pollRequest() {
        return requestQueue.poll();
    }

    @Override
    public void pushResponse(Response response) {
        responseQueue.add(response);
    }

    @Override
    public Response pollResponse() {
        return responseQueue.poll();
    }

    @Override
    public void pushExtract(ExtractInfo extractInfo) {
        extractQueue.add(extractInfo);
    }

    @Override
    public ExtractInfo pollExtract() {
        return extractQueue.poll();
    }
}
