package com.desen.desenmall.product.service.impl;

import com.desen.desenmall.product.entity.AttrEntity;
import com.desen.desenmall.product.service.AttrService;
import com.desen.desenmall.product.vo.AttrGroupWithAttrsVo;
import com.desen.desenmall.product.vo.SpuItemAttrGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.product.dao.AttrGroupDao;
import com.desen.desenmall.product.entity.AttrGroupEntity;
import com.desen.desenmall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        IPage<AttrGroupEntity> page = null;
        if (catelogId == null || catelogId == 0) {
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
        } else {
            wrapper.eq("catelog_id", catelogId);
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
        }

        return new PageUtils(page);
    }

    /**
     * ????????????id???????????????????????????????????????????????????
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //com.atguigu.gulimall.product.vo
        //1?????????????????????
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2?????????????????????
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group,attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());

        return collect;


    }


    @Override
    public List<SpuItemAttrGroup> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        // 1.????????????Spu???????????????????????????????????? ?????????????????????????????????????????????
        // 1.1 ??????????????????
        AttrGroupDao baseMapper = this.getBaseMapper();

        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }
}