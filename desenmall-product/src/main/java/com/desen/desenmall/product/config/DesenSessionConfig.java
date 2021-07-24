package com.desen.desenmall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class DesenSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 明确的指定Cookie的作用域（扩大作用域）
        cookieSerializer.setDomainName("desenmall.com");
        cookieSerializer.setCookieName("DESENSESSION");
        //cookieSerializer.setCookieMaxAge(60);//前端cookie失效时间
        return cookieSerializer;
    }

    /**
     * 自定义序列化机制：使存于redis中的会话信息以json形式保存
     * 这里方法名必须是：springSessionDefaultRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
