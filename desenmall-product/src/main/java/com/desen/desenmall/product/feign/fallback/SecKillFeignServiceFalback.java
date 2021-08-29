package com.desen.desenmall.product.feign.fallback;

import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import com.desen.desenmall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;


@Component
public class SecKillFeignServiceFalback implements SeckillFeignService {

	@Override
	public R getSkuSeckillInfo(Long skuId) {
		System.out.println("秒杀服务异常，触发熔断");
		return R.error(BizCode.SECKILL_SERVER_EXCEPTION);
	}
}
