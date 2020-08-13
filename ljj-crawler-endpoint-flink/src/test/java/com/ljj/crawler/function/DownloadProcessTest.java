package com.ljj.crawler.function;

import com.ljj.crawler.common.utils.UrlUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.net.URI;
import java.net.URL;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/13
 **/
class DownloadProcessTest {

    @Test
    public void testHost() throws Exception {
        System.out.println(StringUtils.isNotBlank(null));
        System.out.println(StringUtils.isBlank(null));
        System.out.println(org.apache.commons.lang3.StringUtils.isBlank(null));
    }

    @Test
    public void testParseUrl() throws Exception {
        /*
         * 可能存在的情况：
         *      www.baidu.com/xxx   这个现需要添加协议
         *      //www.baidu.com/xxx 这个现需要处理协议和分隔符
         *      baidu.com/xxx       这个需要处理其前缀
         *      /xxx/xxx/xxx        这个需要补充host和协议
         *      xxx/xxx/xxx         这个需要补充host、协议、以及对应的分隔符
         * https://www.cnblogs.com/ljc-0923/p/10331779.html
         */
        URI uri = new URI("www.baidu.com/xxx");
        System.out.println(uri.getPath());
        System.out.println(uri.getFragment());
        System.out.println(uri.getRawPath());
    }

    @Test
    public void testParseUrl2() throws Exception {
        URL url = new URL("https://www.cnblogs.com/ljc-0923/p/10331779.html");
        String linkUrl = "ljc-0923/p/10331779.html";
        String host = url.getHost();
        String protocol = url.getProtocol();
        String newUrl = null;
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

        System.out.println(newUrl);
    }
}