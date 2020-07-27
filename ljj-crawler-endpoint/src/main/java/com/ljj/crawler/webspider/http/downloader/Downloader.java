package com.ljj.crawler.webspider.http.downloader;


import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;

/**
 * @author JIUN·LIU
 * @data 2020/2/12 14:18
 */
public interface Downloader {
    Response download(Request request);
}
