package com.desen.desenmall.product.feign;


import com.desen.common.utils.R;
import com.desen.desenmall.product.feign.fallback.SecKillFeignServiceFalback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "desenmall-seckill", fallback = SecKillFeignServiceFalback.class)
public interface SeckillFeignService {

	@GetMapping("/sku/seckill/{skuId}")
	R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
