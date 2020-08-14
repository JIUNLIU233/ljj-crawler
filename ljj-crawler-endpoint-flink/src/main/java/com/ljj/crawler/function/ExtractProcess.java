package com.ljj.crawler.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.AppContext;
import com.ljj.crawler.common.utils.MountUtils;
import com.ljj.crawler.common.utils.TraceUtil;
import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.core.Task;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.core.po.TaskInfo;
import com.ljj.crawler.mapper.ExtractMapper;
import com.ljj.crawler.po.StreamData;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.selector.Selector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 针对解析的处理
 * <p>
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
@EnableAutoConfiguration
@MapperScan("com.ljj.crawler.mapper")
@Slf4j
public class ExtractProcess extends ProcessFunction<StreamData, StreamData> {
    private OutputTag<String> outputTag;
    private ExtractMapper extractMapper;

    public ExtractProcess() {
    }

    public ExtractProcess(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    public void setOutputTag(OutputTag<String> outputTag) {
        this.outputTag = outputTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        extractMapper = AppContext.getBean(ExtractMapper.class);
    }

    @Override
    public void processElement(StreamData value, Context ctx, Collector<StreamData> out) throws Exception {
        String data = value.getData();
        log.info("extract process start >>> offset={}", value.getOffset());
        // TODO 收到消息，设置其为处理状态

        List<String> sourcePTraceId = null;
        try {
            String dataType = value.getDataType();
            List<ExtractInfo> extractInfos = null;
            String pid;
            if (CReceive.taskHandlerKey.equalsIgnoreCase(dataType)) {
                TaskInfo taskInfo = JSONObject.parseObject(data, TaskInfo.class);
                extractInfos = extractMapper.findByTid(taskInfo.getId());
            } else if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                ExtractInfo extractInfo = JSONObject.parseObject(data, ExtractInfo.class);
                sourcePTraceId = extractInfo.getPTraceId();
                pid = extractInfo.getPId();
                if (pid == null || "null".equalsIgnoreCase(pid)) {
                    extractInfos = extractMapper.findByTid(Integer.valueOf(extractInfo.getTid()));
                } else {
                    extractInfos = extractMapper.findByPid(Integer.valueOf(pid));
                }
            }

            if (extractInfos == null || extractInfos.size() < 1) {
                log.info("extract handler end >>> offset={}, msg=don`t have extracts", value.getOffset());
                //TODO 数据已经处理完毕
            } else {
                String traceIdTemp = TraceUtil.traceId();
                boolean newTraceFlag = false;
                for (ExtractInfo extractInfo : extractInfos) {
                    // 上个节点的result是当前节点的content
                    if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                        ExtractInfo sourceExtract = JSONObject.parseObject(data, ExtractInfo.class);

                        extractInfo.setContent(sourceExtract.getResult());
                        extractInfo.setContentBytes(sourceExtract.getResultBytes());
                        extractInfo.setCurUrl(sourceExtract.getCurUrl());
                    }

                    JSONObject dataJson = JSONObject.parseObject(data);

                    // 数据挂载的处理
                    String mount = extractInfo.getMount();
                    if (newTraceFlag || (mount != null && mount.contains("[new]"))) {
                        //TODO 需要进行 同一个子排列下面的traceId 是一样的。最好的办法就是给父节点设置newTraceId。

                        String traceId = dataJson.getString("traceId");
                        extractInfo.setTraceId(traceIdTemp);
                        List<String> pTraceId = extractInfo.getPTraceId();
                        if (pTraceId.size() < 1 || !pTraceId.get(pTraceId.size() - 1).equalsIgnoreCase(traceId)) {
                            pTraceId.add(traceId);
                        }
                        if (sourcePTraceId.size() < 1 || !sourcePTraceId.get(sourcePTraceId.size() - 1).equalsIgnoreCase(traceId)) {
                            sourcePTraceId.add(traceId);
                        }

                        newTraceFlag = true;

                    } else {
                        extractInfo.setTraceId(dataJson.getString("traceId"));
                        extractInfo.setPTraceId(sourcePTraceId);
                    }


                    Integer contentType = extractInfo.getContentType();

                    /**
                     * 这个分支下，为对应的html 返回内容的处理
                     */
                    if (contentType == null || contentType == 0 || contentType == 1) {
                        String selector = extractInfo.getSelector();
                        String selectorAttr = extractInfo.getSelectorAttr();
                        log.info("extract handler >>> offset={} , contentType=html, selector={} , selectorAttr={}", value.getOffset(), selector, selectorAttr);
                        // 默认html
                        // 如果传入的解析（即父解析）的contentType为一个link，则可以直接进行解析。
                        String selectResult = null;
                        if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                            ExtractInfo sourceExtract = JSONObject.parseObject(data, ExtractInfo.class);
                            Integer ct = sourceExtract.getContentType();
                            if (StringUtils.isEmpty(selector)) {
                                selectResult = extractInfo.getContent();
                            } else if (ct == null || ct != 1) {
                                selectResult = Selector.cssSelector().select(extractInfo.getContent().getBytes(), extractInfo);
                            } else {
                                selectResult = Selector.cssXmlSelector().select(extractInfo.getContent().getBytes(), extractInfo);
                            }
                        }
                        Integer resultType = extractInfo.getResultType();
                        if (selectResult == null) {
                            log.info("extract handler end >>> offset={}, msg = 'select result == null'", value.getOffset());
                        } else if ((resultType == null || resultType == 1) && selectResult != null) {
                            /**
                             * 此分支为 提取element中的text
                             */
                            if (StringUtils.isEmpty(extractInfo.getSelectorAttr())) {
                                Document document = Jsoup.parse(selectResult, "", Parser.xmlParser());
                                extractInfo.setResult(document.text());
                            } else {
                                extractInfo.setResult(selectResult);
                            }
                            extractInfo.setContentBytes(null);
                            log.info("extract handler end >>> offset={} , contentType=html , selectResult={}", value.getOffset(), extractInfo.getResult());
                            outSide(ctx, CReceive.dataHandlerKey, extractInfo, CReceive.dataHandlerKey, value.getOffset());
                        } else if (resultType == 2) { // 返回的信息是一个数组
                            extractInfo.setResult(selectResult);
                            log.info("extract handler >>> offset={} , contentType=html , resultType={}", value.getOffset(), "array");
                            handlerHtmlArray(extractInfo, ctx, value.getOffset());
                            log.info("extract handler end >>> offset={} , contentType=html , resultType={}", value.getOffset(), "array");
                        } else if (resultType == 3) { // 解析的结果为一个链接
                            log.info("extract handler >>> offset={} , contentType=html , resultType={}", value.getOffset(), "link");
                            extractInfo.setResult(selectResult);
                            handlerLink(extractInfo, ctx, value.getOffset());
                            log.info("extract handler end >>> offset={} , contentType=html , resultType={}", value.getOffset(), "link");
                        } else {
                            extractInfo.setResult(selectResult);
                            extractInfo.setContentBytes(null);
                            outSide(ctx, CReceive.dataHandlerKey, extractInfo, CReceive.dataHandlerKey, value.getOffset());
                        }

                    }
                    /**
                     *  这个分支下，处理 json 信息
                     */
                    else if (contentType == 2) {

                    }
                    /**
                     *  这个分支下 处理 链接信息
                     */
                    else if (contentType == 3) {
                        // link 直接生成request进行请求去
                        // TODO url 的校验
                        log.info("extract handler >>> offset={} , contentType=link", value.getOffset());
                        Request request = Request.create(extractInfo);
                        request.setParentId(String.valueOf(extractInfo.getId()));
                        log.info("extract handler end >>> offset={} , contentType=link", value.getOffset());

                        outSide(ctx, CReceive.downloadHandlerKey, request, CReceive.downloadHandlerKey, value.getOffset());
                    }
                    /**
                     *  这个分支下处理 从 配置中心配置的静态数据信息
                     */
                    else if (contentType == 4) {
                        // 静态数据
                        log.info("extract handler >>> offset={} , contentType=static", value.getOffset());
                        extractInfo.setResult(extractInfo.getContent());
                        extractInfo.setContentBytes(null);
                        log.info("extract handler end >>> offset={} , contentType=static", value.getOffset());
                        outSide(ctx, CReceive.dataHandlerKey, extractInfo, CReceive.dataHandlerKey, value.getOffset());
                    }
                    /**
                     * 这个分支下处理base64 进行编码原始返回信息
                     */
                    else if (contentType == 5) {
                        // base64 信息处理
                        log.info("extract handler >>> offset={} , contentType=base64", value.getOffset());
                        String base64 = Selector.base64Selector().select(extractInfo.getContentBytes(), extractInfo);
                        extractInfo.setResult(base64);
                        extractInfo.setContentBytes(null);
                        log.info("extract handler end >>> offset={} , contentType=base64", value.getOffset());
                        outSide(ctx, CReceive.dataHandlerKey, extractInfo, CReceive.dataHandlerKey, value.getOffset());
                    }
                }

                //TODO 同一层的数据解析完毕后，进行ack

            }
        } catch (Exception e) {
            // TODO 消息处理异常，设置其为异常状态即可。
        }
    }


    /**
     * 当解析的数据不是最终数据（多条），需要在其中抽取一或者多条数据的时候
     *
     * @param extractInfo
     */
    private void handlerHtmlArray(ExtractInfo extractInfo, Context ctx, long offset) {
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
            temp.setResult(content);
            temp.setResultBytes(content.getBytes());
            temp.setPid(extractInfo.getId());
            // 避免无用数据流转，占用资源
            temp.setContent(null);
            temp.setContentBytes(null);
            outSide(ctx, CReceive.extractHandlerKey, temp, CReceive.extractHandlerKey, offset);
        }
    }


    private void handlerLink(ExtractInfo extractInfo, Context ctx, long offset) {
        String curUrl = extractInfo.getCurUrl();
        String linkUrl = extractInfo.getResult();
        if (StringUtils.isNotEmpty(linkUrl)) {
            Request request = Request.create(extractInfo);
            if (linkUrl.startsWith("http")) { // 是一个完整链接
                request.setUrl(linkUrl);
            } else {    // 不是一个完整的http 链接。
                /*
                 * 可能存在的情况：
                 *      www.baidu.com/xxx   这个现需要添加协议
                 *      //www.baidu.com/xxx 这个现需要处理协议和分隔符
                 *      baidu.com/xxx       这个需要处理其前缀
                 *      /xxx/xxx/xxx        这个需要补充host和协议
                 *      xxx/xxx/xxx         这个需要补充host、协议、以及对应的分隔符
                 */
                try {
                    URL url = new URL(curUrl);
                    String host = url.getHost();
                    String protocol = url.getProtocol();

                    String newUrl;
                    if (linkUrl.contains(host)) {
                        int i = linkUrl.indexOf(host) + host.length();
                        String substring = linkUrl.substring(i);
                        newUrl = protocol + "://" + host + substring;
                    } else {
                        if (linkUrl.startsWith("/")) {
                            newUrl = protocol + "://" + host + linkUrl;
                        } else {
                            newUrl = protocol + "://" + host + "/" + linkUrl;
                        }
                    }

                    request.setUrl(newUrl);

                } catch (MalformedURLException e) { // 正常情况下，这里不会出问题。
                    e.printStackTrace();
                    log.error("handlerLink error >>> pass curUrl to URL error:", e);
                }


            }
            if (StringUtils.isNotEmpty(request.getUrl()))
                outSide(ctx, CReceive.downloadHandlerKey, request, CReceive.downloadHandlerKey, offset);
        }
    }


    private void outSide(Context ctx, String receive, Task data, String dataType, long offset) {
        StreamData cycleStreamData = new StreamData(
                receive,
                JSONObject.toJSONString(data),
                dataType);
        log.info("extract handler end >>> offset={}, result={}", offset, cycleStreamData);
        ctx.output(outputTag, JSONObject.toJSONString(cycleStreamData));
    }
}
