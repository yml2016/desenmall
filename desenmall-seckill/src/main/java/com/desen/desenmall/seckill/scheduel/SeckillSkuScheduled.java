package com.desen.desenmall.seckill.scheduel;

import com.desen.desenmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class SeckillSkuScheduled {

	@Autowired
	private SeckillService seckillService;

	@Autowired
	private RedissonClient redissonClient;

	private final String upload_lock = "seckill:upload:lock";
	/**
	 * 这里应该是幂等的
	 *  三秒执行一次：* /3 * * * * ?
	 *  8小时执行一次：0 0 0-8 * * ?
	 */
	@Scheduled(cron = "*/5 * * * * ?")
	//@Scheduled(cron = "0 0 0-8 * * ?")
	public void uploadSeckillSkuLatest3Day(){
		log.info("上架秒杀商品的信息");
		// 加上分布式锁,避免分布式环境下同时执行，
		// 同时接口要保证幂等性
		// 状态已经更新 释放锁以后其他人才获取到最新状态
		RLock lock = redissonClient.getLock(upload_lock);
		lock.lock(10, TimeUnit.SECONDS);
		try {
			seckillService.uploadSeckillSkuLatest3Day();
		} finally {
			lock.unlock();
		}
	}
}
