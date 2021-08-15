package com.desen.desenmall.order.service.impl;

import com.desen.desenmall.order.entity.OrderEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.order.dao.OrderItemDao;
import com.desen.desenmall.order.entity.OrderItemEntity;
import com.desen.desenmall.order.service.OrderItemService;

//@RabbitListener(queues={"desen-test-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

   /* @RabbitHandler
    public void receiveMessage(Message message, Channel channel, OrderEntity orderEntity) throws IOException {
        byte[] body = message.getBody();
        String s = new String(body, "UTF-8");
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        channel.basicAck(deliveryTag,false);
        //channel.basicNack(deliveryTag,false,true);
        System.out.println("=========================="+ deliveryTag + s + orderEntity);
    }*/

   /* @RabbitHandler
    public void receiveMessageMap(Message message, Channel channel, Map map) throws IOException {
        byte[] body = message.getBody();
        String s = new String(body, "UTF-8");
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        channel.basicAck(deliveryTag,false);
        //channel.basicNack(deliveryTag,false,true);
        System.out.println("=========================="+ deliveryTag+s + map);
    }*/

}