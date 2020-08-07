package com.ljj.crawler.function;

import com.ljj.crawler.po.StreamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.stereotype.Component;

/**
 * 针对解析的处理
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Component
@Slf4j
public class ExtractProcess extends ProcessFunction<StreamData, StreamData> {
    private OutputTag<StreamData> outputTag;

    public ExtractProcess() {
    }

    public ExtractProcess(OutputTag<StreamData> outputTag) {
        this.outputTag = outputTag;
    }

    public OutputTag<StreamData> getOutputTag() {
        return outputTag;
    }

    public void setOutputTag(OutputTag<StreamData> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void processElement(StreamData value, Context ctx, Collector<StreamData> out) throws Exception {
        String data = value.getData();
        log.info("extract process start >>> data={}", data);
    }
}