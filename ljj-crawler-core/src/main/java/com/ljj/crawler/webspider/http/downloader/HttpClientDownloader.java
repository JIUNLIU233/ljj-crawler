package com.ljj.crawler.webspider.http.downloader;


import com.ljj.crawler.webspider.http.Method;
import com.ljj.crawler.webspider.http.Request;
import com.ljj.crawler.webspider.http.Response;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JIUN·LIU
 * @data 2020/2/13 15:19
 */
public class HttpClientDownloader implements Downloader {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientDownloader.class);

    /**
     * 请求方式的转换，并进行下载。（简要的步骤如下）
     * 1、请求方式
     * 2、请求头
     * 3、请求体
     * 4、cookie设置
     * 5、请求的发送
     * 6、response 转换
     * 7、细节化配置
     * 7.1 重定向，交给的外部调用
     *
     * @param request
     * @return
     */
    @Override
    public Response download(Request request) {
        //1
        HttpRequestBase clientRequest = getClientRequest(request);
        //2
        setHeaders(clientRequest, request); // 头设置
        //3

        if (request.getRequestBody() != null) {
            setTextEntity(clientRequest, request);
        } else if (request.getRequestData() != null) {
            setNameValuePair(clientRequest, request);
        }


        //7
        RequestConfig.Builder custom = RequestConfig.custom();
        SocketConfig.Builder socketCustom = SocketConfig.custom();
        socketCustom.setSoKeepAlive(false);
        socketCustom.setSoTimeout(request.getTimeOut());
        //7.1
        custom = custom.setRedirectsEnabled(false);
        custom = custom.setConnectTimeout(request.getTimeOut());
        custom = custom.setConnectionRequestTimeout(request.getTimeOut());


        RequestConfig config = custom.build();

        //4
        HttpClientBuilder builder = HttpClientBuilder.create();
        if (request.getCookieStore() != null) builder.setDefaultCookieStore(request.getCookieStore()); // cookie 的设置
        builder.setDefaultRequestConfig(config);
        builder.setDefaultSocketConfig(socketCustom.build());

        CloseableHttpClient client = builder.build();


        // 5
        CloseableHttpResponse execute = null;
        int retryTime = request.getRetryTime();
        while (retryTime > 0) {

            try {
                logger.debug("task_id={} , 第 {} 次请求发起", request.getTaskId(), request.getRetryTime() - retryTime + 1);

                // TODO 这里有一个问题，请求会被阻塞，导致无法进行下一步处理，系统卡死
                execute = execute(client, clientRequest);

                if (execute.getStatusLine().getStatusCode() > 499) throw new Exception("返回状态码异常");
                logger.debug("task_id={} , 第 {} 次请求 , msg={}", request.getTaskId(), request.getRetryTime() - retryTime + 1, "httpclient 请求成功");
                break;
            } catch (Exception e) {
                logger.info("task_id={} , 第 {} 次请求 , msg={} , e:", request.getTaskId(), request.getRetryTime() - retryTime + 1, "httpclient 请求异常，重试···", e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } finally {
                retryTime--;
            }
        }
        return parse(request, execute);
    }


    private static CloseableHttpResponse execute(CloseableHttpClient client, HttpRequestBase clientRequest) throws Exception {
        return client.execute(clientRequest);
    }


    private Response parse(Request request, CloseableHttpResponse response) {
        if (response == null) return null;
        // TODO httpclient 的请求结果转换为自定义
        HttpEntity entity = response.getEntity();
        Response result = new Response(request);
        result.setCookieStore(request.getCookieStore());
        try {
            result.setCurrentURL(new URL(request.getUrl()));
            result.setResponseBytes(EntityUtils.toByteArray(entity));
            if (result.getResponseBytes() != null) {
                String responseBody = new String(result.getResponseBytes());
                String charSet = getCharSet(responseBody);
                if (charSet == null) {
                    result.setResponseBody(responseBody);
                } else {
                    if (charSet.contains("UTF") || charSet.contains("utf")) charSet = "UTF-8";
                    else charSet = "GBK";
                    result.setResponseBody(new String(result.getResponseBytes(), charSet));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        result.setStatusCode(statusCode);
        Map<String, String> header2Map = header2Map(response.getAllHeaders());
        if (header2Map != null) result.setHeaders(header2Map);
        if (statusCode > 299 && statusCode < 400) {
            String location = header2Map.get("Location");
            result.setLocation(location);
        }
        return result;
    }

    /**
     * 根据请求方式获取对应的httpclient 的请求类
     *
     * @param request
     * @return
     */
    private HttpRequestBase getClientRequest(Request request) {
        HttpRequestBase httpRequest;
        Method method = request.getMethod();
        String url = request.getUrl();
        if (method.equals(Method.GET)) {
            httpRequest = new HttpGet(url);
        } else if (method.equals(Method.POST)) {
            httpRequest = new HttpPost(url);
        } else if (method.equals(Method.DELETE)) {
            httpRequest = new HttpDelete(url);
        } else if (method.equals(Method.HEAD)) {
            httpRequest = new HttpHead(url);
        } else if (method.equals(Method.OPTIONS)) {
            httpRequest = new HttpOptions(url);
        } else if (method.equals(Method.PATCH)) {
            httpRequest = new HttpPatch(url);
        } else if (method.equals(Method.PUT)) {
            httpRequest = new HttpPut(url);
        } else if (method.equals(Method.TRACE)) {
            httpRequest = new HttpTrace(url);
        } else {
            httpRequest = new HttpGet(url);
        }
        return httpRequest;
    }

    private void setHeaders(HttpRequestBase clientRequest, Request request) {
        if (request.getHeaders() != null) {
            clientRequest.setHeaders(map2Header(request.getHeaders()));
        }
    }

    private void setNameValuePair(HttpRequestBase clientRequest, Request request) {
        if (request.getRequestData() != null) {
            HttpEntity entity = EntityBuilder.create().setParameters(map2NameValuePair(request.getRequestData())).build();
            ((HttpPost) clientRequest).setEntity(entity);
        }
    }

    private void setTextEntity(HttpRequestBase clientRequest, Request request) {
        if (request.getRequestBody() != null) {
            HttpEntity entity1 = EntityBuilder.create().setText(request.getRequestBody()).build();
            ((HttpPost) clientRequest).setEntity(entity1);
        }
    }

    private Header[] map2Header(Map<String, String> mapHeaders) {
        if (mapHeaders == null) return null;
        Header[] headers = new Header[mapHeaders.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : mapHeaders.entrySet()) {
            headers[index] = new BasicHeader(entry.getKey(), entry.getValue());
            index++;
        }
        return headers;
    }

    private Map<String, String> header2Map(Header[] headers) {
        if (headers == null) return null;
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            result.put(header.getName(), header.getValue());
        }
        return result;
    }

    private List<NameValuePair> map2NameValuePair(Map<String, String> mapForm) {
        if (mapForm == null) return null;
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : mapForm.entrySet()) {
            if (entry.getKey() == null) continue;
            if (entry.getValue() == null) nameValuePairs.add(new BasicNameValuePair(entry.getKey(), ""));
            else nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return nameValuePairs;
    }


    private static String getCharSet(String content) {

        Document parse = Jsoup.parse(content);
        Elements select = parse.select("meta");
        for (Element element : select) {
            if (element.outerHtml().contains("charset=")) {
                String regex = ".*charset=([^;]*)\".*";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(element.outerHtml());
                if (matcher.find())
                    return matcher.group(1);
                else
                    return null;
            }
        }

        return null;

    }


    public static void main(String[] args) {
        String charSet = getCharSet("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\" />");
        System.out.println(charSet);
    }
}
