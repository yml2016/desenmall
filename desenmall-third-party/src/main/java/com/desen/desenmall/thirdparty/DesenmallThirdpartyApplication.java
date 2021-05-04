package com.desen.desenmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallThirdpartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallThirdpartyApplication.class, args);
    }

}
