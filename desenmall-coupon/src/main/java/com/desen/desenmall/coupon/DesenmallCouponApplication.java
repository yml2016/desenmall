package com.desen.desenmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallCouponApplication.class, args);
    }

}
