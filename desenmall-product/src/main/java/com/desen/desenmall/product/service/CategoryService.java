package com.desen.desenmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.product.entity.CategoryEntity;
import com.desen.desenmall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);


    /**
     * 找到catelogId的完整路径；
     * [父/子/孙]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2Vo>> getCatelogJson();
}

