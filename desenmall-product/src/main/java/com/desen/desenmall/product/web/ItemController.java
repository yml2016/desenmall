package com.desen.desenmall.product.web;

import com.alibaba.fastjson.JSON;
import com.desen.desenmall.product.service.SkuInfoService;
import com.desen.desenmall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        log.debug("查询详情的skuId->{}", skuId);
        SkuItemVo vo = skuInfoService.item(skuId);
        log.debug("查询详情的结果->{}", JSON.toJSON(vo));
        model.addAttribute("item", vo);
        return "item";
    }
}