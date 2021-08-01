package com.desen.desenmall.order.vo;


import com.desen.desenmall.order.entity.OrderEntity;
import lombok.Data;


@Data
public class SubmitOrderResponseVo {

	private OrderEntity orderEntity;

	/**
	 * 错误状态码： 0----成功
	 */
	private Integer code;
}
