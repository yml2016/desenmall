package com.desen.desenmall.order.dao;

import com.desen.desenmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 11:33:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
