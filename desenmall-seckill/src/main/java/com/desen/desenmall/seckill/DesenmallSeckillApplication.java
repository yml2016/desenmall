package com.desen.desenmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DesenmallSeckillApplication {

//	http://127.0.0.1:25000/currentSeckillSkus
//	http://seckill.desenmall.com/currentSeckillSkus
	public static void main(String[] args) {
		SpringApplication.run(DesenmallSeckillApplication.class, args);
	}

}
