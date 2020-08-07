package com.ljj.crawler.core.scheduler.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.constant.KafkaGroupKey;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 以kafka 作为一个调度中心，
 * 从kafka的角度来说 所有信息使用同一个topic，使用不同的groupid即可实现
 * 难处就在于，如何维护这个offset。如果任务处理失败，怎么进行处理
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Slf4j
@Component
public class KafkaScheduler implements Scheduler {


    @Resource
    private KafkaListenerEndpointRegistry registry;

    /**
     * 本地队列都设置为1，方式kafka拉取下来的信息丢失
     */
    LinkedBlockingQueue<Task> taskInfos = new LinkedBlockingQueue<>(1);      // 任务队列
    LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<>(1);    // 请求队列
    LinkedBlockingQueue<Task> extractInfos = new LinkedBlockingQueue<>(1);   // 解析队列
    LinkedBlockingQueue<Task> dataInfos = new LinkedBlockingQueue<>(1);      // 数据队列

    @Override
    public void pushTask(Task taskInfo) {

    }

    /**
     * 任务获取中心
     *
     * @return
     */
    @Override
    public Task pollTask() {
        return taskInfos.poll();
    }

    @KafkaListener(id = KafkaGroupKey.taskKey, topics = "${scheduler.kafka.topic}", groupId = KafkaGroupKey.taskKey, autoStartup = "false")
    public void pollTaskFromKafka(ConsumerRecord<String, String> record) {
        String value = record.value();
        long offset = record.offset();
        log.info("kafka consumer record.offset={},record.value={}", offset, value);
        JSONObject jsonObject = JSONObject.parseObject(value);
        String string = jsonObject.getString("dataType");
        JSONObject data = jsonObject.getJSONObject("data");
        if (KafkaGroupKey.taskKey.equalsIgnoreCase(string)) {
            try {
                taskInfos.put(data.toJavaObject(TaskInfo.class));
            } catch (InterruptedException e) {
                log.error("kafka poll taskInfo to local error >>> e:", e);
            }
        }

    }


    @Override
    public void pushRequest(Task request) {

    }

    /**
     * 下载器获取中心
     *
     * @return
     */
    @Override
    public Task pollRequest() {
        return requests.poll();
    }

    @KafkaListener(id = KafkaGroupKey.downloadKey, topics = "${scheduler.kafka.topic}", groupId = KafkaGroupKey.downloadKey, autoStartup = "false")
    public void pollRequestFromKafka(ConsumerRecord<String, String> record) {

    }

    @Override
    public void pushExtract(Task task) {

    }

    /**
     * 解析器获取中心
     *
     * @return
     */
    @Override
    public Task pollExtract() {
        return extractInfos.poll();
    }

    @KafkaListener(id = KafkaGroupKey.extractKey, topics = "${scheduler.kafka.topic}", groupId = KafkaGroupKey.extractKey, autoStartup = "false")
    public void pollExtractFromKafka(ConsumerRecord<String, String> record) {

    }

    @Override
    public void pushData(Task extractInfo) {

    }

    @Override
    public Task pollData() {
        return dataInfos.poll();
    }

    @KafkaListener(id = KafkaGroupKey.dataKey, topics = "${scheduler.kafka.topic}", groupId = KafkaGroupKey.dataKey, autoStartup = "false")
    public void pollDataFromKafka(ConsumerRecord<String, String> record) {
        log.info("kafka consumer record.offset={},record.value={}", record.offset(), record.value());
    }


    public void startListener() {
        List<String> allKey = KafkaGroupKey.getAllKey();
        for (String s : allKey) {
            if (!registry.getListenerContainer(s).isRunning()) {
                registry.getListenerContainer(s).start();
            }
            registry.getListenerContainer(s).resume();
            log.info("scheduler kafka >>> {} -- 已启动监听", s);
        }
        log.info("scheduler kafka 已全部启动监听完毕");
    }
}

