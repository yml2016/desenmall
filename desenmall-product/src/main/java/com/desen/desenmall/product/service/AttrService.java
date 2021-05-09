package com.desen.desenmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.product.entity.AttrEntity;
import com.desen.desenmall.product.vo.AttrRespVo;
import com.desen.desenmall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);
}

