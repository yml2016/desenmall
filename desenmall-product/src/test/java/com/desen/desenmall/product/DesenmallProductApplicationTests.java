/*
package com.desen.desenmall.product;

import com.desen.desenmall.product.entity.BrandEntity;
import com.desen.desenmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
class DesenmallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Value("${name}")
    private String name;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setDescript("gggg哈哈哈哈呢");
        brandService.save(brandEntity);

        BrandEntity brandEntity1 = brandService.getById(1L);
        System.out.println("33333333333333" + brandEntity1.getName());
    }

    @Test
    public void getNacosConfig() {
        System.out.println("name = " + name);
    }

  */
/*  @Autowired
    OSSClient ossClient;*//*

    @Test
    public void uploadFile() throws FileNotFoundException {
        */
/*//*
/ yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
       // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5t99RuZJ71wXaQ1axZq8Q";
        String accessKeySecret = "6lCHST0wWz3vZaiGcUG64SZxS1Ose3R";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("D:\\yml.sql");
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("desenmall-product", "ymll.sql", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();*//*

    }

}
*/
