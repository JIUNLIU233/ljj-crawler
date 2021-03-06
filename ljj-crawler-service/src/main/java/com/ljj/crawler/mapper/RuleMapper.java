package com.ljj.crawler.mapper;

import com.ljj.crawler.common.constant.TableKey;
import com.ljj.crawler.core.po.TaskRule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Mapper
public interface RuleMapper {

    @Select("select * from " + TableKey.ruleTable + " where tid=#{tid}")
    List<TaskRule> findByTid(Integer tid);

    @Insert("insert into " + TableKey.ruleTable +
            " (tid,field,ruleType,ruleParam) " +
            "values " +
            "(#{tid},#{field},#{ruleType},#{ruleParam})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertOne(TaskRule rule);
}
