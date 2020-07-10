package com.ljj.core.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/10
 **/
public class FileUtils {


    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 创建目录
     *
     * @param path
     */
    public static void mkdir(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) file.mkdirs();
    }

    public static void mkdir(String parent, String path) {
        if (parent == null || parent.equalsIgnoreCase("")) {
            new File(path).mkdirs();
        }
        if (parent.endsWith("\\") || parent.endsWith("/")) {
            new File(parent + path).mkdirs();
        } else new File(parent + "/" + path).mkdirs();
    }

    public static void saveFile(InputStream inputStream, String subFix, String savePath) {
        saveFile(inputStream, subFix, UUID.randomUUID().toString(), savePath);
    }

    public static void saveFile(byte[] bytes, String subFix, String savePath) {
        saveFile(bytes, subFix, UUID.randomUUID().toString(), savePath);
    }

    /**
     * 文件保存
     */
    public static void saveFile(InputStream inputStream, String subFix, String fileName, String savePath) {
        mkdir(savePath);
        File file = new File(savePath + "/" + fileName + subFix);
        try (FileOutputStream os = new FileOutputStream(file)) {
            int len;
            byte[] bs = new byte[2048];
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            logger.error("save file fail, fileName={},savePath={},e:", fileName, savePath, e);
        }
    }


    public static void saveFile(byte[] bytes, String subFix, String fileName, String savePath) {
        mkdir(savePath);
        File file = new File(savePath + "/" + fileName + subFix);
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
        } catch (Exception e) {
            logger.error("save file fail, fileName={},savePath={},e:", fileName, savePath, e);
        }
    }


    public static void main(String[] args) throws Exception {
        Connection.Response execute = Jsoup.connect("https://csdnimg.cn/cdn/content-toolbar/csdn-logo.png?v=20200416.1")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .execute();
        byte[] bytes = execute.bodyAsBytes();
//        BufferedInputStream bufferedInputStream = execute.bodyStream();
        saveFile(bytes, ".png", "E:\\temp\\a");
        saveFile(execute.bodyStream(),".png","E:\\temp\\a");

    }
}
