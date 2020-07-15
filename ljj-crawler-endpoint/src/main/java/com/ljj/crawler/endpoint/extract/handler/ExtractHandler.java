package com.ljj.crawler.endpoint.extract.handler;

import com.ljj.crawler.endpoint.extract.Task;
import com.ljj.crawler.endpoint.extract.mapper.ExtractInfoMapper;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import com.ljj.crawler.endpoint.extract.scheduler.Scheduler;
import com.ljj.crawler.endpoint.extract.selector.Selector;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;
import com.ljj.crawler.endpoint.utils.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 解析配置的处理
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
@Slf4j
@Component
public class ExtractHandler implements Handler {

    @Resource
    private ExtractInfoMapper extractInfoMapper;
    private Scheduler scheduler;

    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 传递过来的是一个 response，此时 搜索其对应的配置文件对其进行配置
     *
     * @param task
     */
    @Override
    public void handler(Task task) {
        List<ExtractInfo> extractInfos = extractInfoMapper.findByTaskId(Integer.valueOf(task.getTaskId()));
        if (extractInfos == null || extractInfos.size() < 1) {
            log.info("task_id={},msg={}", task.getTaskId(), "当前任务无解析配置！");
            return;
        }
        if (task instanceof Response) {
            Response response = (Response) task;
            for (ExtractInfo extractInfo : extractInfos) {
                extractInfo.setTraceId(response.getTraceId());
                handlerExtractInfo(response, extractInfo);
            }

        } else {
            throw new RuntimeException("参数传递错误！！！，传入非response信息");
        }
    }

    /**
     * 实际处理解析
     *
     * @param response
     * @param extractInfo
     */
    private void handlerExtractInfo(Response response, ExtractInfo extractInfo) {
        // extractInfo中应该包含页面类型（html，json，string，file），然后通过页面类型对其进行处理
        String extract = Selector.selector(response, extractInfo);
        Integer resultType = extractInfo.getResultType();
        Integer haveChild = extractInfo.getHaveChild();
        Integer extractType = extractInfo.getExtractType();

        if (haveChild == 0) { //  不包含子任务信息，直接进行信息封装,
            /**
             * 叶子节点肯定是单条数据
             * 可能为字符串数据类型
             * 也可能为文件数据类型，若是文件数据类型，则这里的result保存的是 文件存储地址
             */
            if (extractType == 0) handlerHtmlString(extract, extractInfo);
            log.info("extract field success ,traceId={}, parentTraceId={}, field_name={},field_value={}",
                    extractInfo.getTraceId(), extractInfo.getParentTraceId(), extractInfo.getFieldName(), extractInfo.getExtractResult());
            scheduler.pushExtract(extractInfo);
        } else {// 有子解析任务
            // 当前解析方式为 html解析，并且当前返回类型为array的时候，比如表格的行，图书的章节列表（即多结果，并且多条结果中又包含一个或多个字段信息）
            extractInfo.setExtractResult(extract);
            if (extractType == 0 && resultType == 1) handlerHtmlArray(response, extractInfo);
        }
    }


    /**
     * 处理html页面，从中解析出单条数据信息
     *
     * @param extractResult
     * @param extractInfo
     */
    private void handlerHtmlString(String extractResult, ExtractInfo extractInfo) {
        String extractAttr = extractInfo.getExtractAttr();
        String result;
        if (extractAttr == null) {
            result = Jsoup.parse(extractResult).text();
        } else {
            Document doc = Jsoup.parse(extractResult, "", Parser.xmlParser());
            Element child = doc.child(0);
            result = child.attr(extractAttr);
        }
        extractInfo.setExtractResult(result);
    }

    /**
     * 当解析的数据不是最终数据（多条），需要在其中抽取一或者多条数据的时候
     *
     * @param response
     * @param extractInfo
     */
    private void handlerHtmlArray(Response response, ExtractInfo extractInfo) {
        Document doc = Jsoup.parse(extractInfo.getExtractResult(), "", Parser.xmlParser());
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
                info.setTraceId(traceId);
                int size = info.getParentTraceId().size();
                if (!(info.getParentTraceId().size() > 0) || !info.getParentTraceId().get(size - 1).equalsIgnoreCase(extractInfo.getTraceId()))
                    info.getParentTraceId().add(extractInfo.getTraceId());
                Response res = new Response(response);
                res.setResponseBytes(nodes.get(start).outerHtml().getBytes());
                handlerExtractInfo(res, info);
            }
        }
    }


}
