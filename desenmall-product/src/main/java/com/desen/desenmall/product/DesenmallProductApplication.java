package com.desen.desenmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.desen.desenmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.desen.desenmall.product.dao")
@SpringBootApplication(scanBasePackages ={"com.desen"} )
public class DesenmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallProductApplication.class, args);
    }

}
