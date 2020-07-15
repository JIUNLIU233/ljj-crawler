package com.ljj.crawler.endpoint.extract.webspider.http;


import com.ljj.crawler.endpoint.extract.Task;
import org.apache.http.client.CookieStore;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http 返回体封装
 *
 * @author JIUN·LIU
 * @data 2020/2/12 13:58
 */
public class Response implements Task {
    // 任务id，用于全局同一条任务标识
    private String taskId;
    // http 返回状态码
    private Integer statusCode;
    // http 返回头信息
    private Map<String, String> headers;

    private CookieStore cookieStore;

    // http 返回体 字符串格式
    private String responseBody;
    // http 返回信息 流格式
    private OutputStream responseStream;
    // http 返回信息 二进制数据
    private byte[] responseBytes;

    //
    private String charset;
    private URL currentURL;
    // 用于重定向的location
    private String location;

    private List<String> historyUrls;


    /**
     * 用于流程控制的参数
     *
     * @return
     */

    private String traceId;
    private List<String> parentTraceId = new ArrayList<>();

    public Response() {
    }

    public Response(Request request) {
        this.taskId = request.getTaskId();
        this.traceId = request.getTraceId();
        this.parentTraceId = request.getParentTraceId();
    }

    public Response(Response response) {
        this.taskId = response.getTaskId();
        this.traceId = response.getTraceId();
        this.parentTraceId = response.getParentTraceId();
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();

        this.cookieStore = response.getCookieStore();
        this.charset = response.getCharset();
        this.currentURL = response.getCurrentURL();
        this.location = response.getLocation();
        this.historyUrls = response.getHistoryUrls();
    }

    public URL getCurrentURL() {
        return currentURL;
    }

    public void setCurrentURL(URL currentURL) {
        this.currentURL = currentURL;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }


    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }


    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public OutputStream getResponseStream() {
        return responseStream;
    }

    public void setResponseStream(OutputStream responseStream) {
        this.responseStream = responseStream;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTraceId() {
        return this.traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public List<String> getParentTraceId() {
        return this.parentTraceId;
    }

    @Override
    public void addParentTraceId(String traceId) {
        this.parentTraceId.add(traceId);
    }


    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getHistoryUrls() {
        return historyUrls;
    }

    public void setHistoryUrls(List<String> historyUrls) {
        this.historyUrls = historyUrls;
    }
}


