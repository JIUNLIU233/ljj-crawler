package com.ljj.crawler.endpoint_v1.handler;

import com.ljj.crawler.endpoint_v1.po.CycleData;
import com.ljj.crawler.endpoint_v1.utils.CycleUtils;

import java.util.concurrent.Semaphore;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
public interface AbstractHandler {

    void handler(CycleData cycleData, CycleUtils cycleUtils, Semaphore semaphore);
}
