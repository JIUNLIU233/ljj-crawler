package com.ljj.crawler.function;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.webspider.WRequest;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.net.URL;
import java.util.Collections;

/**
 * 异步下载中心
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Slf4j
public class DownloadAsynProcess extends RichAsyncFunction<StreamData, String> {
    @Override
    public void asyncInvoke(StreamData streamData, ResultFuture<String> resultFuture) throws Exception {
        String receive = streamData.getReceive();
        String dataType = streamData.getDataType();
        if (CReceive.downloadHandlerKey.equalsIgnoreCase(receive) && CReceive.downloadHandlerKey.equalsIgnoreCase(dataType)) {
            log.info("download process start >>> offset={}", streamData.getOffset());
            // TODO 接收到消息，设置消息状态为正在处理
            Request request = JSONObject.parseObject(streamData.getData(), Request.class);
            Response execute = WRequest.create(request).execute();

            /**
             * request 的生成方式
             * 1、通过 task_info 规则来生成
             *      利用 task_info 生成request的时候，request 含有 taskId，
             *      但是不具备 extract_info 的pid信息，此时的request只能给设置task_id
             *
             * 2、通过 extract_info 解析过程中生成
             */

            ExtractInfo extractInfo = ExtractInfo.create(request);
            extractInfo.setResult(execute.getResponseBody());
            extractInfo.setResultBytes(execute.getResponseBytes());

            URL currentURL = execute.getCurrentURL();
            if (currentURL != null) {
                extractInfo.setCurUrl(currentURL.toString());
            }

            // TODO 消息处理完成，设置该条消息为处理完成状态
            // 下面为新的消息，设置其状态到其对应的 push 环节设置即可。
            StreamData cycleStreamData = new StreamData();
            cycleStreamData.setReceive(CReceive.extractHandlerKey);
            cycleStreamData.setData(JSONObject.toJSONString(extractInfo));
            cycleStreamData.setDataType(CReceive.extractHandlerKey);

            resultFuture.complete(Collections.singleton(JSONObject.toJSONString(cycleStreamData)));
        }
        resultFuture.complete(null);

    }
}