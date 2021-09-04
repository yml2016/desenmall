package com.desen.desenmall.product.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.desen.desenmall.product.entity.CategoryEntity;
import com.desen.desenmall.product.service.CategoryService;
import com.desen.desenmall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * redisson使用
 */

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;
    /**
     * RLock锁有看门狗机制 会自动帮我们续期，默认30秒自动过期
     * lock.lock(10,TimeUnit.SECONDS); 十二猴子的时间一定要大于业务的时间 否则会出现死锁的情况
     * <p>
     * 如果我们传递了锁的超时时间就给redis发送超时脚本 默认超时时间就是我们指定的
     * 如果我们未指定，就使用 30 * 1000 [LockWatchdogTimeout]
     * 只要占锁成功 就会启动一个定时任务 任务就是重新给锁设置过期时间 这个时间还是 [LockWatchdogTimeout] 的时间 1/3 看门狗的时间续期一次 续成满时间
     */
    @ResponseBody
    @RequestMapping("/index/hello")
    public String hello() {
        RLock lock = redissonClient.getLock("my-lock");
        // 阻塞式等待
        lock.lock();
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 读写锁
     */
    @GetMapping("/index/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();
        String s = "";
        try {
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(3000);
            stringRedisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping("/index/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        String s = "";
        rLock.lock();
        try {
            s = stringRedisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

   /**
     * 闭锁 只有设定的人全通过才关门
     */
    @ResponseBody
    @GetMapping("/index/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 设置这里有5个人
        door.trySetCount(5);
        door.await();

        return "5个人全部通过了...";
    }

    @ResponseBody
    @GetMapping("/index/go/{id}")
    public String go(@PathVariable("id") Long id) throws InterruptedException {

        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 每访问一次相当于出去一个人
        door.countDown();
        return id + "走了";
    }

   /**
     * 尝试获取车位 [信号量]
     * 信号量:也可以用作限流
    * PubSub
     */
    @ResponseBody
    @GetMapping("/index/park")
    public String park() {

        RSemaphore park = redissonClient.getSemaphore("park");
        //park.acquire();//阻塞
        boolean acquire = park.tryAcquire();
        /*if(acquire){
            //可以用作限流，初始化1000个信号，能拿到就执行业务，否则不执行
            try {
                //执行业务
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                park.release(); //释放信号量
            }
        }*/

        return "获取车位 =>" + acquire;
    }

    /**
     * 释放车位
     */
    @ResponseBody
    @GetMapping("/index/go/park")
    public String goPark() {

        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "ok => 车位+1";
    }


    @ResponseBody
    @RequestMapping("/hello")
    public String hello(@Param("name") String name) {
        System.out.println("================="+name);
        stringRedisTemplate.opsForValue().set("name",name);

        System.out.println("================="+stringRedisTemplate.opsForValue().get("name"));
        return "hello";
    }



    @RequestMapping({"/", "index", "/index.html"})
    public String indexPage(Model model) {
        // 获取一级分类所有缓存
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        model.addAttribute("catagories", categorys);
        return "index";
    }

    @ResponseBody
    @RequestMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatlogJson() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

}
