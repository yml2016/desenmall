package com.desen.desenmall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        return messageConverter;
    }


}
