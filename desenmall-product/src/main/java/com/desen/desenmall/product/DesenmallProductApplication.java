package com.desen.desenmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableCaching
@EnableFeignClients(basePackages = "com.desen.desenmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.desen.desenmall.product.dao")
@SpringBootApplication(scanBasePackages ={"com.desen"} )
public class DesenmallProductApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DesenmallProductApplication.class, args);
        /*String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println("name = " + name);
        }*/

    }

}
