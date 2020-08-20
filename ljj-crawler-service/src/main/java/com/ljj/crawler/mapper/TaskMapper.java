package com.ljj.crawler.mapper;

import com.ljj.crawler.core.po.TaskInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import static com.ljj.crawler.common.constant.TableKey.taskTable;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Mapper
public interface TaskMapper {

    @Select("select * from " + taskTable + " where id=#{id}")
    TaskInfo findById(Integer id);


    @Insert("insert into " + taskTable +
            " (name,startUrl,comment) " +
            "values" +
            " (#{name},#{startUrl},#{comment})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertOne(TaskInfo taskInfo);
}
