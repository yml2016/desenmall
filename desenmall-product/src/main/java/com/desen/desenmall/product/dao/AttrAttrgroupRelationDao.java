package com.desen.desenmall.product.dao;

import com.desen.desenmall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> relationEntities);

    void saveBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> relationEntities);
}
