package com.ljj.crawler.extract.handler;

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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void handler(Task task) {
        if (task instanceof ExtractInfo) {
            String traceId = task.getTraceId();
            List<String> pTraceId = task.getPTraceId();

            String result = ((ExtractInfo) task).getResult();
            String mount = ((ExtractInfo) task).getMount();
            mount = mount.split("\\.")[2];

            if (pTraceId == null || pTraceId.size() < 1) { //没有父关联对象，直接存储即可。
                //TODO 目前仅仅针对单一的数据挂载
                mongoTemplate.upsert(Query.query(Criteria.where("traceId").is(traceId)), Update.update(mount, result), "xbqg");
                mongoTemplate.upsert(Query.query(Criteria.where("traceId").is(traceId)), Update.update("mountTime", new Date()), "xbqg");
            }

        }
    }
}
