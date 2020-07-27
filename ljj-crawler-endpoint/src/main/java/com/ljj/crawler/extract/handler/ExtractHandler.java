package com.ljj.crawler.extract.handler;

import com.alibaba.fastjson.JSON;
import com.ljj.crawler.core.mapper.ExtractMapper;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.handler.AbstractHandler;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.extract.selector.Selector;
import com.ljj.crawler.webspider.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/25 9:28
 */
@Slf4j
@Component
public class ExtractHandler implements AbstractHandler {


    private Scheduler scheduler;

    @Resource
    private ExtractMapper extractMapper;

    public void init(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 接受两种类型的信息，一种是 taskInfo，
     * 一种是父节点 extract info
     * 父节点的出处，1：从task中的response中生成
     * 2：从解析的array等信息中生成
     *
     * @param task
     */
    @Override
    public void handler(Task task) {
        log.info("extract handler start >>> task={}", JSON.toJSONString(task));
        if (scheduler == null) throw new RuntimeException("task handler 未初始化，请初始化后再进行执行");
        List<ExtractInfo> extractInfos = null;
        String pid = task.getPid();
        // 初始任务时，不从url直接开始，所以这里传入的是一个task info 信息，这里就需要通过task id 进行查找解析配置
        // 若是后续url任务，则直接通过其 id 查找其子解析信息

        if (task instanceof TaskInfo) { // 任务中没有起始url ，则直接将task_info 推过来，通过taskId查询相关解析
            extractInfos = extractMapper.findByTid(Integer.valueOf(task.getTid()));
        } else if (task instanceof ExtractInfo) {
            if (pid == null)
                extractInfos = extractMapper.findByTid(Integer.valueOf(task.getTid()));
            else extractInfos = extractMapper.findByPid(Integer.valueOf(pid));
        }

        if (extractInfos == null || extractInfos.size() < 1) {
            log.info("extract handler end >>> msg=don`t have extracts");
        } else {
            for (ExtractInfo extractInfo : extractInfos) {
                // 上个节点的result是当前节点的content
                if (task instanceof ExtractInfo) {
                    extractInfo.setContent(((ExtractInfo) task).getResult());
                    extractInfo.setContentBytes(((ExtractInfo) task).getResultBytes());
                }
                String content = extractInfo.getContent();
                Integer contentType = extractInfo.getContentType();
                if (contentType == null || contentType == 0 || contentType == 1) {
                    log.info("extract handler >>> contentType=html, selector={}, content={}", extractInfo.getSelector(), content);
                    // 默认html
                    // 如果传入的解析（即父解析）的contentType为一个link，则可以直接进行解析。
                    String selectResult = null;
                    if (task instanceof ExtractInfo) {
                        Integer ct = ((ExtractInfo) task).getContentType();
                        if (ct == null || ct != 1) {
                            selectResult = Selector.cssSelector().select(extractInfo.getContent().getBytes(), extractInfo);
                        } else {
                            selectResult = Selector.cssXmlSelector().select(extractInfo.getContent().getBytes(), extractInfo);
                        }
                    }
                    log.info("extract handler >>> contentType=html ,selectResult={}", selectResult);

                    Integer resultType = extractInfo.getResultType();
                    if (selectResult == null) {
                        log.info("extract handler end >>> select result == null");
                    } else if ((resultType == null || resultType == 1) && selectResult != null) {
                        Document document = Jsoup.parse(selectResult, "", Parser.xmlParser());
                        String result = document.text();
                        extractInfo.setResult(result);
                        log.info("extract handler end >>> push to data result={}", JSON.toJSONString(extractInfo));
                        scheduler.pushData(extractInfo);
                    } else if (resultType == 2) { // 返回的信息是一个数组
                        extractInfo.setResult(selectResult);
                        handlerHtmlArray(extractInfo);
                    } else {
                        extractInfo.setResult(selectResult);
                        log.info("extract handler end >>> push to extract result={}", JSON.toJSONString(extractInfo));
                        scheduler.pushExtract(extractInfo);
                    }

                } else if (contentType == 2) {
                } else if (contentType == 3) {
                    // link 直接生成request进行请求去
                    // TODO url 的校验
                    Request request = Request.create(extractInfo);
                    request.setParentId(String.valueOf(extractInfo.getId()));
                    log.info("extract handler end >>> contentType=link, request={}", JSON.toJSONString(request));
                    scheduler.pushRequest(request);
                } else if (contentType == 4) {
                    // 静态数据
                    extractInfo.setResult(extractInfo.getContent());
                    log.info("extract handler end >>> contentType=static, result={}", JSON.toJSONString(extractInfo));
                    scheduler.pushData(extractInfo);
                } else if (contentType == 5) {
                    String base64 = Selector.base64Selector().select(extractInfo.getContentBytes(), extractInfo);
                    extractInfo.setResult(base64);
                    log.info("extract handler end >>> contentType=base64, result={}", JSON.toJSONString(extractInfo));
                    scheduler.pushData(extractInfo);
                }
            }
        }
    }

    /**
     * 当解析的数据不是最终数据（多条），需要在其中抽取一或者多条数据的时候
     *
     * @param extractInfo
     */
    private void handlerHtmlArray(ExtractInfo extractInfo) {
        Document doc = Jsoup.parse(extractInfo.getResult(), "", Parser.xmlParser());
        List<Node> nodesTmp = doc.childNodes();
        List<ExtractInfo> childExtract = extractMapper.findByPid(extractInfo.getId());
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
            // 生成多个extract 父节点信息
            String content = nodes.get(start).outerHtml();
            ExtractInfo temp = null;
            try {
                temp = (ExtractInfo) extractInfo.clone();
            } catch (CloneNotSupportedException e) {
                log.info("extract handler error >>> clone error, e:", e);
            }
            temp.setContent(content);
            temp.setResult(null);
            scheduler.pushExtract(temp);
        }
    }
}
