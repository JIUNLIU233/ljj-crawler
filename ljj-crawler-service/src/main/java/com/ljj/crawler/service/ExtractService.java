package com.ljj.crawler.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljj.crawler.common.constant.ConfigConstant;
import com.ljj.crawler.core.po.ExtractInfo;
import com.ljj.crawler.mapper.ExtractMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/4
 **/
@Service
public class ExtractService {

    @Resource
    private ExtractMapper extractMapper;


    /**
     * 导出任务对应的解析配置
     *
     * @param tid
     * @return
     */
    public JSONArray exportByTid(Integer tid) {
        List<ExtractInfo> extractInfos = extractMapper.findByTid(tid);
        if (extractInfos == null) return null;
        return export(extractInfos);
    }

    /**
     * 解析配置的导入
     *
     * @param extracts
     * @param tid
     */
    public void importConfig(JSONArray extracts, Integer tid) {
        if (extracts == null) return;
        for (int i = 0; i < extracts.size(); i++) {
            JSONObject extractJSON = extracts.getJSONObject(i);
            JSONArray child = extractJSON.getJSONArray(ConfigConstant.extractChildKey);
            ExtractInfo extractInfo = extractJSON.toJavaObject(ExtractInfo.class);
            extractInfo.setTid(tid);
            // 判断是否含有子解析
            if (child == null || child.size() < 1) { // 只插入本条记录即可
                extractMapper.insert(extractInfo);
            } else {
                importChild(extractJSON, tid);
            }
        }
    }

    /**
     * 查找配置的子配置信息
     *
     * @param parentExtract
     */
    private void exportByPid(JSONObject parentExtract) {
        List<ExtractInfo> sons = extractMapper.findByPid(parentExtract.getInteger("id"));
        if (sons == null || sons.size() < 1) return;
        JSONArray child = export(sons);
        parentExtract.put(ConfigConstant.extractChildKey, child);
    }

    /**
     * 数据转换导出
     *
     * @param extractInfos
     * @return
     */
    private JSONArray export(List<ExtractInfo> extractInfos) {
        JSONArray result = new JSONArray();
        for (ExtractInfo extractInfo : extractInfos) {
            String s = JSONObject.toJSONString(extractInfo);
            JSONObject extractJSON = JSONObject.parseObject(s);
            exportByPid(extractJSON);
            extractJSON.remove("id");
            extractJSON.remove("pid");
            extractJSON.remove("pTraceId");
            extractJSON.remove("tid");
            result.add(extractJSON);
        }
        return result;
    }


    private void importChild(JSONObject extractJSON, Integer tid) { //  到了这里基本上是肯定有子解析的。暂时不做判断
        JSONArray child = extractJSON.getJSONArray(ConfigConstant.extractChildKey); // 查询子解析
        ExtractInfo extractInfo = extractJSON.toJavaObject(ExtractInfo.class);
        extractInfo.setTid(tid);
        extractMapper.insert(extractInfo); // 插入自己
        for (int i = 0; i < child.size(); i++) { // 循环子解析
            JSONObject son = child.getJSONObject(i);
            ExtractInfo sonExtract = son.toJavaObject(ExtractInfo.class);
            sonExtract.setTid(tid);
            sonExtract.setPid(extractInfo.getId());

            JSONArray sonChild = son.getJSONArray(ConfigConstant.extractChildKey);
            if (sonChild != null && sonChild.size() > 0) { // 判断子解析是否依然包含子解析
                importChild(son, tid);
            } else {
                extractMapper.insert(sonExtract);
            }
        }

    }
}
