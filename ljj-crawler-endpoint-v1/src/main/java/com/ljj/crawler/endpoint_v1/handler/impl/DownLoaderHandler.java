package com.ljj.crawler.endpoint_v1.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.endpoint_v1.handler.AbstractHandler;
import com.ljj.crawler.endpoint_v1.po.CReceive;
import com.ljj.crawler.endpoint_v1.po.CycleData;
import com.ljj.crawler.endpoint_v1.utils.CycleUtils;
import com.ljj.crawler.webspider.WRequest;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.Semaphore;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Component
@Slf4j
public class DownLoaderHandler implements AbstractHandler {
    @Override
    public void handler(CycleData cycleData, CycleUtils cycleUtils, Semaphore semaphore) {
        try {
            log.info("download process start >>> offset={}", cycleData.getOffset());
            Request request = JSONObject.parseObject(cycleData.getData(), Request.class);
            Response execute = WRequest.create(request).execute();

            // TODO 请求失败的相关处理
            ExtractInfo extractInfo = ExtractInfo.create(request);
            extractInfo.setResult(execute.getResponseBody());
            extractInfo.setResultBytes(execute.getResponseBytes());

            URL currentURL = execute.getCurrentURL();
            if (currentURL != null) {
                extractInfo.setCurUrl(currentURL.toString());
            }

            String data = JSONObject.toJSONString(extractInfo);
            cycleUtils.cycle(CReceive.extractHandlerKey,
                    data,
                    CReceive.extractHandlerKey);
            log.info("download process cycle >>> offset={},data={}", cycleData.getOffset(), data);
        } catch (Exception e) {
            log.info("download process error >>> offset={},e:", cycleData.getOffset(), e);
        }
    }
}
