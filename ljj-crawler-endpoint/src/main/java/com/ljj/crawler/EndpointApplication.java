package com.ljj.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EndpointApplication implements ApplicationRunner {

    private final CrawlerInitial crawlerInitial;

    @Autowired
    public EndpointApplication(CrawlerInitial crawlerInitial) {
        this.crawlerInitial = crawlerInitial;
    }

    public static void main(String[] args) {
        SpringApplication.run(EndpointApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 默认采用本地队列单机运行
        crawlerInitial.start();
    }
}
