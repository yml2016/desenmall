package com.desen.desenmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.desen.desenmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallMemberApplication.class, args);
    }

}
