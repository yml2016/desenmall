package com.desen.desenmall.product;

import com.desen.desenmall.product.entity.BrandEntity;
import com.desen.desenmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class DesenmallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setDescript("gggg哈哈哈哈呢");
        brandService.save(brandEntity);

        BrandEntity brandEntity1 = brandService.getById(1L);
        System.out.println("33333333333333"+brandEntity1.getName());
    }

}
