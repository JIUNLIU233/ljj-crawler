package com.ljj.crawler.function;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.AppContext;
import com.ljj.crawler.common.utils.MountUtils;
import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.po.StreamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 功能：
 * 数据中心，大估计 mongoTemplate再这里也是不可以用的。
 *
 * @Author:JIUNLIU
 * @data : 2020/8/7 21:01
 */
@EnableAutoConfiguration
@Slf4j
public class DataProcess extends ProcessFunction<StreamData, StreamData> {

    private OutputTag<String> outputTag;

    private MongoTemplate mongoTemplate;

    public DataProcess() {
    }

    public DataProcess(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }


    public void setOutputTag(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        mongoTemplate = AppContext.getBean(MongoTemplate.class);
    }

    @Override
    public void processElement(StreamData streamData, Context context, Collector<StreamData> collector) throws Exception {
        String data = streamData.getData();
        String dataType = streamData.getDataType();
        log.info("data process start >>> data={}", data);

        if (CReceive.dataHandlerKey.equalsIgnoreCase(dataType)) {
            ExtractInfo extractInfo = JSONObject.parseObject(data, ExtractInfo.class);
            String traceId = extractInfo.getTraceId();
            List<String> pTraceId = extractInfo.getPTraceId();

            String result = extractInfo.getResult();
            String mount = extractInfo.getMount();

            String mountKey = MountUtils.getMountKey(mount);
            String collectionName = MountUtils.getCollectionName(mount);

            if (MountUtils.isArrayMount(mount)) { // 如果是挂载数据的数组子节点中，需要判断数组中的对象的traceId和当前节点的traceId相同。或者干脆不支持数组，
                // 涉及到数组的全部挂载到其他集合中。

            } else {// 是直接挂载的 或者是挂载到对应对象下面的，不涉及下标。
                if (MountUtils.isNewTraceId(mount)) {
                    mongoTemplate.upsert(
                            Query.query(Criteria.where("traceId").is(traceId)),
                            Update.update(mountKey, result)
                                    .set("mountTime", new Date())
                                    .set("parentId", pTraceId.get(pTraceId.size() - 1)),
                            collectionName);
                } else {
                    mongoTemplate.upsert(
                            Query.query(Criteria.where("traceId").is(traceId)),
                            Update.update(mountKey, result).set("mountTime", new Date()),
                            collectionName);
                }
            }
        }

        collector.collect(streamData);
    }
}
