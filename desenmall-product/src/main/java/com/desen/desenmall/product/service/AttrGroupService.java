package com.desen.desenmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params,Long catelogId);
}

