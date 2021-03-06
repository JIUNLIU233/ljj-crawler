package com.ljj.crawler.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.AppContext;
import com.ljj.crawler.common.utils.TraceUtil;
import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.po.TaskRule;
import com.ljj.crawler.mapper.RuleMapper;
import com.ljj.crawler.mapper.TaskMapper;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 针对任务数据的处理
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@Slf4j
@EnableAutoConfiguration
@MapperScan("com.ljj.crawler.mapper")
public class TaskProcess extends ProcessFunction<StreamData, StreamData> {

    private OutputTag<String> outputTag;

    private TaskMapper taskMapper;
    private RuleMapper ruleMapper;

    public TaskProcess() {
    }

    public TaskProcess(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    public void setOutputTag(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        taskMapper = AppContext.getBean(TaskMapper.class);
        ruleMapper = AppContext.getBean(RuleMapper.class);
    }

    @Override
    public void processElement(StreamData value, Context ctx, Collector<StreamData> out) throws Exception {
        String data = value.getData();
        log.info("task process start >>> data={}", data);


        // TODO 更行任务状态为 执行ing状态。

        TaskInfo task = JSONObject.toJavaObject(JSONObject.parseObject(data), TaskInfo.class);

        TaskInfo taskInfo = taskMapper.findById(Integer.valueOf(task.getTid()));
        //2、对task Info做出相对必要的校验信息
        List<TaskRule> rules = ruleMapper.findByTid(Integer.valueOf(taskInfo.getTid()));
        String startUrl1 = taskInfo.getStartUrl();
        if (rules == null || rules.size() < 1) { // 链接没有规则信息
            log.info("task process don`t have rule >>>");
            taskInfo.setTraceId(TraceUtil.traceId()); // 生成一个traceId。
            if (startUrl1 == null || "".equalsIgnoreCase(startUrl1)) {
                // 为空的时候，证明不需要进行对应的request生成，只需要进行下一步即可。
                log.info("task process pushExtract >>> extract={}", JSON.toJSONString(taskInfo));

                StreamData cycleStreamData = new StreamData() {{
                    setReceive(CReceive.extractHandlerKey);
                    setData(JSONObject.toJSONString(taskInfo));
                    setDataType(CReceive.taskHandlerKey);
                }};
                ctx.output(outputTag, JSONObject.toJSONString(cycleStreamData));
                log.info("task process sideOut >>> tag={},data={}", outputTag, cycleStreamData);

            } else {
                Request request = Request.create(taskInfo);
                log.info("task process pushRequest >>> request={}", JSON.toJSONString(request));
                outDownLoad(ctx, request);
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
                            log.info("task process pushRequest >>> request={}", JSON.toJSONString(request));
                            outDownLoad(ctx, request);
                        });
                    } else {
                        tmpList.forEach(t -> {
                            t.setTraceId(TraceUtil.traceId());
                            Request request = Request.create(t);
                            log.info("task process pushRequest >>> request={}", JSON.toJSONString(request));
                            outDownLoad(ctx, request);
                        });
                    }
                }
            }

        }
        out.collect(value); // 主流不断
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
                tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", String.valueOf(start)));
                taskInfos.add(tmp);
            } else {
                if (flagTmp.get("orderFlag")) {
                    for (TaskInfo info : taskInfos) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", String.valueOf(start)));
                        tmpList.add(tmp);
                    }
                } else {
                    for (TaskInfo info : tmpList) {
                        TaskInfo tmp = new TaskInfo(info);
                        tmp.setStartUrl(tmp.getStartUrl().replace("{" + taskRule.getField() + "}", String.valueOf(start)));
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


    public void outDownLoad(Context ctx, Request request) {
        StreamData streamData = new StreamData() {{
            setReceive(CReceive.downloadHandlerKey);
            setData(JSONObject.toJSONString(request));
            setDataType(CReceive.downloadHandlerKey);
        }};
        ctx.output(outputTag, JSONObject.toJSONString(streamData));
        log.info("task process sideOut >>> tag={},data={}", outputTag, streamData);
    }
}
