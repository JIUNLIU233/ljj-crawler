package com.ljj.crawler.endpoint_v1.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PicSiteTest {


    public static void main(String[] args) throws Exception{
        Document document = Jsoup.connect("http://nsfwpicx.com/archives/1904.html")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get();
        System.out.println(document);
    }
}
