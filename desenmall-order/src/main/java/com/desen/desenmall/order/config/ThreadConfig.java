package com.desen.desenmall.order.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


// 开启这个属性配置
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class ThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties){

        return new ThreadPoolExecutor(
                threadPoolConfigProperties.getCoreSize(),
                threadPoolConfigProperties.getMaxSize(),
                threadPoolConfigProperties.getKeepAliveTime() ,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
