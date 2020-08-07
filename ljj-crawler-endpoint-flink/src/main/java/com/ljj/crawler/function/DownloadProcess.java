package com.ljj.crawler.function;

import com.ljj.crawler.po.StreamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.stereotype.Component;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/7 20:56
 */
@Component
@Slf4j
public class DownloadProcess extends ProcessFunction<StreamData, StreamData> {

    private OutputTag<StreamData> outputTag;

    public DownloadProcess(){}

    public DownloadProcess(OutputTag<StreamData> outputTag) {
        this.outputTag = outputTag;
    }

    public OutputTag<StreamData> getOutputTag() {
        return outputTag;
    }

    public void setOutputTag(OutputTag<StreamData> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void processElement(StreamData streamData, Context context, Collector<StreamData> collector) throws Exception {
        String data = streamData.getData();
        log.info("download process start >>> data={}", data);
    }
}
