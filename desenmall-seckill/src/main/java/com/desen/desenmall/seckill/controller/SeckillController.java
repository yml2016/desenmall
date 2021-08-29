package com.desen.desenmall.seckill.controller;

import com.desen.common.utils.R;
import com.desen.desenmall.seckill.service.SeckillService;
import com.desen.desenmall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * 1.默认所有URL都是被保护的资源，可以直接进行流量的规制限制
 * 2.自定义流控响应
 * 3.feign远程调用的熔断降级
 * 4.自定义受保护的资源,try-catch、@SentinelResource(blockHandler = "blockHandlerForGetUser")
 */

@Controller
public class SeckillController {

	@Autowired
	private SeckillService seckillService;

	@ResponseBody
	@GetMapping("/currentSeckillSkus")
	public R getCurrentSeckillSkus(){
		List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
		return R.ok().setData(vos);
	}

	@ResponseBody
	@GetMapping("/sku/seckill/{skuId}")
	public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
		/*try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		SeckillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
		return R.ok().setData(to);
	}

	@GetMapping("/kill")
	public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num, Model model){
		String orderSn = seckillService.kill(killId,key,num);
		// 1.判断是否登录
		model.addAttribute("orderSn", orderSn);
		return "success";
	}
}
