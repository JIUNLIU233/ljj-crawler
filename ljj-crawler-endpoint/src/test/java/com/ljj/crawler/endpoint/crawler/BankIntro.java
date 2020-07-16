package com.ljj.crawler.endpoint.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.endpoint.extract.webspider.WRequest;
import com.ljj.crawler.endpoint.extract.webspider.http.Response;
import com.ljj.crawler.endpoint.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/16
 **/
public class BankIntro {


    public void start() {
        JSONArray objects = ExcelUtil.readRSXLSX("bank/BankIntro.xlsx");
        for (int i = 0; i < objects.size(); i++) {
            JSONObject jsonObject = objects.getJSONObject(i);
            String intro = getIntro(jsonObject);
            String logo = getLogo(jsonObject);

        }
    }


    public String getIntro(JSONObject object) {
        String introUrl = object.getString("introUrl");
        if (StringUtils.isEmpty(introUrl)) return null;
        String introCssSelector = object.getString("introCssSelector");
        Response response = WRequest.create(object.getString("cert_code"))
                .connect(introUrl)
                .execute();

        if (response == null) return null;
        else {
            Document parse = Jsoup.parse(response.getResponseBody());
            Charset charset = parse.charset();
            String text = parse.select(introCssSelector).text();
            return text;
        }
    }

    public String getLogo(JSONObject object) {
        String logoUrl = object.getString("logoUrl");
        if (StringUtils.isEmpty(logoUrl)) return null;
        Response response = WRequest.create(object.getString("cert_code"))
                .connect(logoUrl)
                .execute();
        if (response == null) return null;
        else {
            byte[] responseBytes = response.getResponseBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(responseBytes);
        }
    }

    public static void main(String[] args) {
        new BankIntro().start();
    }

}
