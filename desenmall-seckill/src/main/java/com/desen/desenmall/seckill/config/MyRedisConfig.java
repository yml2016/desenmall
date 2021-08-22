package com.desen.desenmall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyRedisConfig {

	@Value("${spring.redis.host}")
	private String ipAddr;

	@Value("${spring.redis.port}")
	private String port;

	@Value("${spring.redis.password}")
	private String password;

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() {
		Config config = new Config();
		// 创建单例模式的配置
		config.useSingleServer().setAddress("redis://" + ipAddr + ":" + port);
		config.useSingleServer().setPassword(password);
		return Redisson.create(config);
	}
}
