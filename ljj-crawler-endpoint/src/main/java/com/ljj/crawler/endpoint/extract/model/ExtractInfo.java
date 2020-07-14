package com.ljj.crawler.endpoint.extract.model;

import com.ljj.crawler.endpoint.extract.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 13:29
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExtractInfo implements Task {
    private Integer id; // 主键id
    private Integer taskId; // 解析绑定一个爬虫页面
    private Integer parentId;//如果为子解析时，其父解析的主键id信息
    private String fieldName;// 要解析的字段，存储时的字段名称
    /**
     * 0:html
     * 1:json
     * 2:string
     * 3:js
     * 4:file
     */
    @Builder.Default
    private Integer extractType = 0;// 页面类型（html，json，string，file）对应的解析方式分别是：css,json，正则，文件处理
    private String extractAttr;// 要选择的元素属性 名称
    private String extractParam;//用于解析器的参数
    private Integer resultType;// 解析返回类型 0：string，1：array，2：node
    private String saveType;// 保存方式
    @Builder.Default
    private Integer haveChild = 0;// 是否具有子解析 0:不需要，1需要
    private String arrayRange; // 如果为array的时候，设置其前面舍弃的数据数量和后面舍弃的数量值。

    // 以上信息为通数据库配置信息，以下信息为代码中需要用到的信息
    private String traceId; // 在执行流程中的id
    private List<String> parentTraceId = new ArrayList<>();// 在执行流程中的父id
    private String extractResult;// 执行结果

    @Override
    public void setTaskId(String tid) {

    }

    @Override
    public void addParentTraceId(String traceId) {
        this.parentTraceId.add(traceId);
    }
}
