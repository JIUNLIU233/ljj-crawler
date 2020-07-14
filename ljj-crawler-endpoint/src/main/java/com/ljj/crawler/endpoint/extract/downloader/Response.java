package com.ljj.crawler.endpoint.extract.downloader;

import com.ljj.crawler.endpoint.extract.Task;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public class Response implements Task {


    public Response(Response response) {
    }

    @Override
    public String getTaskId() {
        return null;
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

    public byte[] getBody() {
        return null;
    }

    public void setBody(byte[] bytes){

    }
}
