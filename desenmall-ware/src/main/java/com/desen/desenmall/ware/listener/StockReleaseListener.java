package com.desen.desenmall.ware.listener;

import com.desen.common.to.mq.OrderTo;
import com.desen.common.to.mq.StockLockedTo;
import com.desen.desenmall.ware.service.WareSkuService;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "${myRabbitmq.MQConfig.queues}")
public class StockReleaseListener {

	@Autowired
	private WareSkuService wareSkuService;

	/**
	 * 下单成功 库存解锁 接下来业务调用失败
	 *
	 *  只要解锁库存消息失败 一定要告诉服务解锁失败
	 */
	@RabbitHandler
	public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
		try {
			wareSkuService.unlockStock(to);
			// 执行成功的 回复 [仅回复自己的消息]
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			//在极端情况下可能会死循环(拒绝->接收->异常->拒绝->接收->)，
			//为避免这种情况最好是重试几次之后，记录数据库，ack掉该消息。
			log.error("解锁库存异常", e);
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}
	}

	/**
	 * 订单关闭后 发送的消息这里接收
	 */
	@RabbitHandler
	public void handleOrderCloseRelease(OrderTo to, Message message, Channel channel) throws IOException {
		try {
			//是否是重新派发的消息(通过该属性也可以解决消息重复的问题，最好是把接口设计成幂等的)
			//Boolean redelivered = message.getMessageProperties().getRedelivered();
			wareSkuService.unlockStock(to);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}
	}
}
