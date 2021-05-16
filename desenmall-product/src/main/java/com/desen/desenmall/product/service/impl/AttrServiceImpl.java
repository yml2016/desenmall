package com.desen.desenmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.desen.common.constant.ProductConstant;
import com.desen.desenmall.product.dao.AttrAttrgroupRelationDao;
import com.desen.desenmall.product.dao.AttrGroupDao;
import com.desen.desenmall.product.dao.CategoryDao;
import com.desen.desenmall.product.entity.AttrAttrgroupRelationEntity;
import com.desen.desenmall.product.entity.AttrGroupEntity;
import com.desen.desenmall.product.entity.CategoryEntity;
import com.desen.desenmall.product.service.CategoryService;
import com.desen.desenmall.product.vo.AttrGroupVo;
import com.desen.desenmall.product.vo.AttrRespVo;
import com.desen.desenmall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.product.dao.AttrDao;
import com.desen.desenmall.product.entity.AttrEntity;
import com.desen.desenmall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //保存基本数据
        this.save(attrEntity);
        //保存关联关系
        if (attr.getAttrGroupId() != null && attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", "base".equals(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> obj.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        PageUtils pageUtils = new PageUtils(page);

        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> respVos = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //设置分类和分组的名字
            if ("base".equals(attrType)) {//销售属性不需要分组
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);

        //设置分组信息
        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        //设置分类信息
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        respVo.setCatelogPath(catelogPath);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        //修改分组关联
        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            int cout = relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            if (cout == 0) {
                relationDao.insert(relationEntity);
            }
        }

    }

    /**
     * 根据分组ID找到关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> attrRelation(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = relationEntities.stream().map(entity -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)) {
            return null;
        }
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        return attrEntities;
    }

    /**
     * 当前分组没有被关联的所有属性
     *
     * @param attrgroupId
     * @param params
     * @return
     */
    @Override
    public PageUtils noattrRelation(Long attrgroupId, Map<String, Object> params) {
        //1.当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.当前分组只能关联同分类下的分组没有关联的属性
        //2.1)当前分类下的分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds = attrGroupEntities.stream().map(entity -> {
            return entity.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2)这些分组关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        List<Long> attrIds = relationEntities.stream().map(entity -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());

        //2.3)从当前分类的所有属性中排除掉这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("attr_id", key).like("attr_name", key);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


}