package com.ljj.crawler.endpoint.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 功能：
 *
 * @Author:JIUNLIU
 * @data : 2020/7/14 21:11
 */
@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    public static <T> T getBean(Class<T> tClass) {
        if (context == null) return null;
        return context.getBean(tClass);
    }
}
