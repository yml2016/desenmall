package com.desen.desenmall.order.to;


import com.desen.desenmall.order.entity.OrderEntity;
import com.desen.desenmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 @Description
 @see OrderCreateTo
 @author yangminglin
 @date 2021/8/1
 @version V
**/
@Data
public class OrderCreateTo {

	private OrderEntity order;

	private List<OrderItemEntity> orderItems;

	/**
	 * 订单计算的应付价格
	 */
	private BigDecimal payPrice;

	/**
	 * 运费
	 */
	private BigDecimal fare;
}
