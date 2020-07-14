package com.ljj.crawler.endpoint.extract.mapper;

import com.ljj.crawler.endpoint.extract.model.TaskRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 16:17
 */
@Mapper
public interface TaskRuleMapper {

    @Select("SELECT id,task_id,param_name,rule_type,rule_param FROM task_rule WHERE task_id = #{task_id}")
    @Result(column = "task_id", property = "taskId")
    @Result(column = "param_name", property = "paramName")
    @Result(column = "rule_type", property = "ruleType")
    @Result(column = "rule_param", property = "ruleParam")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "taskId")
    List<TaskRule> findByTaskId(Integer taskId);
}
