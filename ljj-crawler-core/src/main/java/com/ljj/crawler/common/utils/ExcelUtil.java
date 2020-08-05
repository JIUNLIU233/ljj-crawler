package com.ljj.crawler.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/16
 **/
public class ExcelUtil {


    public static JSONArray readRSXLSX(String filePath) {
        return readRSXLSX(filePath, 0);
    }


    /**
     * 读取 resource 目录下的 excel xlsx 文件
     * <p>
     * 表格类型：第一行为列名，无列名的信息不进行读取
     * 默认读取第一个sheet中的数据
     *
     * @return
     */
    public static JSONArray readRSXLSX(String filePath, int sheetIndex) {
        JSONArray result = new JSONArray();
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
            XSSFSheet sheetAt = sheets.getSheetAt(sheetIndex);
            Iterator<Row> rowIterator = sheetAt.rowIterator();
            boolean firstFlag = true;
            List<String> colNames = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (firstFlag) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell next = cellIterator.next();
                        if (next != null) {
                            String stringCellValue = next.getStringCellValue();
                            colNames.add(stringCellValue);
                        } else {
                            break;
                        }
                    }
                    firstFlag = false;
                } else {
                    JSONObject tmp = new JSONObject();
                    for (int i = 0; i < colNames.size(); i++) {
                        Cell cell = row.getCell(i);
                        String s = colNames.get(i);
                        if (cell != null) tmp.put(s, cell.getStringCellValue());
                        else tmp.put(s, null);
                    }
                    result.add(tmp);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
