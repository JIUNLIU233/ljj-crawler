package com.ljj.crawler.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.utils.AppContext;
import com.ljj.crawler.common.utils.TraceUtil;
import com.ljj.crawler.contant.CReceive;
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
        log.info("extract process start >>> data={}", data);
        // TODO 收到消息，设置其为处理状态
        try {
            String dataType = value.getDataType();
            List<ExtractInfo> extractInfos = null;
            String pid;
            if (CReceive.taskHandlerKey.equalsIgnoreCase(dataType)) {
                TaskInfo taskInfo = JSONObject.parseObject(data, TaskInfo.class);
                extractInfos = extractMapper.findByTid(taskInfo.getId());
            } else if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                ExtractInfo extractInfo = JSONObject.parseObject(data, ExtractInfo.class);
                pid = extractInfo.getPId();
                if (pid == null || "null".equalsIgnoreCase(pid)) {
                    extractInfos = extractMapper.findByTid(Integer.valueOf(extractInfo.getTid()));
                } else {
                    extractInfos = extractMapper.findByPid(Integer.valueOf(pid));
                }
            }

            if (extractInfos == null || extractInfos.size() < 1) {
                log.info("extract handler end >>> msg=don`t have extracts");
                //TODO 数据已经处理完毕
            } else {
                String traceIdTemp = TraceUtil.traceId();
                for (ExtractInfo extractInfo : extractInfos) {
                    // 上个节点的result是当前节点的content
                    if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                        ExtractInfo sourceExtract = JSONObject.parseObject(data, ExtractInfo.class);

                        extractInfo.setContent(sourceExtract.getResult());
                        extractInfo.setContentBytes(sourceExtract.getResultBytes());
                    }

                    JSONObject dataJson = JSONObject.parseObject(data);

                    // 数据挂载的处理
                    String mount = extractInfo.getMount();
                    if (mount != null && mount.contains("[new]")) {
                        //TODO 需要进行 同一个子排列下面的traceId 是一样的。最好的办法就是给父节点设置newTraceId。
                        String traceId = dataJson.getString("traceId");
                        extractInfo.setTraceId(traceIdTemp);
                        List<String> pTraceId = extractInfo.getPTraceId();
                        if (pTraceId.size() < 1 || pTraceId.get(pTraceId.size() - 1) != traceId) {
                            pTraceId.add(traceId);
                        }
                    } else {
                        extractInfo.setTraceId(dataJson.getString("traceId"));
                    }


                    String content = extractInfo.getContent();
                    Integer contentType = extractInfo.getContentType();
                    if (contentType == null || contentType == 0 || contentType == 1) {
                        String selector = extractInfo.getSelector();
                        log.info("extract handler >>> contentType=html, selector={}, content={}", selector, content);
                        // 默认html
                        // 如果传入的解析（即父解析）的contentType为一个link，则可以直接进行解析。
                        String selectResult = null;
                        if (CReceive.extractHandlerKey.equalsIgnoreCase(dataType)) {
                            ExtractInfo sourceExtract = JSONObject.parseObject(data, ExtractInfo.class);

                            Integer ct = sourceExtract.getContentType();
                            if (selector == null || StringUtils.isEmpty(selector)) {
                                selectResult = extractInfo.getContent();
                            } else if (ct == null || ct != 1) {
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
                            String result;
                            String selectorAttr = extractInfo.getSelectorAttr();
                            if (selectorAttr == null || StringUtils.isEmpty(selectorAttr)) {
                                result = document.text();
                            } else {
                                result = document.children().attr(selectorAttr);
                            }
                            extractInfo.setResult(result);
                            log.info("extract handler end >>> push to data result={}", JSON.toJSONString(extractInfo));
                            extractInfo.setContentBytes(null);
                            StreamData cycleStreamData = new StreamData(
                                    CReceive.dataHandlerKey,
                                    JSONObject.toJSONString(extractInfo),
                                    CReceive.dataHandlerKey
                            );
                            outSide(ctx, JSONObject.toJSONString(cycleStreamData));

                            log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);

                        } else if (resultType == 2) { // 返回的信息是一个数组
                            extractInfo.setResult(selectResult);
                            handlerHtmlArray(extractInfo, ctx);
                        } else {
                            extractInfo.setResult(selectResult);
                            log.info("extract handler end >>> push to extract result={}", JSON.toJSONString(extractInfo));
                            extractInfo.setContentBytes(null);
                            StreamData cycleStreamData = new StreamData(
                                    CReceive.dataHandlerKey,
                                    JSONObject.toJSONString(extractInfo),
                                    CReceive.dataHandlerKey
                            );
                            outSide(ctx, JSONObject.toJSONString(cycleStreamData));

                            log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);


                        }

                    } else if (contentType == 2) {
                    } else if (contentType == 3) {
                        // link 直接生成request进行请求去
                        // TODO url 的校验
                        Request request = Request.create(extractInfo);
                        request.setParentId(String.valueOf(extractInfo.getId()));
                        log.info("extract handler end >>> contentType=link, request={}", JSON.toJSONString(request));
                        StreamData cycleStreamData = new StreamData(
                                CReceive.downloadHandlerKey,
                                JSONObject.toJSONString(request),
                                CReceive.downloadHandlerKey
                        );

                        outSide(ctx, JSONObject.toJSONString(cycleStreamData));

                        log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);


                    } else if (contentType == 4) {
                        // 静态数据
                        extractInfo.setResult(extractInfo.getContent());
                        log.info("extract handler end >>> contentType=static, result={}", JSON.toJSONString(extractInfo));
                        extractInfo.setContentBytes(null);
                        StreamData cycleStreamData = new StreamData(
                                CReceive.dataHandlerKey,
                                JSONObject.toJSONString(extractInfo),
                                CReceive.dataHandlerKey);

                        outSide(ctx, JSONObject.toJSONString(cycleStreamData));

                        log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);

                    } else if (contentType == 5) {
                        String base64 = Selector.base64Selector().select(extractInfo.getContentBytes(), extractInfo);
                        extractInfo.setResult(base64);
                        log.info("extract handler end >>> contentType=base64, result={}", JSON.toJSONString(extractInfo));
                        extractInfo.setContentBytes(null);
                        StreamData cycleStreamData = new StreamData(
                                CReceive.dataHandlerKey,
                                JSONObject.toJSONString(extractInfo),
                                CReceive.dataHandlerKey);

                        outSide(ctx, JSONObject.toJSONString(cycleStreamData));
                        log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);
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
    private void handlerHtmlArray(ExtractInfo extractInfo, Context ctx) {
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

            StreamData cycleStreamData = new StreamData(
                    CReceive.extractHandlerKey,
                    JSONObject.toJSONString(temp),
                    CReceive.extractHandlerKey);

            outSide(ctx, JSONObject.toJSONString(cycleStreamData));
            log.info("extract process sideOut >>> tag={},data={}", outputTag, cycleStreamData);


        }
    }


    private void outSide(Context ctx, String data) {
        // TODO 更新消息状态为处理完毕
        ctx.output(outputTag, data);
    }
}
