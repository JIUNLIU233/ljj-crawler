package com.ljj.crawler.admin.extract.handler;

import com.ljj.crawler.admin.extract.dao.ExtractInfoMapper;
import com.ljj.crawler.admin.extract.handler.impl.LocalPipeline;
import com.ljj.crawler.admin.extract.po.ExtractInfo;
import com.ljj.crawler.admin.extract.po.TaskInfo;
import com.ljj.crawler.core.utils.RSUtils;
import com.ljj.crawler.core.utils.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private Pipeline pipeline;


    @Resource
    private ExtractInfoMapper extractInfoMapper;

    public void init(Pipeline pipeline) {
        if (pipeline == null) this.pipeline = new LocalPipeline();
        else this.pipeline = pipeline;
    }

    public void handler() {

        TaskInfo taskInfo = pipeline.pullTask();

        //第一步：对taskInfo 进行请求
        //TODO 请求下载
        String content = RSUtils.readFile("test/pages/xbqg.html");
        List<ExtractInfo> extractInfos = extractInfoMapper.findByTaskId(taskInfo.getId());
        for (ExtractInfo extractInfo : extractInfos) {
            extractInfo.setTraceId(taskInfo.getTraceId());
            handlerExtractInfo(content, extractInfo);
        }
    }


    public void handlerExtractInfo(String content, ExtractInfo extractInfo) {
        //1、获取其解析规则
        //2、根据解析规则进行解析
        String extract = extract(content, extractInfo);
        Integer resultType = extractInfo.getResultType();

        Integer haveChild = extractInfo.getHaveChild();
        if (haveChild == 0) { //  不包含子任务信息，直接进行信息封装
            String extractAttr = extractInfo.getExtractAttr();
            String result;
            if (extractAttr == null) {
                result = Jsoup.parse(extract).text();
            } else {
                Document doc = Jsoup.parse(extract, "", Parser.xmlParser());
                Element child = doc.child(0);
                result = child.attr(extractAttr);
            }
            extractInfo.setExtractResult(result);
            log.info("extract field success ,traceId={}, parentTraceId={}, field_name={},field_value={}", extractInfo.getTraceId(), extractInfo.getParentTraceId(), extractInfo.getFieldName(), result);
            pipeline.pushExtract(extractInfo);
        } else {
            if (resultType == 1) { // 节点返回信息为Array类型的。比如表格，返回为一行一行的tr
                Document doc = Jsoup.parse(extract, "", Parser.xmlParser());
                List<Node> nodesTmp = doc.childNodes();

                List<ExtractInfo> childExtract = extractInfoMapper.findByParentId(extractInfo.getId());
                if (childExtract == null || childExtract.size() < 1) return;
                CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<>();
                nodes.addAll(nodesTmp);
                nodes.removeIf(s -> {// 删除空节点信息
                    if (s == null || s.toString().trim().equalsIgnoreCase("")) return true;
                    else return false;
                });

                String arrayRange = extractInfo.getArrayRange();
                int start = 0;
                int endSub = 0;
                if (arrayRange != null && arrayRange.contains("-")) {
                    String[] split = arrayRange.split("-");
                    start = Integer.valueOf(split[0]);
                    endSub = Integer.valueOf(split[1]);
                }
                for (; start < nodes.size() - endSub; start++) {
                    String traceId = TraceUtil.traceId();
                    for (ExtractInfo info : childExtract) {
                        if (info.getParentTraceId() == null) info.setParentTraceId(new ArrayList<>());
                        info.setTraceId(traceId);
                        int size = info.getParentTraceId().size();
                        if (!(info.getParentTraceId().size() > 0) || !info.getParentTraceId().get(size - 1).equalsIgnoreCase(extractInfo.getTraceId()))
                            info.getParentTraceId().add(extractInfo.getTraceId());
                        handlerExtractInfo(nodes.get(start).outerHtml(), info);
                    }
                }
            }
        }
    }


    /**
     * 选择器
     * 0:css
     * 1:xpath
     * 2:正则
     * 3：js
     * 默认选择css选择器
     *
     * @param content 源数据
     * @return
     */
    static String extract(String content, ExtractInfo extractInfo) {
        switch (extractInfo.getExtractType()) {
            case 1:
                return xPathExtract(content, extractInfo.getExtractParam());
            case 2:
                return regexExtract(content, extractInfo.getExtractParam());
            case 3:
                return jsExtract(content, extractInfo.getExtractParam());
            default:
                return cssExtract(content, extractInfo.getExtractParam());
        }
    }

    /**
     * css 选择器选择
     *
     * @param content
     * @param parseRule
     * @return
     */
    static String cssExtract(String content, String parseRule) {
        Document document = Jsoup.parse(content, "", Parser.xmlParser());
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
