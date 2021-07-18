package com.desen.desenmall.product.vo;



import com.desen.desenmall.product.entity.SkuImagesEntity;
import com.desen.desenmall.product.entity.SkuInfoEntity;
import com.desen.desenmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 @Description
 @see SkuItemVo
 @author yangminglin
 @date 2021/7/17
 @version V
**/
@ToString
@Data
public class SkuItemVo {

    /**
     * 基本信息
     */
    SkuInfoEntity info;

    boolean hasStock = true;

    /**
     * 图片信息
     */
    List<SkuImagesEntity> images;

    /**
     * 销售属性组合
     */
    List<ItemSaleAttrVo> saleAttr;

    /**
     * 介绍
     */
    SpuInfoDescEntity desc;

    /**
     * 参数规格信息
     */
    List<SpuItemAttrGroup> groupAttrs;

    /**
     * 秒杀信息
     */
    SeckillInfoVo seckillInfoVo;
}
