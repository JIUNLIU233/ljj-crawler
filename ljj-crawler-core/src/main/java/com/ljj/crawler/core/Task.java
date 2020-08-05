package com.ljj.crawler.core;

import java.util.List;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 8:59
 */
public interface Task {

    /**
     * 获取任务id
     *
     * @return
     */
    String getTid();

    /**
     * 获取父id
     *
     * @return
     */
    String getPId();

    /**
     * 获取流程id
     *
     * @return
     */
    String getTraceId();

    /**
     * 获取父流程id集合
     *
     * @return
     */
    List<String> getPTraceId();

}
