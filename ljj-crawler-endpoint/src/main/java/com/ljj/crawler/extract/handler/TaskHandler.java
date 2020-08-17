package com.ljj.crawler.extract.handler;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.po.TaskRule;
import com.ljj.crawler.common.utils.TraceUtil;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.handler.AbstractHandler;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.mapper.RuleMapper;
import com.ljj.crawler.mapper.TaskMapper;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能：
 * 任务的处理
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:58
 */
@Slf4j
@Component
public class TaskHandler implements AbstractHandler {

    private Scheduler scheduler;

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private RuleMapper ruleMapper;

    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 这里接受的是一个 taskInfo,需要对其进行起始进行操作
     *
     * @param task
     * @return
     */
    @Override
    public void handler(Task task) {
        log.info("task handler start >>> task={}", JSON.toJSONString(task));
        if (scheduler == null) throw new RuntimeException("task handler 未初始化，请初始化后再进行执行");
        //1、查询出具体的taskInfo信息
        TaskInfo taskInfo = taskMapper.findById(Integer.valueOf(task.getTid()));
        //2、对task Info做出相对必要的校验信息
        List<TaskRule> rules = ruleMapper.findByTid(Integer.valueOf(taskInfo.getTid()));
        String startUrl1 = taskInfo.getStartUrl();
        if (rules == null || rules.size() < 1) { // 链接没有规则信息
            log.info("task handler don`t have rule >>>");
            taskInfo.setTraceId(TraceUtil.traceId()); // 生成一个traceId。
            if (startUrl1 == null || "".equalsIgnoreCase(startUrl1)) {
                // 为空的时候，证明不需要进行对应的request生成，只需要进行下一步即可。
                log.info("task handler pushExtract >>> extract={}", JSON.toJSONString(taskInfo));
                scheduler.pushExtract(taskInfo);
            } else {
                Request request = Request.create(taskInfo);
                log.info("task handler pushRequest >>> request={}", JSON.toJSONString(request));
                scheduler.pushRequest(request);
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
                            log.info("task handler pushRequest >>> request={}", JSON.toJSONString(request));
                            scheduler.pushRequest(request);
                        });
                    } else {
                        tmpList.forEach(t -> {
                            t.setTraceId(TraceUtil.traceId());
                            Request request = Request.create(t);
                            log.info("task handler pushRequest >>> request={}", JSON.toJSONString(request));
                            scheduler.pushRequest(request);
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

}
