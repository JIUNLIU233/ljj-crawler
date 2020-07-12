package com.ljj.crawler.admin.extract.handler;

import com.ljj.core.utils.RSUtils;
import com.ljj.crawler.admin.extract.dao.ExtractInfoMapper;
import com.ljj.crawler.admin.extract.po.ExtractInfo;
import com.ljj.crawler.admin.extract.po.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能：
 * 对任务进行解析工作
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 16:44
 */
@Slf4j
@Component
public class ExtractHandler {

    @Resource
    private ExtractInfoMapper extractInfoMapper;

    public void handler(TaskInfo taskInfo) {
        //第一步：对taskInfo 进行请求
        //TODO 请求下载

        String content = RSUtils.readFile("test/pages/table.html");
        List<ExtractInfo> extractInfos = extractInfoMapper.findByTaskId(taskInfo.getId());
        for (ExtractInfo extractInfo : extractInfos) {
            //1、获取其解析规则
            //2、根据解析规则进行解析
            String extract = extract(content, extractInfo.getExtractParam(), extractInfo.getExtractType());
            String resultType = extractInfo.getResultType();
            if (resultType != null && resultType.equalsIgnoreCase("string")) {
                String fieldsValue = Jsoup.parse(extract).text();
                log.info("field_name:{}, field_value:{}", extractInfo.getFieldName(), fieldsValue);
            }
            Integer extractFlag = extractInfo.getExtractFlag();// 是否需要进行下一步解析
            if (extractFlag == 0) continue;
            //TODO 进行下一步解析的时候，一种是，继续进行解析请求，另一种是：生成子任务，
            /**
             * 线性直接解析
             *      优点：方便代码处理
             *      缺点：不利于分布式异步请求
             * 生成子任务方式：
             *      优点：利于分布式异步请求
             *
             * 继续处理又分为：继续解析和继续请求
             *
             */
            String extractUrlRule = extractInfo.getExtractUrlRule();
            if (extractUrlRule == null) {// 当子任务链接规则为空时，访问子解析规则：比如表格


            }


        }
    }


    /**
     * 选择器
     *
     * @param content     源数据
     * @param selector    抽取规则
     * @param extractType 抽取方式
     * @return
     */
    static String extract(String content, String selector, String extractType) {
        if (extractType.equalsIgnoreCase("xpath")) {
            return xPathExtract(content, selector);
        }


        return cssExtract(content, selector);

    }

    /**
     * css 选择器选择
     *
     * @param content
     * @param parseRule
     * @return
     */
    static String cssExtract(String content, String parseRule) {
        Document document = Jsoup.parse(content);
        return document.select(parseRule).outerHtml();
    }

    /**
     * xpath 选择器
     *
     * @param content
     * @param parseRule
     * @return
     */
    static String xPathExtract(String content, String parseRule) {

        return null;
    }

    /**
     * js选择器
     *
     * @param content
     * @param jsCode
     * @return
     */
    static String jsExtract(String content, String jsCode) {
        return null;
    }

    /**
     * 正则选择器
     *
     * @param content
     * @param jsCode
     * @return
     */
    static String regexExtract(String content, String jsCode) {
        return null;
    }

}
