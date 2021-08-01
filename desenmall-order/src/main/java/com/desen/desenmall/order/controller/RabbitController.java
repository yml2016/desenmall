package com.desen.desenmall.order.controller;

import com.desen.desenmall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num",required = false) Integer num){
        if(num == null){
            num=5;
        }
        for (Integer i = 0; i < num; i++) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setMemberId(5566L);
            orderEntity.setMemberUsername("小杨二郎"+i);
            rabbitTemplate.convertAndSend("desen-test","desen-test1", orderEntity, new CorrelationData(UUID.randomUUID().toString()));
        }
        return "OK";
    }
}
