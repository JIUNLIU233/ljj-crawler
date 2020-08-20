package com.ljj.crawler.mapper;

import com.ljj.crawler.core.po.ExtractInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import static com.ljj.crawler.common.constant.TableKey.extractTable;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
@Mapper
public interface ExtractMapper {

    @Select("select * from " + extractTable + " where tid=#{tid} and (ISNULL(pid) or pid='')")
    List<ExtractInfo> findByTid(Integer tid);

    @Select("select * from " + extractTable + " where pid=#{pid}")
    List<ExtractInfo> findByPid(Integer pid);


    @Insert("insert into " + extractTable +
            " (tid,pid,content,contentType,selector,selectorAttr,resultType,mount,arrayRange) " +
            "values " +
            "(#{tid},#{pid},#{content},#{contentType},#{selector},#{selectorAttr},#{resultType},#{mount},#{arrayRange})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(ExtractInfo extractInfo);

}
