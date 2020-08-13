package com.ljj.crawler.data;

import com.ljj.crawler.common.utils.MountUtils;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.handler.AbstractHandler;
import com.ljj.crawler.core.po.ExtractInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 数据处理中心
 * 用于存储数据到Mongo中
 * Create by JIUN·LIU
 * Create time 2020/8/5
 **/
@Slf4j
@Component
public class DataMongoHandler implements AbstractHandler {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DataMongoHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void handler(Task task) {
        if (task instanceof ExtractInfo) {
            String traceId = task.getTraceId();
            List<String> pTraceId = task.getPTraceId();

            String result = ((ExtractInfo) task).getResult();
            String mount = ((ExtractInfo) task).getMount();

            String mountKey = MountUtils.getMountKey(mount);
            String collectionName = MountUtils.getCollectionName(mount);

            // 是直接挂载的 或者是挂载到对应对象下面的，不涉及下标。
            if (MountUtils.isNewTraceId((ExtractInfo) task)) {
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
}
