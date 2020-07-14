package com.ljj.crawler.endpoint.extract.mapper;

import com.ljj.crawler.endpoint.extract.constant.TableKey;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
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


    @Select("SELECT id,task_id,parent_id,field_name,extract_type,extract_param,result_type" +
            ",save_type,have_child,array_range,extract_attr FROM " + TableKey.EXTRACT + " WHERE task_id = #{task_id}  AND ISNULL(parent_id) ")
    @Result(column = "task_id", property = "taskId")
    @Result(column = "parent_id", property = "parentId")
    @Result(column = "field_name", property = "fieldName")
    @Result(column = "extract_type", property = "extractType")
    @Result(column = "extract_param", property = "extractParam")
    @Result(column = "result_type", property = "resultType")
    @Result(column = "save_type", property = "saveType")
    @Result(column = "have_child", property = "haveChild")
    @Result(column = "array_range", property = "arrayRange")
    @Result(column = "extract_attr", property = "extractAttr")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    List<ExtractInfo> findByTaskId(Integer taskId);

    @Select("SELECT id,task_id,parent_id,field_name,extract_type,extract_param,result_type" +
            ",save_type,have_child,array_range,extract_attr FROM " + TableKey.EXTRACT + " WHERE parent_id = #{parentId}")
    @Result(column = "task_id", property = "taskId")
    @Result(column = "parent_id", property = "parentId")
    @Result(column = "field_name", property = "fieldName")
    @Result(column = "extract_type", property = "extractType")
    @Result(column = "extract_param", property = "extractParam")
    @Result(column = "result_type", property = "resultType")
    @Result(column = "save_type", property = "saveType")
    @Result(column = "have_child", property = "haveChild")
    @Result(column = "array_range", property = "arrayRange")
    @Result(column = "extract_attr", property = "extractAttr")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    List<ExtractInfo> findByParentId(Integer parentId);
}
