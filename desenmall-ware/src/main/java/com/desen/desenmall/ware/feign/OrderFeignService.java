package com.desen.desenmall.ware.feign;

import com.desen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("desenmall-order")
public interface OrderFeignService {

	/**
	 * 查询订单状态
	 */
	@RequestMapping("/order/order/status/{orderSn}")
	R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
