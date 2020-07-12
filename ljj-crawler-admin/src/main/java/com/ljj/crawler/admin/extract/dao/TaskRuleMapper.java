package com.ljj.crawler.admin.extract.dao;

import com.ljj.crawler.admin.extract.po.TaskRule;
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

    @Select("SELECT id,task_id,param_name,param_rule_type,rule_str,reserve FROM task_rule WHERE task_id = #{task_id}")
    @Result(column = "task_id", property = "taskId")
    @Result(column = "param_name", property = "paramName")
    @Result(column = "param_rule_type", property = "paramRuleType")
    @Result(column = "rule_str", property = "ruleStr")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "taskId")
    List<TaskRule> findByTaskId(Integer taskId);
}
