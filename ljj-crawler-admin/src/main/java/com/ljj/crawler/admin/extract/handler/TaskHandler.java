package com.ljj.crawler.admin.extract.handler;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.admin.extract.dao.TaskInfoMapper;
import com.ljj.crawler.admin.extract.dao.TaskRuleMapper;
import com.ljj.crawler.admin.extract.handler.impl.LocalPipeline;
import com.ljj.crawler.admin.extract.po.TaskInfo;
import com.ljj.crawler.admin.extract.po.TaskRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 14:02
 */
@Slf4j
@Component
public class TaskHandler {

    private Pipeline pipeline;
    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private TaskRuleMapper taskRuleMapper;

    public void init(Pipeline pipeline) {
        if (pipeline == null) this.pipeline = new LocalPipeline();
        else this.pipeline = pipeline;
    }

    /**
     * 一个爬虫任务初始化的处理
     * 到这里，基本上页面入口的url已经生成了。
     */
    public void initHandler(Integer task_id) {
        //1、解析一个任务
        TaskInfo taskInfo = taskInfoMapper.findById(task_id);
        //2、判断url是否有规则
        Integer haveRule = taskInfo.getHaveRule();
        if (haveRule == 0) {//无规则
            pipeline.pushTask(taskInfo);
        } else {// 有规则
            /**
             * 获取到规则之后，要生成一批taskInfo信息。
             * 规则链表，后一个规则依赖第一个规则的结果
             */
            List<TaskRule> taskRules = taskRuleMapper.findByTaskId(taskInfo.getId());

            List<TaskInfo> taskInfos = new ArrayList<>();
            List<TaskInfo> tmpList = new ArrayList<>();
            boolean firstFlag = true;// 是否是第一个规则的标示
            boolean orderFlag = true;
            for (TaskRule taskRule : taskRules) {
                if (taskRule.getRuleType() == 0) {
                    String ruleStr = taskRule.getRuleParam();
                    String[] split = ruleStr.split("-");
                    int start = Integer.valueOf(split[0]);
                    int max = Integer.valueOf(split[1]);
                    for (; start <= max; start++) {
                        if (firstFlag) {
                            TaskInfo tmp = new TaskInfo(taskInfo);
                            tmp.setStatus(1);
                            tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getParamName() + "}", String.valueOf(start)));
                            taskInfos.add(tmp);
                        } else {
                            if (orderFlag) {
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
                    if (firstFlag) {
                        firstFlag = false;
                        continue;
                    }
                    if (orderFlag) {
                        taskInfos.clear();
                        orderFlag = false;
                    } else {
                        tmpList.clear();
                        orderFlag = true;
                    }
                }
            }
            if (orderFlag) {
                taskInfos.forEach(t -> {
                    pipeline.pushTask(t);
                });
            } else {
                tmpList.forEach(t -> {
                    pipeline.pushTask(t);
                });
            }
        }
    }

    public static void main(String[] args) {
        TaskHandler taskHandler = new TaskHandler();
        taskHandler.initHandler(1);
    }

}
