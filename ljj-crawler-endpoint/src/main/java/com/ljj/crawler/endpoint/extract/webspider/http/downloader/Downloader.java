package com.ljj.crawler.endpoint.extract.webspider.http.downloader;


import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;

/**
 * @author JIUN·LIU
 * @data 2020/2/12 14:18
 */
public interface Downloader {
    Response download(Request request);
}
