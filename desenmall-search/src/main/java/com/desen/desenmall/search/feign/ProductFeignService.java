package com.desen.desenmall.search.feign;



import com.desen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("desenmall-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    R getAttrsInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    R brandInfo(@RequestParam("brandIds") List<Long> brandIds);

}
