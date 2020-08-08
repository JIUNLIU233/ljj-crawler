package com.ljj.crawler.webspider.selector;

import com.ljj.crawler.core.po.ExtractInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.util.Base64;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public interface Selector {

    String select(byte[] content, ExtractInfo extractInfo);

    static Selector cssSelector() {
        return (content, extractInfo) -> {
            Document document = Jsoup.parse(new String(content));
            return document.select(extractInfo.getSelector()).outerHtml();
        };
    }

    static Selector cssXmlSelector() {
        return (content, extractInfo) -> {
            Document document = Jsoup.parse(new String(content), "", Parser.xmlParser());
            return document.select(extractInfo.getSelector()).outerHtml();
        };
    }

    static Selector jsonSelector() {
        return (content, extractInfo) -> {
            // JSON 的选择
            return null;
        };
    }

    static Selector jsSelector() {
        return (content, extractInfo) -> {
            // JSON 的选择
            return null;
        };
    }

    static Selector regexSelector() {
        return (content, extractInfo) -> {
            // 正则 的选择
            return null;
        };
    }


    static Selector base64Selector() {
        return (content, extractInfo) ->
                Base64.getEncoder().encodeToString(content);
    }


}
