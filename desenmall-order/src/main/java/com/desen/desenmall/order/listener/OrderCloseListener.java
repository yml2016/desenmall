package com.desen.desenmall.order.listener;

import com.desen.desenmall.order.config.AlipayTemplate;
import com.desen.desenmall.order.entity.OrderEntity;
import com.desen.desenmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@RabbitListener(queues = "${myRabbitmq.MQConfig.queues}")
public class OrderCloseListener {

	@Autowired
	private OrderService orderService;

	@Autowired
	private AlipayTemplate alipayTemplate;

	@RabbitHandler
	public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
		try {
			orderService.closeOrder(entity);
			// 手动调用支付宝收单
			//alipay.trade.close(统一收单交易关闭接口:https://opendocs.alipay.com/open/028wob)
			//alipayTemplate.close();
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
}
