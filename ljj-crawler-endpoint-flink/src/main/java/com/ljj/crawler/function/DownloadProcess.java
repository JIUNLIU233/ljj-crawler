package com.ljj.crawler.function;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.webspider.WRequest;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/7 20:56
 */
@Slf4j
public class DownloadProcess extends ProcessFunction<StreamData, StreamData> {

    private OutputTag<String> outputTag;

    public DownloadProcess() {
    }

    public DownloadProcess(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }


    public void setOutputTag(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void processElement(StreamData streamData, Context context, Collector<StreamData> collector) throws Exception {
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

        context.output(outputTag, JSONObject.toJSONString(cycleStreamData));
        log.info("download process sideOut >>> offset={} , tag={},data={}", streamData.getOffset(), outputTag, cycleStreamData);

        collector.collect(streamData);
    }
}
