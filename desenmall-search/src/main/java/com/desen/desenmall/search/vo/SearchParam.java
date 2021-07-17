package com.desen.desenmall.search.vo;

import lombok.Data;

import java.util.List;


/**
 * catalog3Id=225&keyword=华为&sort=saleCount_asc&hasStock=0/1&brandId=25&brandId=30
 @Description：封装页面所有可能传递过来的关键字
 @see SearchParam
 @author yangminglin
 @date 2021/7/7
 @version V01
**/
@Data
public class SearchParam {

    /**
     * 全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hasStock_asc/desc
     */
    private String sort;

    /**
     * 库存
     */
    private Integer hasStock;

    /**
     * 价格区间
     */
    private String skuPrice; //0_100 100_500 _500 1000_

    /**
     * 品牌id 可以多选
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选，格式：attrs=1_安卓&attrs=5_其他:1080P
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生所有查询属性
     */
    private String _queryString;
}