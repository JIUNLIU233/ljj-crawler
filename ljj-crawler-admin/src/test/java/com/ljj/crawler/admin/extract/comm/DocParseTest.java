package com.ljj.crawler.admin.extract.comm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/13
 **/
public class DocParseTest {


    public static void main(String[] args) {
        Document doc = Jsoup.parse(content, "", Parser.xmlParser());
        Element child = doc.child(0);
        String href = child.attr("href");
        System.out.println("");
    }

    public static String content = "<a href=\"http://www.xbiquge.la/61/61129/\" target=\"_blank\">最强领域系统挂机就变强</a>";
}
