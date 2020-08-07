package com.ljj.crawler.function;

import com.ljj.crawler.po.StreamData;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 针对解析的处理
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
public class ExtractProcess extends ProcessFunction<StreamData, StreamData> {
    @Override
    public void processElement(StreamData value, Context ctx, Collector<StreamData> out) throws Exception {

    }
}
