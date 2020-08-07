package com.ljj.crawler.utils;

import com.ljj.crawler.contant.CReceive;
import com.ljj.crawler.po.StreamData;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/7
 **/
public class OutPutTagUtils {

    public static Map<String, OutputTag<StreamData>> getSourceOutTag() {
        HashMap<String, OutputTag<StreamData>> result = new HashMap<>();

        for (String cReceive : CReceive.cReceives) {
            result.put(cReceive, new OutputTag<>(cReceive, TypeInformation.of(new TypeHint<StreamData>() {
            })));
        }

        return result;
    }
}
