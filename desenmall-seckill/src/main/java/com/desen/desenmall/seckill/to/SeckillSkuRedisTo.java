package com.desen.desenmall.seckill.to;

import com.desen.desenmall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 @Description
 @see SeckillSkuRedisTo
 @author yangminglin
 @date 2021/8/22
 @version V
**/
@Data
public class SeckillSkuRedisTo {

	private Long promotionId;
	/**
	 * 活动场次id
	 */
	private Long promotionSessionId;
	/**
	 * 商品id
	 */
	private Long skuId;
	/**
	 * 商品的秒杀随机码，随机码会在秒杀开始才暴露，没有随机码是不能秒杀的。
	 */
	private String randomCode;
	/**
	 * 秒杀价格
	 */
	private BigDecimal seckillPrice;
	/**
	 * 秒杀总量
	 */
	private BigDecimal seckillCount;
	/**
	 * 每人限购数量
	 */
	private BigDecimal seckillLimit;
	/**
	 * 排序
	 */
	private Integer seckillSort;

	/**
	 *  sku的详细信息
	 */
	private SkuInfoVo skuInfoVo;

	/**
	 *  商品秒杀的开始时间
	 */
	private Long startTime;

	/**
	 *  商品秒杀的结束时间
	 */
	private Long endTime;
}
