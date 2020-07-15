package com.ljj.crawler.endpoint.extract.scheduler;

import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;

/**
 * 功能：
 * 整个爬虫过程中，task request response extractInfo 的数据流动
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:00
 */
public interface Scheduler {

    /**
     * push request 到request 管理容器中
     *
     * @param request
     */
    void pushRequest(Request request);

    /**
     * 从request 管理容器中 poll一个request 下来
     *
     * @return
     */
    Request pollRequest();

    /**
     * push response 用于extractHandler来处理
     *
     * @param response
     */
    void pushResponse(Response response);

    /**
     * 用于extractHandler获取response进行处理
     *
     * @return
     */
    Response pollResponse();


    void pushExtract(ExtractInfo extractInfo);

    ExtractInfo pollExtract();
}
