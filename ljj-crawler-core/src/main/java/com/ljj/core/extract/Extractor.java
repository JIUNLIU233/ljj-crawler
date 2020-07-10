package com.ljj.core.extract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 用于信息抽取，适配各种抽取规则
 * Create by JIUN·LIU
 * Create time 2020/7/10
 **/
public interface Extractor {


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
            return Extractor.xPathExtract(content, selector);
        }


        return Extractor.cssExtract(content, selector);

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
        Elements select = document.select(parseRule);
        if (select == null) return null;
        else return select.text();
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
