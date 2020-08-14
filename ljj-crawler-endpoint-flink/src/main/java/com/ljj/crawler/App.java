package com.ljj.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/8/7 21:04
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    private FlinkEndPointApp flinkEndPointApp;


    @Autowired
    public App(FlinkEndPointApp flinkEndPointApp) {
        this.flinkEndPointApp = flinkEndPointApp;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        flinkEndPointApp.start();
    }
}
