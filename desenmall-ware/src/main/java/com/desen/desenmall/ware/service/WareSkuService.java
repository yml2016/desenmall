package com.desen.desenmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.to.SkuHasStockVo;
import com.desen.common.to.mq.OrderTo;
import com.desen.common.to.mq.StockLockedTo;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.ware.entity.WareSkuEntity;
import com.desen.desenmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 11:45:32
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockedTo to);
    /**
     * 由于订单超时而自动释放订单之后来解锁库存
     */
    void unlockStock(OrderTo to);
}

