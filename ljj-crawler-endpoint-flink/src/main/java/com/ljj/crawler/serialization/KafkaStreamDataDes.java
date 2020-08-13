package com.ljj.crawler.serialization;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.po.StreamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Slf4j
public class KafkaStreamDataDes implements KafkaDeserializationSchema<StreamData> {
    @Override
    public boolean isEndOfStream(StreamData streamData) {
        return false;
    }

    /**
     * kafka 中数据编码需要是 utf-8 的编码格式
     *
     * @param consumerRecord
     * @return
     * @throws Exception
     */
    @Override
    public StreamData deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        try {
            String text = new String(consumerRecord.value());
            log.info("offset={},value={}", consumerRecord.offset(), "");
            StreamData streamData = JSONObject.parseObject(text, StreamData.class);
            return streamData;
        } catch (Exception e) {
            log.error("des streamData error >>> e:", e);
        }
        return null;
    }

    @Override
    public TypeInformation<StreamData> getProducedType() {
        return TypeInformation.of(StreamData.class);
    }
}
