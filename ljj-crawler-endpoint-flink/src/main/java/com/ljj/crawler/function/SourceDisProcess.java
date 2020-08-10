package com.ljj.crawler.function;

import com.ljj.crawler.po.StreamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 将从kafka中拿到的数据按照 receive 进行分组
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Component
@Slf4j
public class SourceDisProcess extends ProcessFunction<StreamData, StreamData> {
    Map<String, OutputTag<StreamData>> srt;

    public SourceDisProcess() {
    }

    public SourceDisProcess(Map<String, OutputTag<StreamData>> srt) {
        this.srt = srt;
    }

    public void setSrt(Map<String, OutputTag<StreamData>> srt) {
        this.srt = srt;
    }

    @Override
    public void processElement(StreamData value, Context ctx, Collector<StreamData> out) throws Exception {
        log.info("source process start >>> data={}", value);
        if (value != null) { // 将不同的数据发送到不同的支流中，然后处理 分别是 task download extract data
            String receive = value.getReceive();
            ctx.output(srt.get(receive), value);
        }
        out.collect(value);
    }
}
