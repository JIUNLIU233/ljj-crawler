package com.ljj.crawler.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.constant.KafkaGroupKey;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 以kafka 作为一个调度中心，
 * 从kafka的角度来说 所有信息使用同一个topic，使用不同的groupid即可实现
 * 难处就在于，如何维护这个offset。如果任务处理失败，怎么进行处理
 * <p>
 * 通过kafka 和本地阻塞队列达成一个中转过程即可。
 * <p>
 * 从push 的角度来说，可以预先设置是谁来接收，以及数据类型
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Slf4j
@Component
public class KafkaScheduler implements Scheduler {


    @Value("${scheduler.kafka.topic}")
    private String kafkaTopic;
    @Autowired
    private KafkaTemplate kafkaTemplate;

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
        pushToKafka(KafkaGroupKey.taskKey, taskInfo, KafkaGroupKey.taskKey);
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
        log.debug("receive from kafka  >>> offset={},value={}", record.offset(), record.value());
        String receive = getReceive(record);
        String data = getData(record);
        if (KafkaGroupKey.taskKey.equalsIgnoreCase(receive)) {
            TaskInfo taskInfo = JSONObject.parseObject(data, TaskInfo.class);
            try {
                taskInfos.put(taskInfo);
            } catch (InterruptedException e) {
                log.error("kafka push taskInfo to local error >>> e:", e);
            }
        }
    }


    @Override
    public void pushRequest(Task request) {
        pushToKafka(KafkaGroupKey.downloadKey, request, KafkaGroupKey.downloadKey);
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
        log.debug("receive from kafka  >>> offset={},value={}", record.offset(), record.value());

        String receive = getReceive(record);
        String data = getData(record);
        if (KafkaGroupKey.downloadKey.equalsIgnoreCase(receive)) {
            Request request = JSONObject.parseObject(data, Request.class);
            try {
                requests.put(request);
            } catch (InterruptedException e) {
                log.error("kafka push request to local error >>> e:", e);
            }
        }
    }

    @Override
    public void pushExtract(Task task) {
        if (task instanceof TaskInfo) {
            pushToKafka(KafkaGroupKey.extractKey, task, KafkaGroupKey.taskKey);
        } else if (task instanceof ExtractInfo) {
            pushToKafka(KafkaGroupKey.extractKey, task, KafkaGroupKey.extractKey);
        }
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
        log.debug("receive from kafka  >>> offset={},value={}", record.offset(), record.value());

        String receive = getReceive(record);
        String data = getData(record);
        String dataType = getDataType(record);
        if (KafkaGroupKey.extractKey.equalsIgnoreCase(receive)) {
            Task ex = null;

            if (KafkaGroupKey.taskKey.equalsIgnoreCase(dataType)) {
                ex = JSONObject.parseObject(data, TaskInfo.class);
            } else if (KafkaGroupKey.extractKey.equalsIgnoreCase(dataType)) {
                ex = JSONObject.parseObject(data, ExtractInfo.class);
            }
            if (ex != null)
                try {
                    extractInfos.put(ex);
                } catch (InterruptedException e) {
                    log.error("kafka push extract to local error >>> e:", e);
                }
        }
    }

    @Override
    public void pushData(Task extractInfo) {
        pushToKafka(KafkaGroupKey.dataKey, extractInfo, KafkaGroupKey.dataKey);
    }

    @Override
    public Task pollData() {
        return dataInfos.poll();
    }

    @KafkaListener(id = KafkaGroupKey.dataKey, topics = "${scheduler.kafka.topic}", groupId = KafkaGroupKey.dataKey, autoStartup = "false")
    public void pollDataFromKafka(ConsumerRecord<String, String> record) {
        log.debug("receive from kafka  >>> offset={},value={}", record.offset(), record.value());

        String receive = getReceive(record);
        String data = getData(record);
        if (KafkaGroupKey.dataKey.equalsIgnoreCase(receive)) {
            ExtractInfo extractInfo = JSONObject.parseObject(data, ExtractInfo.class);
            try {
                dataInfos.put(extractInfo);
            } catch (InterruptedException e) {
                log.error("kafka push data to local error >>> e:", e);
            }
        }
    }


    public void pushToKafka(String receive, Task data, String dataTyp) {
        JSONObject kafkaValue = new JSONObject() {{
            put("receive", receive);
            put("data", data);
            put("dataType", dataTyp);
        }};
        String dataStr = kafkaValue.toJSONString();
        log.info("push data to kafka >>> topic={}, value={}", kafkaTopic, dataStr);
        kafkaTemplate.send(kafkaTopic, dataStr);
    }

    public String getReceive(ConsumerRecord<String, String> record) {
        JSONObject jsonObject = JSONObject.parseObject(record.value());
        return jsonObject.getString("receive");
    }

    public String getData(ConsumerRecord<String, String> record) {
        JSONObject jsonObject = JSONObject.parseObject(record.value());
        return jsonObject.getString("data");
    }

    public String getDataType(ConsumerRecord<String, String> record) {
        JSONObject jsonObject = JSONObject.parseObject(record.value());
        return jsonObject.getString("dataType");
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

