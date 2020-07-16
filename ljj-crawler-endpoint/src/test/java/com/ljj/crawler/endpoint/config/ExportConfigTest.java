package com.ljj.crawler.endpoint.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.endpoint.extract.mapper.ExtractInfoMapper;
import com.ljj.crawler.endpoint.extract.mapper.TaskInfoMapper;
import com.ljj.crawler.endpoint.extract.mapper.TaskRuleMapper;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.model.TaskInfo;
import com.ljj.crawler.endpoint.extract.model.TaskRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/16
 **/
@SpringBootTest
public class ExportConfigTest {

    @Resource
    TaskInfoMapper taskInfoMapper;

    @Resource
    TaskRuleMapper taskRuleMapper;

    @Resource
    ExtractInfoMapper extractInfoMapper;

    @Test
    public void testExport() {
        Integer exportTid = 1;
        JSONObject result = new JSONObject();
        TaskInfo taskInfo = taskInfoMapper.findById(exportTid);
        String s = JSON.toJSONString(taskInfo);
        JSONObject value = JSON.parseObject(s);
        value.remove("id");
        value.remove("taskId");
        value.remove("parentTraceId");
        value.remove("status");
        result.put("task_info", value);

        List<TaskRule> taskRuleList = taskRuleMapper.findByTaskId(exportTid);
        JSONArray taskRules = new JSONArray();

        for (TaskRule taskRule : taskRuleList) {
            String s1 = JSON.toJSONString(taskRule);
            JSONObject e = JSON.parseObject(s1);
            e.remove("id");
            e.remove("taskId");
            taskRules.add(e);
        }

        result.put("task_rules", taskRules);

        List<ExtractInfo> extractInfos = extractInfoMapper.findByTaskId(exportTid);


        JSONArray exs = new JSONArray();
        for (ExtractInfo extractInfo : extractInfos) {
            Integer haveChild = extractInfo.getHaveChild();
            String s1 = JSON.toJSONString(extractInfo);
            JSONObject jsonObject = JSON.parseObject(s1);
            jsonObject.remove("id");
            jsonObject.remove("parentTraceId");
            jsonObject.remove("taskId");
            jsonObject.remove("parentId");
            if (haveChild == 1) {
                searchChild(extractInfo, jsonObject);
            }
            exs.add(jsonObject);
        }

        result.put("extract_infos", exs);

        System.out.println(result);
    }

    public JSONObject searchChild(ExtractInfo extractInfo, JSONObject result) {
        List<ExtractInfo> extractInfos = extractInfoMapper.findByParentId(extractInfo.getId());
        if (extractInfos != null && extractInfos.size() > 0) {
            JSONArray child = new JSONArray();
            for (ExtractInfo info : extractInfos) {
                String s = JSON.toJSONString(info);
                JSONObject jsonObject = JSON.parseObject(s);
                jsonObject.remove("id");
                jsonObject.remove("parentTraceId");
                jsonObject.remove("taskId");
                jsonObject.remove("parentId");
                if (info.getHaveChild() == 1) { // 如果还是含有子节点
                    searchChild(info, jsonObject);
                } else {
                    child.add(jsonObject);
                }
            }
            result.put("child", child);

        }

        return result;
    }
}
