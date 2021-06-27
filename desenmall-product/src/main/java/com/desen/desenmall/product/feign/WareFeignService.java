package com.desen.desenmall.product.feign;


import com.desen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>Title: WareFeignService</p>
 * Description：
 * date：2020/6/27 20:26
 */
@FeignClient("desenmall-ware")
public interface WareFeignService {

    /**
     * 修改整个系统的 R 带上泛型
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> SkuIds);
}
