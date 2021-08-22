package com.desen.desenmall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 1.spring-session依赖
 * 2.spring-session配置
 * 3.引入登录拦截器，与WebMvcConfigurer
 *
 */
@Configuration
public class DesenmallSessionConfig {

	@Bean
	public CookieSerializer cookieSerializer(){
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		// 明确的指定Cookie的作用域
		cookieSerializer.setDomainName("desenmall.com");
		cookieSerializer.setCookieName("DESENSESSION");
		return cookieSerializer;
	}

	/**
	 * 自定义序列化机制
	 * 这里方法名必须是：springSessionDefaultRedisSerializer
	 */
	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
		return new GenericJackson2JsonRedisSerializer();
	}
}
