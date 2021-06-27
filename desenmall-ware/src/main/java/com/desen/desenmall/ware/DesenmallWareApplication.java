package com.desen.desenmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.desen.desenmall.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages ={"com.desen"} )
public class DesenmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallWareApplication.class, args);
    }

}
