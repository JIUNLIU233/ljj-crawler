package com.ljj.crawler.webspider.selector;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.ljj.crawler.core.po.ExtractInfo;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public interface Selector {

    String select(byte[] content, ExtractInfo extractInfo);

    /**
     * 会填充html的基本标签
     *
     * @return
     */
    static Selector cssSelector() {
        return (content, extractInfo) -> {
            Document document = Jsoup.parse(new String(content));
            Elements select = document.select(extractInfo.getSelector());
            String selectorAttr = extractInfo.getSelectorAttr();
            if (StringUtils.isEmpty(selectorAttr)) return select.outerHtml();
            else return select.attr(selectorAttr);
        };
    }

    /**
     * 不填充 html 的一些基本标签信息
     *
     * @return
     */
    static Selector cssXmlSelector() {
        return (content, extractInfo) -> {
            Document document = Jsoup.parse(new String(content), "", Parser.xmlParser());
            return document.select(extractInfo.getSelector()).outerHtml();
        };
    }

    /**
     * json 的解析
     * 当已经到了这里的时候，证明已经是json数据了。
     *
     * @return
     */
    static Selector jsonSelector() {
        return (contentBytes, extractInfo) -> {
            // JSON 的选择
            String selector = extractInfo.getSelector();
            String content = extractInfo.getContent();
            Object read = JSONPath.read(content, selector);
            return read == null ? null : String.valueOf(read);
        };
    }

    static Selector jsSelector() {
        return (content, extractInfo) -> {
            // JS 的选择
            return null;
        };
    }

    static Selector regexSelector() {
        return (contentBytes, extractInfo) -> {
            // 正则 的选择

            JSONArray result = new JSONArray();
            String selector = extractInfo.getSelector();
            String content = extractInfo.getContent();
            Pattern compile = Pattern.compile(selector);
            Matcher matcher = compile.matcher(content);
            if (matcher.find()) {
                int i = matcher.groupCount();
                for (int j = 0; j < i; j++) {
                    result.add(matcher.group(j));
                }
                return result.toJSONString();
            }
            return null;
        };
    }


    static Selector base64Selector() {
        return (content, extractInfo) ->
                Base64.getEncoder().encodeToString(content);
    }


}
