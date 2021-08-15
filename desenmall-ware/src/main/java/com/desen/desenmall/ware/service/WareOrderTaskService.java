package com.desen.desenmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 11:45:32
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

