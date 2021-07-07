
package com.desen.desenmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class RedissonConfig {

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
//        config.useClusterServers()
//                .addNodeAddress("47.115.19.227:6379","47.115.19.227:6379"); //集群模式
        config.useSingleServer()
                .setAddress("redis://47.115.19.227:6379")
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        return redissonClient;
    }
}
