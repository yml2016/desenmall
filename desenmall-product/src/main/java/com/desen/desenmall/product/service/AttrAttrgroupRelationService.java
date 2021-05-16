package com.desen.desenmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.product.entity.AttrAttrgroupRelationEntity;
import com.desen.desenmall.product.vo.AttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void deleteRelations(AttrGroupVo[] attrGroupVos);
    void addRelations(List<AttrGroupVo> attrGroupVos);

}

