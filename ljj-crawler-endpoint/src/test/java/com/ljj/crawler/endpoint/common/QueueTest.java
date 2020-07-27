package com.ljj.crawler.endpoint.common;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Create by JIUN·LIU
 * Create time 2020/7/27
 **/
public class QueueTest {


    public static void main(String[] args) {
        BlockingDeque<String> queue = new LinkedBlockingDeque<>();

        String poll = queue.poll();
        System.out.println("");
    }
}
