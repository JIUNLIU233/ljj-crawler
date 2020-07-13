package com.ljj.crawler.admin.extract.handler;

import com.ljj.crawler.admin.extract.po.ExtractInfo;
import com.ljj.crawler.admin.extract.po.TaskInfo;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/13
 **/
public interface Pipeline {
    
    void pushTask(TaskInfo taskInfo);

    TaskInfo pullTask();

    void pushExtract(ExtractInfo extractInfo);

    ExtractInfo pullExtract();
}
