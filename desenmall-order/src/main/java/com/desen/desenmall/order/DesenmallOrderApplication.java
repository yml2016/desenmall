package com.desen.desenmall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * 使用RabbitMQ
 * 1.引入amqp，RabbitAutoConfiguration就会自动生效，不需要 @EnableRabbit
 * 2.给容器中自动配置
 *       rabbitTemplate AmqpAdmin CachingConnectionFactory RabbitMessagingTemplate
 * 3.@ConfigurationProperties(prefix = "spring.rabbitmq")
 * 4.@EnableRabbit 开启功能
 * 5.监听消息：@RabbitListener 必须有@EnableRabbit
 *    @RabbitListener 和  @RabbitHandler 组合用来监听同一队列中的不同消息类型
 *
 */
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class DesenmallOrderApplication {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DesenmallOrderApplication.class, args);
    }


    /*@LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    public class TestController {

        private final RestTemplate restTemplate;

        @Autowired
        public TestController(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

        @RequestMapping(value = "/oder/{str}", method = RequestMethod.GET)
        public String echo(@PathVariable String str) {
            System.out.println("str====="+str);
            return restTemplate.getForObject("http://desenmall-product/product/category/nacos", String.class);
        }
    }
*/
}
