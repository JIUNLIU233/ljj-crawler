package com.ljj.crawler;


import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.deserialization.KafkaStreamDataDes;
import com.ljj.crawler.function.*;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.utils.OutPutTagUtils;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.OutputTag;
import org.springframework.beans.factory.annotation.Autowired;
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


    private TaskProcess taskProcess;
    private DownloadProcess downloadProcess;
    private ExtractProcess extractProcess;
    private DataProcess dataProcess;
    private SourceDisProcess sourceDisProcess;

    public void init() {
        sourceDisProcess.setSrt(OutPutTagUtils.getSourceOutTag());
        taskProcess.setOutputTag(OutPutTagUtils.getCycleTag());
        downloadProcess.setOutputTag(OutPutTagUtils.getCycleTag());
        extractProcess.setOutputTag(OutPutTagUtils.getCycleTag());
        dataProcess.setOutputTag(OutPutTagUtils.getCycleTag());
    }

    @Autowired
    public FlinkEndPointApp(TaskProcess taskProcess, DownloadProcess downloadProcess, ExtractProcess extractProcess, DataProcess dataProcess, SourceDisProcess sourceDisProcess) {
        this.taskProcess = taskProcess;
        this.downloadProcess = downloadProcess;
        this.extractProcess = extractProcess;
        this.dataProcess = dataProcess;
        this.sourceDisProcess = sourceDisProcess;
    }


    public void start() throws Exception {
        init();
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
        consumerProps.setProperty("group.id", "ljj-crawler");
        consumerProps.setProperty("enable.auto.commit", "true");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<StreamData> consumer = new FlinkKafkaConsumer<>(endpointTopic, new KafkaStreamDataDes(), consumerProps);

        // 将数据按照不同的类型输出到不同的支流中。
        SingleOutputStreamOperator<StreamData> dataStream =
                env.addSource(consumer).process(sourceDisProcess);

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
                SingleOutputStreamOperator<StreamData> taskSide = sideOutput.process(taskProcess);
                dealCycleData(taskSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.downloadHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> downloadSide = sideOutput.process(downloadProcess);
                dealCycleData(downloadSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.extractHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> extractSide = sideOutput.process(extractProcess);
                dealCycleData(extractSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            } else if (CReceive.dataHandlerKey.equalsIgnoreCase(key)) {
                SingleOutputStreamOperator<StreamData> dataSide = sideOutput.process(dataProcess);
                dealCycleData(dataSide.getSideOutput(OutPutTagUtils.getCycleTag()));
            }
        }
    }

    public void dealCycleData(DataStream<StreamData> cycleStreamSideOutput) {
        //TODO 重新把cycle中的数据发送到kafka中
        cycleStreamSideOutput.print();
    }

}
