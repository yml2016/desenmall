package com.desen.desenmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.order.entity.OrderEntity;
import com.desen.desenmall.order.vo.*;
import feign.Param;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 11:33:41
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给订单确认页返回需要的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;


    /**
     * 下单操作
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    //OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    /**
     * 获取当前订单的支付信息
     */
    //PayVo getOrderPay(String orderSn);

    //PageUtils queryPageWithItem(@Param("params") Map<String, Object> params);

    /**
     * 处理支付宝的返回数据
     */
    //String handlePayResult(PayAsyncVo vo);

    //void createSecKillOrder(SecKillOrderTo secKillOrderTo);
}

