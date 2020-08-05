package com.ljj.crawler.core.mapper;

import com.ljj.crawler.core.po.TaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Mapper
public interface TaskMapper {

    @Select("select * from task_info where id=#{id}")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    TaskInfo findById(Integer id);
}
