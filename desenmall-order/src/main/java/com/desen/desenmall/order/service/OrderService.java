package com.desen.desenmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 11:33:41
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

