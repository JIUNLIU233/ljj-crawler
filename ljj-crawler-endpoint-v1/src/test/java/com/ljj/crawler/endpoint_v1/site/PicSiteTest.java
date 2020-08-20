package com.ljj.crawler.endpoint_v1.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class PicSiteTest {


    public static void main(String[] args) throws Exception{
        Document document = Jsoup.connect("http://nsfwpicx.com/archives/1904.html")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get();
        System.out.println(document);
    }

    @Test
    public void testSys() throws Exception{
        Map<String, String> getenv =
                System.getenv();
        System.out.println(System.getenv("COMPUTERNAME"));
    }
}
