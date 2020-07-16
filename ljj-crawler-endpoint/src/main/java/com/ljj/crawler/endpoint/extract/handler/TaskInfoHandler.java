package com.ljj.crawler.endpoint.extract.handler;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.mapper.TaskInfoMapper;
import com.ljj.crawler.endpoint.extract.mapper.TaskRuleMapper;
import com.ljj.crawler.endpoint.extract.model.TaskInfo;
import com.ljj.crawler.endpoint.extract.model.TaskRule;
import com.ljj.crawler.endpoint.extract.scheduler.QueueScheduler;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.webspider.http.Request;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主要负责从task 到 request 的过程
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
@Slf4j
@Component
public class TaskInfoHandler implements Handler {

    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private TaskRuleMapper taskRuleMapper;

    private Scheduler scheduler = new QueueScheduler();

    /**
     * 默认调度中心为本地Java对象队列
     *
     * @param scheduler
     */
    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void handler(Task task) {
        TaskInfo taskInfo = taskInfoMapper.findById(Integer.valueOf(task.getTaskId()));
        Integer haveRule = taskInfo.getHaveRule();
        if (haveRule == 0) {// 无规则情况
            log.info("virtual push, task_id={},url={}", taskInfo.getId(), taskInfo.getStartUrl());

            /**
             * 没有规则的情况下，可能存在无链接信息的情况出现
             */

            if (StringUtils.isNoneEmpty(taskInfo.getStartUrl())) {
                Request request = Request.create(taskInfo);
                log.info("task_id={}, push to request");
                scheduler.pushRequest(request);
            } else {
                Response response = Response.create(taskInfo);
                log.info("task_id={}, push to response");
                scheduler.pushResponse(response);
            }

        } else {// 有规则情况
            List<TaskRule> taskRules = taskRuleMapper.findByTaskId(taskInfo.getId());

            // 以下参数主要用于递增情况
            List<TaskInfo> taskInfos = new ArrayList<>();
            List<TaskInfo> tmpList = new ArrayList<>();


            ConcurrentHashMap<String, Boolean> flagTmp = new ConcurrentHashMap<>();
            flagTmp.put("firstFlag", true);
            flagTmp.put("orderFlag", true);

            for (TaskRule taskRule : taskRules) {
                if (taskRule.getRuleType() == 0) {
                    incrementRule(taskRule, flagTmp, taskInfo, taskInfos, tmpList);
                }
            }
            if (flagTmp.get("orderFlag")) {
                taskInfos.forEach(t -> {
                    log.info("virtual push, task_id={},url={}", t.getId(), t.getStartUrl());
                    Request request = Request.create(t);
                    scheduler.pushRequest(request);
                });
            } else {
                tmpList.forEach(t -> {
                    log.info("virtual push, task_id={},url={}", t.getId(), t.getStartUrl());
                    Request request = Request.create(t);
                    scheduler.pushRequest(request);
                });
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
            if (flagTmp.get("firstFlag")) {
                TaskInfo tmp = new TaskInfo(taskInfo);
                tmp.setStatus(1);
                tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getParamName() + "}", String.valueOf(start)));
                taskInfos.add(tmp);
            } else {
                if (flagTmp.get("orderFlag")) {
                    for (TaskInfo info : taskInfos) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getParamName() + "}", String.valueOf(start)));
                        tmpList.add(tmp);
                    }
                } else {
                    for (TaskInfo info : tmpList) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getParamName() + "}", String.valueOf(start)));
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
