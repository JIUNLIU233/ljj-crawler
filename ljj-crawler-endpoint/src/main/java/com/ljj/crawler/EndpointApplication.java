package com.ljj.crawler;

import com.ljj.crawler.common.utils.AppContext;
import com.ljj.crawler.core.scheduler.Scheduler;
import com.ljj.crawler.core.scheduler.impl.KafkaScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 通过启动参数 sc=kafka 来指定使用什么传递消息数据
 */
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

        Scheduler scheduler = null;

        String[] sourceArgs = args.getSourceArgs();
        for (String sourceArg : sourceArgs) {
            if (sourceArg != null && sourceArg.contains("sc=")) {
                String[] split = sourceArg.split("sc=");
                if ("kafka".equalsIgnoreCase(split[1])) {
                    KafkaScheduler kafkaScheduler = AppContext.getBean(KafkaScheduler.class);
                    kafkaScheduler.startListener();
                    scheduler = kafkaScheduler;

                    break;
                }
            }
        }
        crawlerInitial.start(scheduler);
    }
}
