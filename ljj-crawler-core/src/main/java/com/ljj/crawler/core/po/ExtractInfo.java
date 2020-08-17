package com.ljj.crawler.core.po;

import com.ljj.crawler.core.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：解析配置类
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:48
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ExtractInfo implements Task, Cloneable {
    /**
     * 以下为用于流程跟踪的属性
     */
    private Integer id;         // 主键id
    private Integer tid;        // 任务id
    private Integer pid;        // 父解析的主键id
    private String traceId;     // 流程id
    private List<String> pTraceId = new ArrayList<>();// 父流程id信息

    /**
     * 以下为用于解析的属性
     */

    private String content;     // 本次解析需要解析的内容
    private byte[] contentBytes;// 本次解析的二进制内容
    // 本次解析内容的类型 1：html，2：json,3:link,4:static,5:base64图片信息
    private Integer contentType;
    private String selector;    // 本次解析的参数信息
    private String selectorAttr;// 解析html中的属性参数
    private Integer resultType;  // 本次解析结果的类型 1：string，2：array ， 3：link
    private String result;      // 本次解析的结果
    private byte[] resultBytes; // 本次解析内容的字节数组
    private String mount;      // 本次解析结果的数据存储挂载点
    private String arrayRange; // 数组的选择范围


    private String curUrl;    // 本次解析对应的页面的链接。

    public static ExtractInfo create(Task task) {
        ExtractInfo extractInfo = new ExtractInfo();
        extractInfo.setTid(task.getTid() == null ? null : Integer.valueOf(task.getTid()));
        extractInfo.setTraceId(task.getTraceId());
        try {
            Integer integer = Integer.valueOf(task.getPId());
            extractInfo.setPid(task.getPId() == null ? null : integer);
        } catch (Exception e) {
        }
        extractInfo.setPTraceId(task.getPTraceId());
        return extractInfo;
    }

    @Override
    public String getTid() {
        return tid == null ? null : String.valueOf(tid);
    }

    @Override
    public String getPId() {
        return pid == null ? null : String.valueOf(pid);
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public List<String> getPTraceId() {
        return pTraceId;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        ExtractInfo extractInfo;
        extractInfo = (ExtractInfo) super.clone();
        return extractInfo;
    }
}
