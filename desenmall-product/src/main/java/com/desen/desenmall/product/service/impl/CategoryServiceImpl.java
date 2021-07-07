package com.desen.desenmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.desen.desenmall.product.service.CategoryBrandRelationService;
import com.desen.desenmall.product.vo.Catalog3Vo;
import com.desen.desenmall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.product.dao.CategoryDao;
import com.desen.desenmall.product.entity.CategoryEntity;
import com.desen.desenmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        return level1Menus;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        return children;
    }


    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    //@CacheEvict(value = {"category"},key = "'getLevel1Categorys'")//单个操作
    /*@Caching(evict={ //同时进行多种缓存操作
            @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
            @CacheEvict(value = {"category"},key = "'getCatelogJson'")
    })*/
    @CacheEvict(value = {"category"}, allEntries = true)//失效模式，批量删除category分区下的所有缓存
    //@CachePut //双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }


    /**
     * 测试使用缓存
     * @Cacheable： 触发将数据保存到缓存的操作, 根据业务设定好缓存的分区名字
     * @CacheEvict： 触发将数据从缓存删除的操作
     * @CachePut： 不影响方法执行更新缓存
     * @Caching： 组合以上多个操作
     * @CacheConfig： 在类级别共享缓存的相同配置
     */

    //缓存有结果就不会执行方法了，
    // key采用SpEL表达式
    //自定义缓存配置 RedissonConfig
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        log.info("==================getLevel1Categorys=================");
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    /**
     * 采用springcache的缓存机制
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> map = getCatelogJsonFromDb();
        return map;
    }

    /**
     * 自己实现的缓存机制
     * @return
     * @Override
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonByMe() {

        /**
         * 高并发下，需要枷锁，否则仍然会产生大量请求访问数据库的压力
         * 1.空结果缓存，解决缓存穿透
         * 2.设置过期时间（随机值），解决缓存雪崩
         * 3.加锁，解决缓存击穿
         */
        Map<String, List<Catelog2Vo>> fromRedis = getCatelogJsonFromRedis();
        if (fromRedis != null) {
            return fromRedis;
        }
        //Map<String, List<Catelog2Vo>> fromDb = getCatelogJsonFromDbWithRedisLock();
        Map<String, List<Catelog2Vo>> fromDb = getCatelogJsonFromDbWithRedissonLock();

        return fromDb;
    }

    /**
     * 从redis中获取数据
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //先获取缓存，有就直接返回，没有再考虑查询数据库
        String catelogJson = ops.get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            return null;
        }
        //以json格式存储，拿出来后进行转化为对象类型
        Map<String, List<Catelog2Vo>> fromRedis = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return fromRedis;
    }

    /**
     * 1.读取的时候，这样处理，没有问题了
     * 2.但是如果有写请求，就会有缓存一致性的问题
     * 对此有两种模式解决：
     *  a.双写模式，更新数据库，同时更新redis（会有脏数据，需要加锁（如果一致性要求不高，也可以不用管redis，等到redis过期即可））
     *  b.失效模式，更新数据库，然后删除redis数据（也会有脏数据，需要加锁）
     * 3.经常修改的数据不建议使用缓存；
     * 4.实在有必要且数据一致性要求高的，采用读写锁 RReadWriteLock；
     * 5.本系统一致性解决方案：
     *   a.采用缓存增加过期时间，数据过期下一次查询触发更新；
     *   b.读写数据的时候，加上分布式读写锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {
        //catelogJson 加具体锁名字，锁的粒度，越细越块。
        RLock lock = redissonClient.getLock("catelogJson-lock");
        lock.lock();
        try {
            //再查一次redis数据
            Map<String, List<Catelog2Vo>> fromRedis = getCatelogJsonFromRedis();
            if (fromRedis != null) {
                return fromRedis;
            }
            //查询数据库
            return getCatelogJsonFromDb();
        }
        finally {
            lock.unlock();
        }
    }




    /**
     * 使用redis自己实现的分布式锁
     * 1.加分布式锁再从数据库中获取数据。
     *  否则高并发下仍然会产生大量请求访问数据库的压力，
     *  缓存击穿问题
     * PS：本地锁没法保证所有请求只有一次访问数据库，
     * 但并不是不能使用本地锁，允许N个服务访问数据库的情况下也行,synchronized (this){}
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        Map<String, List<Catelog2Vo>> fromRedis = getCatelogJsonFromRedis();
        if (fromRedis != null) {
            return fromRedis;
        }
        /**
         * 1.加锁和过期时间，必须是一个原子操作，否则机器挂了,就会导致死锁
         * 2.请求a加锁过期时间，可能会导致业务没有执行完，锁就过期解掉了，
         *   解掉之后，请求b就可以进行加锁，但是请求a可能会误删调请求b的锁，然后请求c...
         * 3.避免误删他人之锁，可以加版本标识，如uuid，并且整个删锁过程是原子操作
         *   但是仍然会出现锁过期解掉后，请求b执行查库业务，
         * 4..我们希望的是如果服务挂了，锁就不会存在，对此
         *   a.给锁加过期时间，并进行续命,如redisson
         *   b.基于监控下的加锁，如ZK
         * 总结：加锁过程，和删锁过程，都必须是原子操作
         *       但是redis分布式锁，仍然有其不足，在于需要给锁加过期时间，也正因为过期时间的不确定性，所以要给锁续期。
         */
        //抢占分布式锁
        String uuid = UUID.randomUUID().toString();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        Boolean lock = ops.setIfAbsent("lock", uuid, Duration.ofSeconds(300));//加锁原子操作
        if (lock) {
            //加锁成功，执行业务查数据库
            log.info("加锁成功，执行业务查数据库");
            Map<String, List<Catelog2Vo>> fromDb = null;
            try {
                fromDb = getCatelogJsonFromDb();
            }
            catch (Exception e) {
                log.error("加锁成功，执行业务查数据库，异常");
            }
            finally {
                /*//这样仍然会导致误删，需要使用lua脚本，保证原子操作
                if(uuid.equals(ops.get("lock"))){
                    stringRedisTemplate.delete("lock");//删除锁
                }*/
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long isDel = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                        Arrays.asList("lock"), uuid);//解锁原子操作
                log.info("解锁:{}"+isDel,isDel);
            }
            return fromDb;
        } else {
            //加锁失败，等待，重试
            log.info("加锁失败，等待，重试");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();//自旋
        }
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {

        log.debug("查询数据库。。。。");
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catelog2Vo>> catelogMap = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        //ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //ops.set("catelogJson", JSON.toJSONString(catelogMap)); //json 跨语言平台兼容
        return catelogMap;
    }

    /**
     * 第一次查询的所有 CategoryEntity 然后根据 parent_cid去这里找
     */
    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> entityList, Long parent_cid) {

        return entityList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
    }

}