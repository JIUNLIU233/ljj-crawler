package com.ljj.crawler.core.mapper;

import com.ljj.crawler.core.po.ExtractInfo;
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
public interface ExtractMapper {

    @Select("select * from extract_info where tid=#{tid} and (ISNULL(pid) or pid='')")
    List<ExtractInfo> findByTid(Integer tid);

    @Select("select * from extract_info where pid=#{pid}")
    List<ExtractInfo> findByPid(Integer pid);


    @Insert("insert into extract_info " +
            "(tid,pid,content,contentType,selector,selectorAttr,resultType,mount,arrayRange) " +
            "values " +
            "(#{tid},#{pid},#{content},#{contentType},#{selector},#{selectorAttr},#{resultType},#{mount},#{arrayRange})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(ExtractInfo extractInfo);

}
