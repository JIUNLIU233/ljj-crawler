package com.ljj.crawler.endpoint_v1.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.TraceUtil;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.po.TaskRule;
import com.ljj.crawler.endpoint_v1.handler.AbstractHandler;
import com.ljj.crawler.endpoint_v1.po.CReceive;
import com.ljj.crawler.endpoint_v1.po.CycleData;
import com.ljj.crawler.endpoint_v1.utils.CycleUtils;
import com.ljj.crawler.service.TaskRuleService;
import com.ljj.crawler.service.TaskService;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/14
 **/
@Component
@Slf4j
public class TaskHandler implements AbstractHandler {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRuleService taskRuleService;

    @Override
    public void handler(CycleData value, CycleUtils cycleUtils, Semaphore semaphore) {
        String data = value.getData();
        log.info("task process start >>> offset={}", value.getOffset());

        // TODO 更行任务状态为 执行ing状态。

        TaskInfo task = JSONObject.toJavaObject(JSONObject.parseObject(data), TaskInfo.class);

        TaskInfo taskInfo = taskService.findById(Integer.valueOf(task.getTid()));
        //2、对task Info做出相对必要的校验信息
        List<TaskRule> rules = taskRuleService.findByTid(Integer.valueOf(taskInfo.getTid()));
        String startUrl1 = taskInfo.getStartUrl();
        if (rules == null || rules.size() < 1) { // 链接没有规则信息
            log.info("task process don`t have rule >>> offset={}", value.getOffset());
            taskInfo.setTraceId(TraceUtil.traceId()); // 生成一个traceId。
            if (startUrl1 == null || "".equalsIgnoreCase(startUrl1)) {
                // 为空的时候，证明不需要进行对应的request生成，只需要进行下一步即可。

                cycleUtils.cycle(CReceive.extractHandlerKey,
                        JSONObject.toJSONString(taskInfo),
                        CReceive.taskHandlerKey);
                log.info("task process cycle >>> offset={} , type={} , data={} , dataType={}",
                        value.getOffset(), CReceive.downloadHandlerKey, data, CReceive.downloadHandlerKey
                );
            } else {
                Request request = Request.create(taskInfo);
                outDownLoad(cycleUtils, request, value.getOffset());
            }
        } else { // 链接有规则信息，有规则信息，则必然存在startUrl。要不然规则无处安放。
            for (TaskRule rule : rules) {
                if (rule.getRuleType() == 1) {
                    // 以下参数主要用于递增情况
                    List<TaskInfo> taskInfos = new ArrayList<>();
                    List<TaskInfo> tmpList = new ArrayList<>();

                    ConcurrentHashMap<String, Boolean> flagTmp = new ConcurrentHashMap<>();
                    flagTmp.put("firstFlag", true);
                    flagTmp.put("orderFlag", true);
                    incrementRule(rule, flagTmp, taskInfo, taskInfos, tmpList);
                    if (flagTmp.get("orderFlag")) {
                        taskInfos.forEach(t -> {
                            t.setTraceId(TraceUtil.traceId());
                            Request request = Request.create(t);
                            outDownLoad(cycleUtils, request, value.getOffset());
                        });
                    } else {
                        tmpList.forEach(t -> {
                            t.setTraceId(TraceUtil.traceId());
                            Request request = Request.create(t);
                            outDownLoad(cycleUtils, request, value.getOffset());
                        });
                    }
                }
            }

        }
    }

    /**
     * 递增序列
     */
    private void incrementRule(TaskRule taskRule, ConcurrentHashMap<String, Boolean> flagTmp,
                               TaskInfo taskInfo, List<TaskInfo> taskInfos, List<TaskInfo> tmpList) {
        String ruleStr = taskRule.getRuleParam();
        String[] split = ruleStr.split("-");
        int start = Integer.valueOf(split[0]);
        int max = Integer.valueOf(split[1]);
        for (; start <= max; start++) {
            String replacement = String.valueOf(start);
            if (flagTmp.get("firstFlag")) {
                TaskInfo tmp = new TaskInfo(taskInfo);
                tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", replacement));
                taskInfos.add(tmp);
            } else {
                if (flagTmp.get("orderFlag")) {
                    for (TaskInfo info : taskInfos) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", replacement));
                        tmpList.add(tmp);
                    }
                } else {
                    for (TaskInfo info : tmpList) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", replacement));
                        taskInfos.add(tmp);
                    }
                }
            }
        }
        if (flagTmp.get("firstFlag")) {
            flagTmp.put("firstFlag", Boolean.FALSE);
            return;
        }
        if (flagTmp.get("orderFlag")) {
            taskInfos.clear();
            flagTmp.put("orderFlag", Boolean.FALSE);
        } else {
            tmpList.clear();
            flagTmp.put("orderFlag", Boolean.TRUE);
        }
    }

    public void outDownLoad(CycleUtils cycleUtils, Request request, long offset) {
        String data = JSONObject.toJSONString(request);
        cycleUtils.cycle(CReceive.downloadHandlerKey, data
                , CReceive.downloadHandlerKey);
        log.info("task process cycle >>> offset={} , type={} , data={} , dataType={}",
                offset, CReceive.downloadHandlerKey, data, CReceive.downloadHandlerKey
        );
    }
}
