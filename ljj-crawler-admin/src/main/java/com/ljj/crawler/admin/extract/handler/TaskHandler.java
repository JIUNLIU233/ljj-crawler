package com.ljj.crawler.admin.extract.handler;

import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.admin.extract.dao.TaskInfoMapper;
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

    @Resource
    private TaskInfoMapper taskInfoMapper;

    /**
     * 一个爬虫任务初始化的处理
     */
    public void initHandler(Integer task_id) {
        //1、解析一个任务
        TaskInfo taskInfo = taskInfoMapper.findById(task_id);
        //2、判断url是否有规则
        Integer haveRule = taskInfo.getHaveRule();
        if (haveRule == 0) {//无规则
            pushTask(taskInfo);
        } else {// 有规则
            /**
             * 获取到规则之后，要生成一批taskInfo信息。
             * 规则链表，后一个规则依赖第一个规则的结果
             */
            List<TaskRule> taskRules = getTaskRule(taskInfo);

            List<TaskInfo> taskInfos = new ArrayList<>();
            List<TaskInfo> tmpList = new ArrayList<>();
            boolean firstFlag = true;// 是否是第一个规则的标示
            boolean orderFlag = true;
            for (TaskRule taskRule : taskRules) {
                if (taskRule.getParamRuleType() == 0) {
                    String ruleStr = taskRule.getRuleStr();
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
            if (orderFlag) {
                taskInfos.forEach(t -> {
                    pushTask(t);
                });
            } else {
                tmpList.forEach(t -> {
                    pushTask(t);
                });
            }
        }
    }


    public void pushTask(TaskInfo... taskInfos) {
        for (TaskInfo taskInfo : taskInfos) {
            taskInfo.setStatus(2);
            System.out.println(JSONObject.toJSONString(taskInfo));
        }
    }

    public List<TaskRule> getTaskRule(TaskInfo taskInfo) {
        //TODO 爬虫任务初始化规则的获取
        return new ArrayList<TaskRule>() {{
            add(new TaskRule() {{
                setParamName("yeshu");
                setParamRuleType(0);
                setRuleStr("1-2");
            }});
            add(new TaskRule() {{
                setParamName("yeshu1");
                setParamRuleType(0);
                setRuleStr("1-2");
            }});
            add(new TaskRule() {{
                setParamName("yeshu2");
                setParamRuleType(0);
                setRuleStr("1-2");
            }});
        }};
    }


    public static void main(String[] args) {
        TaskHandler taskHandler = new TaskHandler();
        taskHandler.initHandler(1);
    }

}
