package com.ljj.crawler.admin.extract.dao;

import com.ljj.crawler.admin.extract.po.ExtractInfo;
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
 * @data : 2020/7/12 16:52
 */
@Mapper
public interface ExtractInfoMapper {
    @Select("SELECT id,task_id,field_name,extract_type,extract_param,result_type" +
            ",save_type,extract_flag,extract_url_rule FROM extract_info WHERE task_id = #{task_id}")
    @Result(column = "task_id", property = "taskId")
    @Result(column = "field_name", property = "fieldName")
    @Result(column = "extract_type", property = "extractType")
    @Result(column = "extract_param", property = "extractParam")
    @Result(column = "result_type", property = "resultType")
    @Result(column = "save_type", property = "saveType")
    @Result(column = "extract_flag", property = "extractFlag")
    @Result(column = "extract_url_rule", property = "extractUrlRule")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "taskId")
    List<ExtractInfo> findByTaskId(Integer taskId);
}
