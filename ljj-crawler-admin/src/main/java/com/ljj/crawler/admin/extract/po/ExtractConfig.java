package com.ljj.crawler.admin.extract.po;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/12 13:29
 */
public class ExtractConfig {
    private Integer id;
    private String fieldName;
    private String extractType;
    private String extractParam;
    private String resultType;
    private String saveType;
    private String extractFlag;
    private String extractUrlRule;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getExtractType() {
        return extractType;
    }

    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }

    public String getExtractParam() {
        return extractParam;
    }

    public void setExtractParam(String extractParam) {
        this.extractParam = extractParam;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public String getExtractFlag() {
        return extractFlag;
    }

    public void setExtractFlag(String extractFlag) {
        this.extractFlag = extractFlag;
    }

    public String getExtractUrlRule() {
        return extractUrlRule;
    }

    public void setExtractUrlRule(String extractUrlRule) {
        this.extractUrlRule = extractUrlRule;
    }
}
