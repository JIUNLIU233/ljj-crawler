package com.ljj.crawler.endpoint_v1.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.MountUtils;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.endpoint_v1.handler.AbstractHandler;
import com.ljj.crawler.endpoint_v1.po.CReceive;
import com.ljj.crawler.endpoint_v1.po.CycleData;
import com.ljj.crawler.endpoint_v1.utils.CycleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Component
@Slf4j
public class DataMongoHandler implements AbstractHandler {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void handler(CycleData cycleData, CycleUtils cycleUtils, Semaphore semaphore) {
        String data = cycleData.getData();
        String dataType = cycleData.getDataType();
        log.info("data process start >>> offset={} ", cycleData.getOffset());

        //TODO 设置消息状态为正在处理
        if (CReceive.dataHandlerKey.equalsIgnoreCase(dataType)) {
            ExtractInfo extractInfo = JSONObject.parseObject(data, ExtractInfo.class);
            String traceId = extractInfo.getTraceId();
            List<String> pTraceId = extractInfo.getPTraceId();

            String result = extractInfo.getResult();
            String mount = extractInfo.getMount();

            String mountKey = MountUtils.getMountKey(mount);
            String collectionName = MountUtils.getCollectionName(mount);


            // 是直接挂载的 或者是挂载到对应对象下面的，不涉及下标。
            if (MountUtils.isNewTraceId(extractInfo)) {
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

            log.info("data process end >>> offset={} , collection={} , traceId={}", cycleData.getOffset(), collectionName, traceId);
        }
    }
}
