package com.desen.desenmall.order;

import com.desen.desenmall.order.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class DesenmallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate ;

    @Test
    public void createExchange(){
        Exchange exchange = new DirectExchange("desen-test",true,false);
        amqpAdmin.declareExchange(exchange);
    }

    @Test
    public void createQueues(){
        Queue queue = new Queue("desen-test-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
    }

    @Test
    public void createBinding(){
        Binding binding = new Binding("desen-test-queue",
                Binding.DestinationType.QUEUE,
                "desen-test",
                "desen-test",
                null);
        amqpAdmin.declareBinding(binding);
    }

    @Test
    public void sendMessage(){

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(55L);
        orderEntity.setMemberUsername("小杨哈哈哈en");
        rabbitTemplate.convertAndSend("desen-test","desen-test",orderEntity);
    }

}
