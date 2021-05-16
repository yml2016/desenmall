package com.desen.desenmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.desen.desenmall.product.entity.AttrEntity;
import com.desen.desenmall.product.service.AttrAttrgroupRelationService;
import com.desen.desenmall.product.service.AttrService;
import com.desen.desenmall.product.service.CategoryService;
import com.desen.desenmall.product.vo.AttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.desen.desenmall.product.entity.AttrGroupEntity;
import com.desen.desenmall.product.service.AttrGroupService;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.R;


/**
 * 属性分组
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        //PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    ///product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntities = attrService.attrRelation(attrgroupId);
        return R.ok().put("data", attrEntities);
    }

    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public R noattrRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page  = attrService.noattrRelation(attrgroupId, params);
        return R.ok().put("page", page);
    }

    ///product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteRelations(@RequestBody AttrGroupVo[] attrGroupVos) {
        relationService.deleteRelations(attrGroupVos);
        return R.ok();
    }
    ///product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R addRelations(@RequestBody List<AttrGroupVo> attrGroupVos) {
        relationService.addRelations(attrGroupVos);
        return R.ok();
    }
}
