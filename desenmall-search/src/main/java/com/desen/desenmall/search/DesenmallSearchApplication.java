package com.desen.desenmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallSearchApplication.class, args);
    }

}
