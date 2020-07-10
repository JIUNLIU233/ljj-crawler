package com.ljj.core.utils;

import java.net.URL;

/**
 * create by JIUN·LIU
 * create time 2019/8/10
 */
public class UrlUtils {
    /**
     * 移除链接上的参数
     *
     * @param url
     * @return
     */
    public static String removeParam(String url) {
        int index = url.indexOf("?");
        if (index > 0 && index < url.length()) {
            return url.substring(0, index);
        } else {
            return url;
        }
    }


    /**
     * 获取文件后缀
     *
     * @return
     */
    public static String fileSuffix(String url) {
        url = removeParam(url);
        int index = url.lastIndexOf(".");
        if (index > 0 && index < url.length()) {
            return url.substring(index);
        } else {
            return ".html";
        }
    }

    /**
     * 获取url的顶级域名
     *
     * @param
     * @return
     */
    public static String getHost(String url) {
        String host;
        try {
            host = new URL(url).getHost();
        } catch (Exception e) {
            return null;
        }
        return host;
    }

    public static String urlCheckAndRepair(String host, String url) {
        if (!url.contains(host) && !url.startsWith("http")) {
            url = "http://" + host + url;
        }
        return url;
    }
}