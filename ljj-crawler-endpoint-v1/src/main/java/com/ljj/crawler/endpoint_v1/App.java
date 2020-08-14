package com.ljj.crawler.endpoint_v1;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.endpoint_v1.handler.impl.DataMongoHandler;
import com.ljj.crawler.endpoint_v1.handler.impl.DownLoaderHandler;
import com.ljj.crawler.endpoint_v1.handler.impl.ExtractHandler;
import com.ljj.crawler.endpoint_v1.handler.impl.TaskHandler;
import com.ljj.crawler.endpoint_v1.po.CReceive;
import com.ljj.crawler.endpoint_v1.po.CycleData;
import com.ljj.crawler.endpoint_v1.utils.CycleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@SpringBootApplication
@MapperScan("com.ljj.crawler.mapper")
public class App implements CommandLineRunner {

    @Value("${crawler.concurrent}")
    private int concurrent;

    @Autowired
    private TaskHandler taskHandler;
    @Autowired
    private DownLoaderHandler downLoaderHandler;
    @Autowired
    private ExtractHandler extractHandler;
    @Autowired
    private DataMongoHandler dataMongoHandler;
    @Autowired
    private CycleUtils cycleUtils;

    private Semaphore concurrentSemaphore;
    ExecutorService threadPool;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        concurrentSemaphore = new Semaphore(concurrent);
        threadPool = Executors.newFixedThreadPool(concurrent);
    }

    /**
     * 爬虫流程处理
     *
     * @param record
     */
    @KafkaListener(groupId = "endpoint", topics = "${crawler.topic}")
    public void listener(ConsumerRecord<String, String> record) {
        int partition = record.partition();
        long offset = record.offset();
        String value = record.value();
        log.info("endpoint listener start >>> offset={} , data={}", offset, value);
        CycleData cycleData = JSONObject.parseObject(value, CycleData.class);
        cycleData.setOffset(offset);
        cycleData.setPartition(partition);
        String type = cycleData.getType();
        if (CReceive.taskHandlerKey.equalsIgnoreCase(type)) { //  task的处理
            taskHandler.handler(cycleData, cycleUtils, concurrentSemaphore);
        } else if (CReceive.extractHandlerKey.equalsIgnoreCase(type)) { // 解析的处理
            extractHandler.handler(cycleData, cycleUtils, concurrentSemaphore);
        } else if (CReceive.dataHandlerKey.equalsIgnoreCase(type)) { // 数据存储的处理
            dataMongoHandler.handler(cycleData, cycleUtils, concurrentSemaphore);
        }
        log.info("endpoint listener end >>> offset={} , data={}", offset);
    }

    @KafkaListener(groupId = "endpoint-downloader", topics = "${crawler.topic}")
    public void downloader(ConsumerRecord<String, String> record) {
        int partition = record.partition();
        long offset = record.offset();
        String value = record.value();
        log.info("endpoint-downloader listener start >>> offset={} , data={}", offset, value);
        CycleData cycleData = JSONObject.parseObject(value, CycleData.class);
        cycleData.setOffset(offset);
        cycleData.setPartition(partition);
        String type = cycleData.getType();
        if (CReceive.downloadHandlerKey.equalsIgnoreCase(type)) {
            try {
                concurrentSemaphore.acquire();
                threadPool.execute(() -> downLoaderHandler.handler(cycleData, cycleUtils, concurrentSemaphore));
            } catch (InterruptedException e) {
                log.error("downloader listener error , e:", e);
                concurrentSemaphore.release();
            }
        }
        log.info("endpoint-downloader listener end >>> offset={} , data={}", offset, value);
    }
}