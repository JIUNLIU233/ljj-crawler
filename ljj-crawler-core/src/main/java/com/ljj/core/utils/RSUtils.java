package com.ljj.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 功能：
 * 读取resource下的文件内容
 *
 * @Author:JIUNLIU
 * @data : 2020/6/6 16:20
 */
public class RSUtils {

    private static final Logger logger = LoggerFactory.getLogger(RSUtils.class);

    public static String readFile(String filePath, String encoding) {
        BufferedReader reader = null;
        String content = "";
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try {
            reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));
            content = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
        } catch (Exception e) {
            logger.error("read file error, msg->{}", e.getMessage(), e);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("read file error, msg->{}", e.getMessage(), e);
            }
        }
        return content;
    }

    public static String readFile(String filePath) {
        return readFile(filePath, "UTF-8");
    }

    public static Set<String> readFile2Set(String filePath) {
        String s = readFile(filePath);
        HashSet<String> result = new HashSet<>();
        for (String tmp : s.split("\n")) {
            result.add(tmp.trim());
        }
        return result;
    }
}
