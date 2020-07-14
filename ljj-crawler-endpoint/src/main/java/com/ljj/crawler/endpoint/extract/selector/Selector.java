package com.ljj.crawler.endpoint.extract.selector;

import com.ljj.crawler.endpoint.extract.downloader.Response;
import com.ljj.crawler.endpoint.extract.model.ExtractInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/14
 **/
public interface Selector {

    //TODO Selector 提供一个子类，自我另外实现类的一个注册方式，并增加对应的工厂
    String select(byte[] content, ExtractInfo extractInfo);

    static Selector cssSelector() {
        return (content, extractInfo) -> {
            Document document = Jsoup.parse(new String(content), "", Parser.xmlParser());
            return document.select(extractInfo.getExtractParam()).outerHtml();
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


    static Selector fileSelector() {
        return (content, extractInfo) -> {
            // 文件 的选择
            return null;
        };
    }


    static String selector(Response response, ExtractInfo extractInfo) {
        switch (extractInfo.getExtractType()) {
            case 1:
                return Selector.jsonSelector().select(response.getBody(), extractInfo);
            case 2:
                return Selector.regexSelector().select(response.getBody(), extractInfo);
            case 3:
                return Selector.jsSelector().select(response.getBody(), extractInfo);
            case 4:
                return Selector.fileSelector().select(response.getBody(), extractInfo);
            default:
                return Selector.cssSelector().select(response.getBody(), extractInfo);
        }
    }
}
