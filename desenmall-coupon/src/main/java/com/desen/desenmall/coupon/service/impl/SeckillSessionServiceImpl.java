package com.desen.desenmall.coupon.service.impl;

import com.desen.desenmall.coupon.entity.SeckillSkuRelationEntity;
import com.desen.desenmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.coupon.dao.SeckillSessionDao;
import com.desen.desenmall.coupon.entity.SeckillSessionEntity;
import com.desen.desenmall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {


    @Autowired
    private SeckillSkuRelationService skuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<SeckillSessionEntity> getLate3DaySession() {
        // 计算最近三天的时间
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime(), endTime()));
        if(!CollectionUtils.isEmpty(list)){
            return list.stream().map(session -> {
                // 给每一个活动写入他们的秒杀项
                Long id = session.getId();
                List<SeckillSkuRelationEntity> entities = skuRelationService
                        .list(new QueryWrapper<SeckillSkuRelationEntity>()
                        .eq("promotion_session_id", id));
                session.setRelationSkus(entities);
                return session;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private String startTime(){
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    private String endTime(){
        LocalDate acquired = LocalDate.now().plusDays(2);
        LocalDateTime end = LocalDateTime.of(acquired, LocalTime.MAX);
        return end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}