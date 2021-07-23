package com.desen.desenmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.desen.desenmall.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallAuthApplication.class, args);
    }

}
