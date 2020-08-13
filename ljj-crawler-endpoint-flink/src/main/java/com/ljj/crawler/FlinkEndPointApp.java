package com.ljj.crawler;


import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.function.*;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.serialization.KafkaStreamDataDes;
import com.ljj.crawler.utils.OutPutTagUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.OutputTag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

/**
 * flink 执行端点的起始类。
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Configuration
public class FlinkEndPointApp {


    @Value("${ljj.crawler.endpoint.topic}")
    private String endpointTopic;

    public void start() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 开启Checkpoinit
        env.enableCheckpointing(60000);
        // EXACTLY_ONCE: 准确一次，结果不丢不重 AT_LEAST_ONCE: 至少一次，结果可能会重复
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 设置两次checkpoint的最小时间间隔
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(10000);
        // 检查点必须在二分钟内完成，或者被丢弃
        env.getCheckpointConfig().setCheckpointTimeout(120000);
        // 允许的最大checkpoint并行度
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 任务取消后保留Checkpoint目录
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        /**
         * 消费者配置
         */
        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "fjr-yz-204-15:9092,fjr-yz-0-134:9092,fjr-yz-0-135:9092");
        consumerProps.setProperty("group.id", "idea-test12");
        consumerProps.setProperty("auto.offset.reset","latest");
        consumerProps.setProperty("enable.auto.commit", "true");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<StreamData> consumer = new FlinkKafkaConsumer<>(endpointTopic, new KafkaStreamDataDes(), consumerProps);

        // 将数据按照不同的类型输出到不同的支流中。
        SingleOutputStreamOperator<StreamData> dataStream =
                env.addSource(consumer).process(new SourceDisProcess(OutPutTagUtils.getSourceOutTag()));

        dealStreamData(dataStream);


        env.execute("ljj-crawler-endpoint-flink");
    }


    /**
     * 对通过source 划分后的数据流进行不同的handler。
     *
     * @param dataStream
     */
    public void dealStreamData(SingleOutputStreamOperator<StreamData> dataStream) {
        Map<String, OutputTag<StreamData>> sourceOutTag = OutPutTagUtils.getSourceOutTag();
        for (Map.Entry<String, OutputTag<StreamData>> outputTagEntry : sourceOutTag.entrySet()) {
            DataStream<StreamData> sideOutput =
                    dataStream.getSideOutput(outputTagEntry.getValue());
            String key = outputTagEntry.getKey();
            if (CReceive.taskHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> taskSide = sideOutput.process(new TaskProcess(OutPutTagUtils.getCycleTag()));
                dealCycleData(taskSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.downloadHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> downloadSide = sideOutput.process(new DownloadProcess(OutPutTagUtils.getCycleTag()));
                dealCycleData(downloadSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.extractHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> extractSide = sideOutput.process(new ExtractProcess(OutPutTagUtils.getCycleTag()));
                dealCycleData(extractSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.dataHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> dataSide = sideOutput.process(new DataProcess(OutPutTagUtils.getCycleTag()));
                dealCycleData(dataSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            }
        }
    }

    public void dealCycleData(DataStream<String> cycleStreamSideOutput) {
        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "fjr-yz-204-15:9092,fjr-yz-0-134:9092,fjr-yz-0-135:9092");
        producerProps.setProperty("enable.auto.commit", "true");
        producerProps.setProperty("max.request.size", Integer.toString(1024 * 1024 * 5));
        producerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        producerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // TODO 设置消息处理状态为 已经发送到kafka中。
        cycleStreamSideOutput.addSink(new FlinkKafkaProducer<String>(endpointTopic, new SimpleStringSchema(), producerProps));

    }

}
