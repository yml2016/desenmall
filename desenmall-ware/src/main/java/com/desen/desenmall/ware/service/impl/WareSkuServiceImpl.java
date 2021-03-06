package com.desen.desenmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.desen.common.enume.OrderStatusEnum;
import com.desen.common.exception.NotStockException;
import com.desen.common.to.SkuHasStockVo;
import com.desen.common.to.mq.OrderTo;
import com.desen.common.to.mq.StockDetailTo;
import com.desen.common.to.mq.StockLockedTo;
import com.desen.common.utils.R;
import com.desen.desenmall.ware.entity.WareOrderTaskDetailEntity;
import com.desen.desenmall.ware.entity.WareOrderTaskEntity;
import com.desen.desenmall.ware.feign.OrderFeignService;
import com.desen.desenmall.ware.feign.ProductFeignService;
import com.desen.desenmall.ware.service.WareOrderTaskDetailService;
import com.desen.desenmall.ware.service.WareOrderTaskService;
import com.desen.desenmall.ware.vo.OrderItemVo;
import com.desen.desenmall.ware.vo.OrderVo;
import com.desen.desenmall.ware.vo.WareSkuLockVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.ware.dao.WareSkuDao;
import com.desen.desenmall.ware.entity.WareSkuEntity;
import com.desen.desenmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService orderTaskService;

    @Autowired
    private WareOrderTaskDetailService orderTaskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderFeignService orderFeignService;

    @Value("${myRabbitmq.MQConfig.eventExchange}")
    private String eventExchange;

    @Value("${myRabbitmq.MQConfig.routingKey}")
    private String routingKey;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1????????????????????????????????????????????????
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO ????????????sku???????????????????????????????????????????????????
            //1?????????catch??????
            //TODO ???????????????????????????????????????????????????????????????
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }


            wareSkuDao.insert(skuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }


    /**
     * ????????????????????????
     * SELECT SUM(stock - stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(id -> {
            SkuHasStockVo stockVo = new SkuHasStockVo();
            // ????????????sku???????????????
            stockVo.setSkuId(id);
            // ?????????????????????null ????????????????????????
            stockVo.setHasStock(baseMapper.getSkuStock(id)==null?false:true);
            return stockVo;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = NotStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // ????????????????????????????????? ????????????????????????
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);
        // [?????????]1. ??????????????????????????? ????????????????????????, ????????????
        // [?????????]1. ?????????????????????????????????????????????????????????????????????????????????
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock hasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            hasStock.setSkuId(skuId);
            // ????????????????????????????????????,todo ???????????????????????????????????????????????????????????????
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            hasStock.setWareId(wareIds);
            hasStock.setNum(item.getCount());
            return hasStock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if(wareIds == null || wareIds.size() == 0){
                // ?????????????????????????????????
                throw new NotStockException(skuId.toString());
            }
            // ???????????????????????????????????? ?????????????????????????????????????????????????????????MQ
            // ?????????????????? ???????????????????????????????????????
            for (Long wareId : wareIds) { //todo ????????????????????????????????????????????????????????????????????????????????????????????????
                // ??????????????? 1 ????????????0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if(count == 1){
                    // TODO ??????MQ?????????????????? ???????????????????????? ?????????????????????????????????
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null,skuId,"",hasStock.getNum() ,taskEntity.getId(),wareId,1);
                    orderTaskDetailService.save(detailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity, detailTo);
                    // ????????????????????????????????? ??????????????????
                    stockLockedTo.setDetailTo(detailTo);

                    rabbitTemplate.convertAndSend(eventExchange, routingKey ,stockLockedTo);
                    skuStocked = true;
                    break;
                }
                // ???????????????????????? ?????????????????????
            }
            if(!skuStocked){
                // ???????????????????????????????????????
                throw new NotStockException(skuId.toString());
            }
        }
        // 3.??????????????????
        return true;
    }


    @Override
    public void unlockStock(StockLockedTo to) {
        log.info("???????????????????????????");
        // ??????id
        Long id = to.getId();
        StockDetailTo detailTo = to.getDetailTo();
        Long detailId = detailTo.getId();
        /**
         * ????????????
         * 	??????????????????????????????????????????
         * 		???: ????????????????????????
         * 			1.??????????????????, ????????????
         * 			2.??????????????? ??????????????????
         * 				????????????????????????,????????????
         * 				????????????????????????	;
         * 		???????????????????????????????????? ??????????????? ????????????????????????
         */
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if(byId != null){
            // ??????
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            // ??????????????? ?????????????????? ????????????????????????
            R orderStatus = orderFeignService.getOrderStatus(orderSn);
            if(orderStatus.getCode() == 0){
                // ????????????????????????
                OrderVo orderVo = orderStatus.getData(new TypeReference<OrderVo>() {});
                // ???????????????
                if(orderVo == null || orderVo.getStatus() == OrderStatusEnum.CANCLED.getCode()){
                    // ??????????????? ??????1 ?????????  ?????????????????????
                    if(byId.getLockStatus() == 1){
                        unLock(detailTo.getSkuId(), detailTo.getWareId(), detailTo.getSkuNum(), detailId);
                    }
                }
            }else{
                // ???????????? ?????????????????? ???????????????????????????
                throw new RuntimeException("??????????????????");
            }
        }else{
            // ????????????
        }
    }

    /**
     * ????????????
     */
    private void unLock(Long skuId,Long wareId, Integer num, Long taskDeailId){
        // ????????????
        wareSkuDao.unlockStock(skuId, wareId, num);
        // ??????????????????????????????
        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(taskDeailId);
        detailEntity.setLockStatus(2);
        orderTaskDetailService.updateById(detailEntity);
    }

    /**
     * ???????????????????????? ????????????????????????????????? ???????????????????????? ??????????????????????????? ????????????????????????
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo to) {
        log.info("????????????????????????,??????????????????");
        String orderSn = to.getOrderSn();
        // ?????????????????????????????? ????????????????????????[Order???????????????????????????]
        WareOrderTaskEntity taskEntity = orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long taskEntityId = taskEntity.getId();
        // ??????????????????????????? ????????????????????? ???????????? ?????????1???????????????
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService
                .list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", taskEntityId)
                .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entities) {
            unLock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum(), entity.getId());
        }
    }

    @Data
    class SkuWareHasStock{

        private Long skuId;

        private List<Long> wareId;

        private Integer num;
    }

}