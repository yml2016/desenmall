package com.desen.desenmall.search.controller;

import com.desen.common.exception.BizCode;
import com.desen.common.to.es.SkuEsModel;
import com.desen.common.utils.R;
import com.desen.desenmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


/**
 * <p>Title: ElasticSaveController</p>
 * Description：商品上架 Controller
 * date：2020/6/8 21:13
 */
@Slf4j
@RequestMapping("search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    /**
     * 上架商品
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){

        boolean status;
        try {
            status = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误: {}", e);
            return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(), BizCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(!status){
            return R.ok();
        }
        return R.error(BizCode.PRODUCT_UP_EXCEPTION.getCode(), BizCode.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
