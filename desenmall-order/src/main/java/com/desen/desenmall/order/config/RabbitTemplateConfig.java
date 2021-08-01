package com.desen.desenmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class RabbitTemplateConfig {

    @Resource
    RabbitTemplate rabbitTemplate;


    /**
     * 定制RabbitTemplate
     *  @PostConstruct 对象创建完后调用
     * @return
     */
    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
            /**
             * 只要消息抵达broker 就ack为true
             * @param correlationData 当前消息的唯一关联数据
             * @param b 消息是否成功收到
             * @param s 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("setConfirmCallback==============="+b+s+correlationData);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调（用错误的路由键模拟）
             * @param message 投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 当时这个消息发给哪个交换机
             * @param routingKey 当时这个消息发给哪个路由建
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("setReturnCallback============="+"replyCode:"+replyCode+"replyText:"+replyText+"exchange:"+exchange+"routingKey:"+routingKey+"message::"+message);
            }
        });

    }

}
