package com.desen.desenmall.member.feign;


import com.desen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;


@FeignClient("desenmall-order")
public interface OrderFeignService {

	@PostMapping("/order/order/listWithItem")
	R listWithItem(@RequestBody Map<String, Object> params);
}
