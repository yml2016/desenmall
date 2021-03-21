package com.desen.desenmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.desen.desenmall.product.dao")
@SpringBootApplication
public class DesenmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesenmallProductApplication.class, args);
    }

}
