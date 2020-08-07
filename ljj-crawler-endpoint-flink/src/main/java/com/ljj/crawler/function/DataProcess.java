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
 * @data : 2020/8/7 21:01
 */
@Component
@Slf4j
public class DataProcess extends ProcessFunction<StreamData, StreamData> {

    private OutputTag<StreamData> outputTag;

    public DataProcess() {
    }

    public DataProcess(OutputTag<StreamData> outputTag) {
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
        log.info("data process start >>> data={}", data);
    }
}
