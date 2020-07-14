package com.ljj.crawler.endpoint.extract.handler;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.mapper.TaskInfoMapper;
import com.ljj.crawler.endpoint.extract.mapper.TaskRuleMapper;
import com.ljj.crawler.endpoint.extract.model.TaskInfo;
import com.ljj.crawler.endpoint.extract.model.TaskRule;
import lombok.extern.slf4j.Slf4j;
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
public class TaskInfoHandler<T> implements Handler {

    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private TaskRuleMapper taskRuleMapper;

    @Override
    public void handler(Task task) {
        TaskInfo taskInfo = taskInfoMapper.findById(Integer.valueOf(task.getTaskId()));
        Integer haveRule = taskInfo.getHaveRule();
        if (haveRule == 0) {// 无规则情况
            log.info("virtual push, task_id={},url={}", taskInfo.getId(), taskInfo.getStartUrl());
            //TODO 生成 Request ，并对其进行调度
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
                    //TODO 生成 Request ，并对其进行调度
                });
            } else {
                tmpList.forEach(t -> {
                    log.info("virtual push, task_id={},url={}", t.getId(), t.getStartUrl());
                    //TODO 生成 Request ，并对其进行调度
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
