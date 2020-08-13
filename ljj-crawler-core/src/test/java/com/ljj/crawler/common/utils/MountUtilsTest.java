package com.ljj.crawler.common.utils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Create by JIUN·LIU
 * Create time 2020/8/13
 **/
class MountUtilsTest {


    public static void main(String[] args) {
        String collectionName = MountUtils.getCollectionName("ljj[new].xxx");
        System.out.println(collectionName);
    }
}