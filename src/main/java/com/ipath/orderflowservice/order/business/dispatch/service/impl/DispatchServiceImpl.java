package com.ipath.orderflowservice.order.business.dispatch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.core.order.service.CoreOrderService;
import com.ipath.orderflowservice.feignclient.CacheFeign;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.ExtraService;
import com.ipath.orderflowservice.order.bean.param.OrderBaseInfoParam;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import com.ipath.orderflowservice.order.business.dispatch.bean.param.SubmitDispatchParam;
import com.ipath.orderflowservice.order.business.dispatch.bean.vo.DispatchCompanyLogicVo;
import com.ipath.orderflowservice.order.business.dispatch.bean.vo.OrderDispatchConfigVo;
import com.ipath.orderflowservice.order.business.dispatch.producer.DelayPlaceOrderProducer;
import com.ipath.orderflowservice.order.business.dispatch.service.DispatchService;
import com.ipath.orderflowservice.order.dao.OrderBaseMapper;
import com.ipath.orderflowservice.order.dao.OrderSourceMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;
import com.ipath.orderflowservice.order.task.OrderTask;

import com.ipath.orderflowservice.order.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 极速派单调度 service impl
 */
@Service
@Slf4j
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheFeign hcglFeign;
    @Autowired
    private OrderBaseMapper orderBaseMapper;
    @Autowired
    private SnowFlakeUtil snowFlakeUtil;
    @Autowired
    private DelayPlaceOrderProducer delayPlaceOrderProducer;
    @Autowired
    private CoreOrderService coreOrderService;
    @Autowired
    private OrderSourceMapper orderSourceMapper;

    @Autowired
    private OrderTask orderTask;

    @Autowired
    private CacheUtil cacheutil;


    // 企业条件 key
    public static final String KEY_CRITERIA_COMPANY = "icar:config:company:dispatchresponse:{}";
    // 用户条件 key
    public static final String KEY_CRITERIA_USER = "icar:config:company:dispatch:user:{}";


    // 订单条件 key
    public static final String KEY_CRITERIA_ORDER = "icar:config:company:dispatch:fee:config:{}";

    // 已经下单流程锁 + orderId key
    public static final String KEY_DISPATCH_FLOW_LOCK = "dispatch:flow:lock:";

    // 下单信息 + orderId key
    public static final String KEY_DISPATCH_PALCEORDERPARMTER = "palceOrderParmter:";

    // 新极速派单画像
    public static final String REDIS_KEY_DISPATCH_COMPANY = "icar:config:dispatch:company:{}"; // 极速派车-用户画像-企业维度

    public static final String REDIS_KEY_DISPATCH_COMPANY_USER = "icar:config:dispatch:company:user:{}"; // 极速派车-用户画像-用户维度

    // 新极速派单画像

    /**
     * 极速派单调度 下单
     *
     * @param requestPlaceOrderDto
     * @param orderParam
     * @return
     */
    @Override
    public String placeOrder(RequestPlaceOrderDto requestPlaceOrderDto, CreateOrderParam orderParam) {

        if (orderParam.getServiceType() != 1) {
            log.info("订单号:{},需求:极速派单调度 下单 校验不通过:[服务类型非实时类型],serviceType:{}", orderParam.getOrderId(),
                    orderParam.getServiceType());
            return null;
        }
        boolean isUserSelectionDrivers = this.checkUserSelectionDrivers(orderParam.getExtraServices());
        if (isUserSelectionDrivers){
            log.info("订单号:{},需求:极速派单调度 下单 校验不通过:[双选司机订单], extraServices:{}", orderParam.getOrderId(),JSONUtil.toJsonStr(orderParam.getExtraServices()),
                    orderParam.getServiceType());
            return null;
        }

        // 企业条件
        // 极速派单调度 下单校验 企业条件
        boolean checkCompanyResult = this.checkCompany(orderParam.getCompanyId());
        if (!checkCompanyResult) {
            log.info("订单号:{},需求:极速派单调度 下单 企业条件校验不通过:[未开启极速派单],CompanyId:{}", orderParam.getOrderId(),
                    orderParam.getCompanyId());
            return null;
        }

        // 用户条件
        // 极速派单调度 下单校验 用户条件
        boolean checkUserResult = this.checkUserTemp(orderParam.getCompanyId(), orderParam.getUserId());
        if (!checkUserResult) {
            log.info("订单号:{},需求:极速派单调度 下单 用户条件校验不通过:[未匹配极速派单],UserId:{}", orderParam.getOrderId(),
                    orderParam.getUserId());
            return null;
        }

        // 订单条件
        // 极速派单调度 下单校验 订单条件
        OrderDispatchConfigVo companyDispatchConfig = this.checkOrder(requestPlaceOrderDto, orderParam);
        if (companyDispatchConfig == null) {
            log.info("订单号:{},需求:极速派单调度 下单 订单条件校验不通过:[未匹配极速派单]", orderParam.getOrderId());
            return null;
        }
        log.info("订单号:{},需求:极速派单调度 下单 订单条件校验通过:[配置信息:{}]", orderParam.getOrderId(),
                JSONUtil.toJsonStr(companyDispatchConfig));

        String coreOrderId = snowFlakeUtil.getNextId().toString();
        JSONObject dataObject = new JSONObject();
        //long delayTime = companyDispatchConfig.getOutTime() + companyDispatchConfig.getPopupTime()  + companyDispatchConfig.getDelayTime();
        //上修改为下
        long delayTime = companyDispatchConfig.getDelayTime();
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderParam.getOrderId());

        String customInfo = orderBase.getCustomInfo();
        JSONObject customJSONObject = new JSONObject(customInfo);
        List<String> carList = new ArrayList<>();
        if (customJSONObject.containsKey("selectedCars") && customJSONObject.get("selectedCars") != null) {
            JSONArray jsonArray = customJSONObject.getJSONArray("selectedCars");
            if (jsonArray.size() > 0) {
                List<SelectedCar> selectedCars = JSONUtil.toList(jsonArray, SelectedCar.class);
                carList = selectedCars.stream().map(car -> car.getCarSourceId() + "_" + car.getCarLevel())
                        .collect(Collectors.toList());
            }
        }
        companyDispatchConfig.setCarList(carList);
        customJSONObject.put("dispatchConfig", companyDispatchConfig);
        customJSONObject.put("passingPoints", orderParam.getPassingPoints());
        orderBase.setCustomInfo(customJSONObject.toString());

        orderBaseMapper.updateByPrimaryKeySelective(orderBase);
        if (delayTime == 0) {
            return null;
        }

        redisUtil.set(KEY_DISPATCH_PALCEORDERPARMTER + orderParam.getOrderId(), requestPlaceOrderDto, 60 * 60);
        dataObject.put("orderId", requestPlaceOrderDto.getPartnerOrderId());
        dataObject.put("coreOrderId", coreOrderId);
        delayPlaceOrderProducer.sendDelayPlaceOrder(dataObject, delayTime);

        log.info("订单号:{},需求:极速派单调度 下单 延时下单:[延时时间:{},返回coreOrderId:{} ]", orderParam.getOrderId(), delayTime,
                coreOrderId);
        return coreOrderId;
    }

    private boolean checkUserSelectionDrivers(List<ExtraService> extraServices) {
        boolean isUserSelectionDrivers = false;
        try {
            if (CollectionUtils.isNotEmpty(extraServices)) {
                List<ExtraService> collect = extraServices.stream().filter(
                                o -> org.apache.commons.lang3.StringUtils.equals(o.getCode(), "ES0008"))
                        .collect(Collectors.toList());
                isUserSelectionDrivers = CollectionUtils.isNotEmpty(collect);
            }
        } catch (Exception e) {
            log.info("校验极速派单是否为双选司机单失败: {}", e);
        }
        return isUserSelectionDrivers;
    }

    /**
     * cancelOrder
     *
     * @return
     */
    @Override
    public void cancelOrder(OrderBase orderBase, OrderSource orderSource) {
//        log.info("订单号:{},需求:极速派单调度 取消", orderBase.getId());
//        OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
//        if (null != dispatchConfig && (StringUtils.isBlank(dispatchConfig.getSubmitType())
//                || StringUtils.equals(dispatchConfig.getSubmitType(), "0"))) {
//            boolean isLock = redisUtil.setLock(KEY_DISPATCH_FLOW_LOCK + orderBase.getId(), "已经取消订单", 10 * 60);
//            if (!isLock) {
//                Object lockInfo = redisUtil.get(KEY_DISPATCH_FLOW_LOCK + orderBase.getId());
//                log.info("订单号:{},需求:极速派单调度 取消 未获取到锁原因:{}", orderBase.getId(), lockInfo.toString());
//                return;
//            } else {
//                log.info("订单号:{},需求:极速派单调度 取消 获取到锁 走延时下单后取消流程", orderBase.getId());
//            }
//            // 需求:极速派单调度 下单(取消/提交 立即)
//            this.immediatelyPlaceOrder(orderBase.getId(), true);
//            redisUtil.delete(KEY_DISPATCH_PALCEORDERPARMTER + orderBase.getId());
//        } else {
//            log.info("订单号:{},需求:极速派单调度 取消 极速派单配置:{},  正常走取消流程，直接取消", orderBase.getId(),JSONUtil.toJsonStr(dispatchConfig));
//        }

    }

    /**
     * 立即下单
     *
     * @param orderId
     * @return
     */
    @Override
    public String immediatelyPlaceOrder(long orderId, boolean isdelay) {
//        try {
//            log.info("订单号:{},需求:极速派单调度 下单(取消/提交 立即)", orderId);
//
//            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
//            OrderSource orderSource = orderSourceMapper.selectByOrderId(orderBase.getId());
//
//            // 预估
//            Map<String, Object> estimate = this.estimate(orderBase);
//            List<SelectedCar> cars = new ArrayList<>();
//            if (orderBase.getCustomInfo() != null) {
//                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
//                if (customJSONObject != null) {
//                    if (customJSONObject.containsKey("selectedCars")) {
//                        cars = customJSONObject.getBeanList("selectedCars", SelectedCar.class);
//                    }
//                }
//            }
//            String estimateId = (String) estimate.get("estimateId");
//
//            if (null != orderBase.getCustomInfo()) {
//                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
//                if (customJSONObject.containsKey("passingPoints") && customJSONObject.getJSONArray("passingPoints").size() > 0) {
//                    cars = cars.stream().filter(t -> t.getCarSourceId() == 5).collect(Collectors.toList());
//                }
//            }
//
//            cars = this.getEstimateCarList(estimate, cars, isdelay);
//
//            Date date = (Date) estimate.get("date");
//            Object o = redisUtil.get(KEY_DISPATCH_PALCEORDERPARMTER + orderId);
//            RequestPlaceOrderDto reqOld = (RequestPlaceOrderDto) o;
//
//            RequestPlaceOrderDto req = BeanUtil.copyProperties(reqOld, RequestPlaceOrderDto.class);
//
//            // 重新下单
//            req.setUserId(orderBase.getUserId());
//            req.setPartnerOrderId(orderBase.getId().toString());
//            req.setCompanyId(orderBase.getCompanyId());
//            req.setAccountId(orderBase.getAccountId().toString());
//            req.setEstimateId(estimateId);
//            req.setSceneId(orderBase.getSceneId().toString());
//            req.setCars(cars);
//            req.setServiceType(orderBase.getServiceType());
//            req.setDepartLat(orderBase.getDepartLat());
//            req.setDepartLng(orderBase.getDepartLng());
//            req.setDepartCityCode(orderBase.getDepartCityCode());
//            req.setPickupLocation(orderBase.getPickupLocation());
//            req.setPickupLocationName(orderBase.getPickupLocationName());
//            req.setDestLat(orderBase.getDestLat());
//            req.setDestLng(orderBase.getDestLng());
//            req.setDestCityCode(orderBase.getDestCityCode());
//            req.setDestLocation(orderBase.getDestLocation());
//            req.setDestLocationName(orderBase.getDestLocationName());
//            req.setUserPhone(orderBase.getUserPhone());
//            req.setPassengerPhone(orderBase.getPassengerPhone());
//            req.setDepartTime(orderBase.getDepartTime());
//            req.setUserAI(true);
//
//            if (redisUtil.hasKey(
//                    CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + String.valueOf(estimateId))) {
//                JSONArray jsonArray = JSONUtil
//                        .parseArray(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX
//                                + String.valueOf(estimateId)).toString());
//                req.setPassingPoints(jsonArray);
//            }
//
//            req.setCoreOrderId(orderSource.getCoreOrderId() + "");
//
//            orderTask.notifyLog(null, req.getEstimateId(), req.getUserId(), Long.valueOf(req.getPartnerOrderId()),
//                    FirstCategoryEnum.PLACE_ORDER,
//                    null, RecordTypeEnum.RECORD, LogLevelEnum.INFO, ServiceEnum.ORDER_KA,
//                    "submitDispatch-->immediatelyPlaceOrder", JSONUtil.toJsonStr(req), new Date());
//
//            BaseResponse response = coreOrderService.placeOrderRes(req);
//            JSONObject dataObject = new JSONObject(response.getData());
//            if (response.getCode() != 0) {
//                if (StrUtil.isNotEmpty(response.getMessage())
//                        && ("已经存在的coreOrderId:" + orderSource.getCoreOrderId()).equals(response.getMessage())) {
//                    coreOrderService.placeOrderRes(req);
//                    dataObject = new JSONObject(response.getData());
//                } else {
//                    log.error("depayPlaceOrder ===> 延时下单触发 中台返回失败{}", response.getMessage());
//                }
//
//            }
//            updateVirtual(orderId, dataObject);
//
//            log.info("订单号:{},需求:极速派单调度 下单(取消/提交 立即 成功)", orderId);
//            redisUtil.delete(KEY_DISPATCH_PALCEORDERPARMTER + orderId);
//            return dataObject.getStr("coreOrderId");
//        } catch (Exception e) {
//            log.error("depayPlaceOrder ===> 延时下单触发 异常", e);
//        }

        return null;
    }

    @Override
    public String depayPlaceOrder(long orderId, long coreOrderId) {
//        try {
//            if (!redisUtil.hasKey(KEY_DISPATCH_PALCEORDERPARMTER + orderId)) {
//                log.info("订单号:{},需求:极速派单调度 下单(mq 延时), 校验不通过:[缓存 {} 为空]", orderId,
//                        KEY_DISPATCH_PALCEORDERPARMTER + orderId);
//                return null;
//            }
//            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
//
//            // 预估
//            Map<String, Object> estimate = this.estimate(orderBase);
//            List<SelectedCar> cars = new ArrayList<>();
//            if (orderBase.getCustomInfo() != null) {
//                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
//                if (customJSONObject != null) {
//                    if (customJSONObject.containsKey("selectedCars")) {
//                        cars = customJSONObject.getBeanList("selectedCars", SelectedCar.class);
//                    }
//                }
//            }
//            String estimateId = (String) estimate.get("estimateId");
//            cars = this.getEstimateCarList(estimate, cars, false);
//            Date date = (Date) estimate.get("date");
//            Object o = redisUtil.get(KEY_DISPATCH_PALCEORDERPARMTER + orderId);
//            RequestPlaceOrderDto reqOld = (RequestPlaceOrderDto) o;

//            RequestPlaceOrderDto req = BeanUtil.toBean(reqOld, RequestPlaceOrderDto.class);
//
//            // 重新下单
//            req.setUserId(orderBase.getUserId());
//            req.setPartnerOrderId(orderBase.getId().toString());
//            req.setCompanyId(orderBase.getCompanyId());
//            req.setAccountId(orderBase.getAccountId().toString());
//            req.setEstimateId(estimateId);
//            req.setSceneId(orderBase.getSceneId().toString());
//            req.setCars(cars);
//            req.setServiceType(orderBase.getServiceType());
//            req.setDepartLat(orderBase.getDepartLat());
//            req.setDepartLng(orderBase.getDepartLng());
//            req.setDepartCityCode(orderBase.getDepartCityCode());
//            req.setPickupLocation(orderBase.getPickupLocation());
//            req.setPickupLocationName(orderBase.getPickupLocationName());
//            req.setDestLat(orderBase.getDestLat());
//            req.setDestLng(orderBase.getDestLng());
//            req.setDestCityCode(orderBase.getDestCityCode());
//            req.setDestLocation(orderBase.getDestLocation());
//            req.setDestLocationName(orderBase.getDestLocationName());
//            req.setUserPhone(orderBase.getUserPhone());
//            req.setPassengerPhone(orderBase.getPassengerPhone());
//            req.setDepartTime(orderBase.getDepartTime());
//            req.setUserAI(true);

//            if (redisUtil.hasKey(
//                    CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + String.valueOf(estimateId))) {
//                JSONArray jsonArray = JSONUtil
//                        .parseArray(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX
//                                + String.valueOf(estimateId)).toString());
//                req.setPassingPoints(jsonArray);
//            }
//
//            req.setCoreOrderId(coreOrderId + "");

//            boolean b = redisUtil.setLock(KEY_DISPATCH_FLOW_LOCK + orderId, "已经延迟下单了", 10 * 60);
//            if (!b) {
//                return null;
//            }
//
//            orderTask.notifyLog(null, req.getEstimateId(), req.getUserId(), Long.valueOf(req.getPartnerOrderId()),
//                    FirstCategoryEnum.PLACE_ORDER,
//                    null, RecordTypeEnum.RECORD, LogLevelEnum.INFO, ServiceEnum.ORDER_KA,
//                    "mq-->depayPlaceOrder", JSONUtil.toJsonStr(req), new Date());
//
//            BaseResponse response = coreOrderService.placeOrderRes(req);
//            JSONObject dataObject = new JSONObject(response.getData());
//            if (response.getCode() != 0) {
//                if (StrUtil.isNotEmpty(response.getMessage())
//                        && ("已经存在的coreOrderId:" + coreOrderId).equals(response.getMessage())) {
//                    coreOrderService.placeOrderRes(req);
//                    dataObject = new JSONObject(response.getData());
//                } else {
//                    log.error("depayPlaceOrder ===> 延时下单触发 中台返回失败{}", response.getMessage());
//                }
//
//            }
//
//            updateVirtual(orderId, dataObject);

//            return dataObject.getStr("coreOrderId");
//        } catch (Exception e) {
//            log.error("depayPlaceOrder ===> 延时下单触发 异常", e);
//        }

        return null;
    }

    private void updateVirtual(long orderId, JSONObject dataObject) {
        try {
            String passengerPhoneVirtual = dataObject.getStr("passengerPhoneVirtual");
            String userPhoneVirtual = dataObject.getStr("userPhoneVirtual");

            if (ObjectUtil.isNotEmpty(passengerPhoneVirtual) || ObjectUtil.isNotEmpty(userPhoneVirtual)) {
                // 2024-10-12 记录虚拟号, 并缓存1小时通知config服务时使用
                OrderBase updatePhoneVirtual = new OrderBase();
                updatePhoneVirtual.setId(orderId);
                updatePhoneVirtual.setUserPhoneVirtual(userPhoneVirtual);
                updatePhoneVirtual.setPassengerPhoneVirtual(passengerPhoneVirtual);
                orderBaseMapper.updateByPrimaryKeySelective(updatePhoneVirtual);
            }
        } catch (Exception e) {
            log.info("修改虚拟号异常");
        }
    }

    /**
     * 需求:极速派单调度 提交
     *
     * @param param
     * @throws Exception
     */
    @Override
    public void submitDispatch(SubmitDispatchParam param) {
        // log.info("订单号:{},需求:极速派单调度 提交信息:[{}]", param.getOrderId(),
        // JSONUtil.toJsonStr(param));
        // 校验
        String submitType = param.getSubmitType();
        Long orderId = param.getOrderId();
        if (StringUtils.isBlank(submitType)) {
            throw new BusinessException("提交选项 不能为空");
        }
        if (null == orderId) {
            throw new BusinessException("订单id 不能为空");
        }

        try {
            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
            String customInfo = orderBase.getCustomInfo();
            if (customInfo != null) {
                JSONObject customJSONObject = new JSONObject(customInfo);
                if (customJSONObject.containsKey("dispatchConfig")) {
                    OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
                    dispatchConfig.setSubmitType(submitType);
                    customJSONObject.put("dispatchConfig", dispatchConfig);
                    orderBase.setCustomInfo(customJSONObject.toString());
                    orderBaseMapper.updateByPrimaryKey(orderBase);
                } else {
                    log.info("订单号:{},需求:极速派单调度 orderBase表customInfo.dispatchConfig 是空", param.getOrderId());
                }
            }
//            if (StringUtils.equals(submitType, "1")) {
//                log.info("订单号:{},需求:极速派单调度 提交信息:[加价调度]", param.getOrderId(), JSONUtil.toJsonStr(param));
//                boolean isLock = redisUtil.setLock(KEY_DISPATCH_FLOW_LOCK + orderId, "已经接受调度费了", 10 * 60);
//                if (!isLock) {
//                    log.info("订单号:{},需求:极速派单调度 提交信息:[加价调度] 未获取到锁，{}={}", param.getOrderId(),
//                            KEY_DISPATCH_FLOW_LOCK + orderId,
//                            redisUtil.get(KEY_DISPATCH_FLOW_LOCK + orderId).toString());
//                    return;
//                }
//                log.info("订单号:{},需求:极速派单调度 提交信息:[加价调度] 立即下单", param.getOrderId(), JSONUtil.toJsonStr(param));
//                // 需求:极速派单调度 下单(取消/提交 立即)
//                if (redisUtil.hasKey(KEY_DISPATCH_PALCEORDERPARMTER + orderId)) {
//                    this.immediatelyPlaceOrder(orderBase.getId(), false);
//                }
//
//            } else {
//                log.info("订单号:{},需求:极速派单调度 提交信息:[我再等等]", param.getOrderId(), JSONUtil.toJsonStr(param));
//            }

        } catch (Exception e) {
            log.error("极速派单调度 提交调度 异常", e);
        }

    }

    /**
     * 需求:极速派单调度 中台状态5通知 （结算处理）
     */
    @Override
    public void notifyOrderStatus5Settle(RequestBillNotifySettleOrderDto requestSettleOrderDto, OrderBase orderBase,
            OrderSource orderSource) {
        log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）", orderBase.getId());
        try {
            DispatchFee dispatchFee = new DispatchFee();
            dispatchFee.setDispatchResponse(false);
            dispatchFee.setDispatchAmount(new BigDecimal("0"));

            OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
            // UpgradeModel upgradeCar = requestSettleOrderDto.getUpgradeCar();
            // if (null != upgradeCar && upgradeCar.isUpgradeCar()) {
            // requestSettleOrderDto.setDispatchFee(dispatchFee);
            // log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）校验不通过[免费升舱成功]", orderBase.getId());
            // return;
            // }
            if (StringUtils.equals(dispatchConfig.getSubmitType(), "1")) {
                List<String> carList = dispatchConfig.getCarList();
                String sourceCode = orderSource.getSourceCode();
                Integer carLevel = orderSource.getCarLevel();
                long count = carList.stream().filter(car -> {
                    if (StringUtils.equals(car.split("_")[0], sourceCode)
                            && StringUtils.equals(car.split("_")[1], carLevel + "")) {
                        return true;
                    }
                    return false;
                }).count();
                if (count > 0) {
                    dispatchFee.setDispatchResponse(true);
                    dispatchFee.setDispatchAmount(dispatchConfig.getDispatchFeeAmount());
                    requestSettleOrderDto.setDispatchFee(dispatchFee);
                    log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）加调度费成功 参数:[{}]", orderBase.getId(),
                            JSONUtil.toJsonStr(requestSettleOrderDto));
                } else {
                    log.info(
                            "订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）校验不通过,不加调度费原因:[接单车型:sourceCode={},carLevel={},加调度费车型:carList={}]",
                            orderBase.getId(), sourceCode, carLevel, JSONUtil.toJsonStr(carList));
                }
            } else {
                log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）校验不通过,不加调度费原因:[用户提交信息SubmitType={}],不是1加价调度",
                        orderBase.getId(), dispatchConfig.getSubmitType());
            }

        } catch (Exception e) {
            log.error("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）订单结算处理异常" + e, orderBase.getId());
        }
    }

    /**
     * 需求:极速派单调度 中台状态5通知 （结算处理）
     */
    @Override
    public void notifyOrderStatus5SettleNew(SettleReq settleReq, OrderBase orderBase, OrderSource orderSource) {
        log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）", orderBase.getId());
        try {
            DispatchFee dispatchFee = new DispatchFee();
            dispatchFee.setDispatchResponse(false);
            dispatchFee.setDispatchAmount(new BigDecimal("0"));

            OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);

            if (null != dispatchConfig && StringUtils.equals(dispatchConfig.getSubmitType(), "1")) {
                List<String> carList = dispatchConfig.getCarList();
                String sourceCode = orderSource.getSourceCode();
                Integer carLevel = orderSource.getCarLevel();
                long count = carList.stream().filter(car -> {
                    if (StringUtils.equals(car.split("_")[0], sourceCode)
                            && StringUtils.equals(car.split("_")[1], carLevel + "")) {
                        return true;
                    }
                    return false;
                }).count();
                if (count > 0) {
                    dispatchFee.setDispatchResponse(true);
                    dispatchFee.setDispatchAmount(dispatchConfig.getDispatchFeeAmount());
                    settleReq.setDispatchFee(dispatchFee);
                    log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）加调度费成功 参数:[{}]", orderBase.getId(),
                            JSONUtil.toJsonStr(settleReq));
                } else {
                    log.info(
                            "订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）校验不通过,不加调度费原因:[接单车型:sourceCode={},carLevel={},加调度费车型:carList={}]",
                            orderBase.getId(), sourceCode, carLevel, JSONUtil.toJsonStr(carList));
                }
            } else {
                log.info("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）校验不通过,不加调度费原因:[用户提交信息SubmitType={}],不是1加价调度",
                        orderBase.getId(), null == dispatchConfig ? "0" : dispatchConfig.getSubmitType());
            }

        } catch (Exception e) {
            log.error("订单号:{},需求:极速派单调度 中台状态5通知 （结算处理）订单结算处理异常" + e, orderBase.getId());
        }
    }

    /**
     * 需求:极速派单调度 中台状态5通知 （合规预警处理）
     */
    @Override
    public void notifyOrderStatus5AbnormalOrder(OrderBase orderBase, OrderSource orderSource) {
        try {
            OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
            if (null != dispatchConfig) {
                log.info("订单号:{},需求:极速派单调度 中台状态5通知 （合规预警处理）配置dispatchConfig:[{}]", orderBase.getId(),
                        JSONUtil.toJsonStr(dispatchConfig));
                if (StringUtils.equals(dispatchConfig.getSubmitType(), "1")) {
                    BigDecimal amount = orderSource.getAmount();
                    orderSource.setAmount(amount.subtract(dispatchConfig.getDispatchFeeAmount()));
                    if (orderSource.getCompanyBearAmount().compareTo(new BigDecimal("0")) == 1) {
                        orderSource.setCompanyBearAmount(
                                orderSource.getCompanyBearAmount().subtract(dispatchConfig.getDispatchFeeAmount()));
                    } else {
                        if (orderSource.getUserBearAmount().compareTo(new BigDecimal("0")) == 1) {
                            orderSource.setUserBearAmount(
                                    orderSource.getUserBearAmount().subtract(dispatchConfig.getDispatchFeeAmount()));
                        }
                    }
                    log.info("订单号:{},需求:极速派单调度 中台状态5通知 （合规预警处理）处理成功:[原订单金额={},现在订单金额={},调度费={}]", orderBase.getId(),
                            amount, orderSource.getAmount(), dispatchConfig.getDispatchFeeAmount());
                } else {
                    log.info("订单号:{},需求:极速派单调度 中台状态5通知 （合规预警处理）不处理，原因:[用户提交信息SubmitType={}],不是1加价调度",
                            orderBase.getId(), dispatchConfig.getSubmitType());
                }
            }
        } catch (Exception e) {
            log.error("订单号:{},需求:极速派单调度 中台状态5通知 （合规预警处理）异常{}", orderSource.getOrderId(), e);
        }
    }

    /**
     * 极速派单调度 查询订单基础信息 设置极速派单配置
     *
     * @param param
     * @param orderBase
     */
    @Override
    public void setOrderBaseInfoDispatchConfig(OrderBaseInfoParam param, OrderBase orderBase) {
        try {
            OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
            if (null != dispatchConfig && StringUtils.isBlank(dispatchConfig.getSubmitType())) {
                param.setDispatchConfig(dispatchConfig);
            }
        } catch (Exception e) {
            log.error("setOrderBaseInfoDispatchConfig ===> 获取订单保存的极速派单配置 异常", e);
        }
    }

    /**
     * 获取订单保存的极速派单配置
     *
     * @param orderBase
     */
    private OrderDispatchConfigVo getDispatchConfig(OrderBase orderBase) {
        String customerInfo = orderBase.getCustomInfo();
        try {
            if (customerInfo != null) {
                JSONObject customJSONObject = new JSONObject(customerInfo);
                if (customJSONObject != null && customJSONObject.containsKey("dispatchConfig")) {
                    OrderDispatchConfigVo dispatchConfig = JSONUtil.toBean(customJSONObject.getStr("dispatchConfig"),
                            OrderDispatchConfigVo.class);

                    if (null != orderBase.getState() && orderBase.getState() == 1) {
                        Date createTime = orderBase.getCreateTime();
                        Date date = new Date();
                        long differenceInMillis = date.getTime() - createTime.getTime();
                        long seconds = differenceInMillis / 1000;
                        long residuePopupTime = dispatchConfig.getPopupTime() - seconds;
                        dispatchConfig.setResiduePopupTime(residuePopupTime);
                        if (residuePopupTime < 0) {
                            dispatchConfig.setResidueOutTime(dispatchConfig.getOutTime() + residuePopupTime);
                        } else {
                            dispatchConfig.setResidueOutTime(dispatchConfig.getOutTime());
                        }
                    }
                    return dispatchConfig;
                }
            }
        } catch (Exception e) {
            log.error("getDispatchConfig ===> 获取订单保存的极速派单配置 异常", e);

        }
        return null;
    }

    /**
     * 本次预估 取值 上一次预估车型
     */
    public List<SelectedCar> getEstimateCarList(Map<String, Object> estimateNew, List<SelectedCar> carsOld,
            boolean isdelay) {
        List<SelectedCar> selectedCarList = new ArrayList<SelectedCar>();
        JSONArray estimateCarArray = JSONUtil.parseArray(estimateNew.get("rows"));
        log.info("本次预估 取值 上一次预估车型 预估{}", estimateCarArray.toString());
        for (int i = 0; i < estimateCarArray.size(); i++) {
            JSONObject carObject = estimateCarArray.get(i, JSONObject.class);
            int carType = carObject.getInt("carType", -1);
            int carSource = carObject.getInt("carSource", -1);
            String subCarType = carObject.getStr("subCarType");
            BigDecimal estimatePrice = carObject.getBigDecimal("estimatePrice");
            BigDecimal actualEstimatePrice = carObject.getBigDecimal("platformEstimatePrice");
            String dynamicCode = carObject.getStr("dynamicCode");
            String carTypeName = carObject.getStr("carTypeName");

            for (int j = 0; j < carsOld.size(); j++) {
                SelectedCar selectedCar = carsOld.get(j);
                int carTypeSelectedCar = selectedCar.getCarLevel();
                int carSourceSelectedCar = selectedCar.getCarSourceId();
                String carTypeNameSelectedCar = selectedCar.getCarLevelName();

                if (carType == carTypeSelectedCar &&
                        carSource == carSourceSelectedCar &&
                        StrUtil.equals(subCarType, selectedCar.getSubCarType()) &&
                        org.apache.commons.lang3.StringUtils.equals(carTypeName, carTypeNameSelectedCar)) {
                    SelectedCar selectedCar1 = new SelectedCar();
                    selectedCar1.setCarLevel(carType);
                    selectedCar1.setCarSourceId(carSource);
                    selectedCar1.setEstimatePrice(estimatePrice);
                    selectedCar1.setActualEstimatePrice(actualEstimatePrice);
                    selectedCar1.setDynamicCode(dynamicCode);
                    selectedCar1.setCarLevelName(carTypeName);
                    selectedCar1.setSubCarType(subCarType);
                    if (isdelay) {
                        selectedCar1.setDelay(5);
                    }
                    selectedCarList.add(selectedCar1);
                }

            }
        }
        return selectedCarList;
    }

    /**
     * 追加车型下单
     */
    @Override
    public boolean appendPlaceOrder(RequestAppendPlaceOrderDto requestAppendPlaceOrderDto, OrderBase orderBase,
            JSONObject customObject) {

        try {
            OrderDispatchConfigVo dispatchConfig = this.getDispatchConfig(orderBase);
            if (null != dispatchConfig && StringUtils.equals(dispatchConfig.getSubmitType(), "1")) {
               // return false;
            } else {
                String customInfo = orderBase.getCustomInfo();
                if (customInfo != null) {
                    JSONObject customJSONObject = new JSONObject(customInfo);
                    if (customJSONObject.containsKey("dispatchConfig")) {
                        List<SelectedCar> cars = requestAppendPlaceOrderDto.getCars();
                        List<String> carList = cars.stream().map(car -> car.getCarSourceId() + "_" + car.getCarLevel())
                                .collect(Collectors.toList());
                        List<String> carList1 = dispatchConfig.getCarList();
                        if (CollectionUtils.isNotEmpty(carList1)) {
                            carList1.addAll(carList);
                            dispatchConfig.setCarList(carList1);
                        } else {
                            dispatchConfig.setCarList(carList);
                        }
                        customObject.put("dispatchConfig", dispatchConfig);
                    }
                }
               // return true;
            }

        } catch (Exception e) {
            log.error("appendPlaceOrder ===> 追加车型下单 异常", e);
        }
        return false;
    }

    /**
     * 预估
     */
    private Map<String, Object> estimate(OrderBase orderBase) throws Exception {
        Date date = new Date();
        CreateOrderParam orderParam = new CreateOrderParam();
        orderParam.setDepartTime(date);
        orderParam.setCompanyId(orderBase.getCompanyId());
        orderParam.setPassengerPhone(orderBase.getPassengerPhone());
        orderParam.setFlightNumber(orderBase.getFlightNumber());
        orderParam.setFlightArrivalAirportCode(orderBase.getArrivalAirportCode());
        orderParam.setFlightDepartTime(orderBase.getDepartTime());
        orderParam.setDepartLat(orderBase.getDepartLat());
        orderParam.setDepartLng(orderBase.getDepartLng());
        orderParam.setDestLat(orderBase.getDestLat());
        orderParam.setDestLng(orderBase.getDestLng());
        orderParam.setServiceType(orderBase.getServiceType());
        orderParam.setDepartCityCode(orderBase.getDepartCityCode());
        orderParam.setPassengerPhone(orderBase.getPassengerPhone());
        orderParam.setPickupLocation(orderBase.getPickupLocation());
        orderParam.setDestLocation(orderBase.getDestLocation());
        orderParam.setSceneId(orderBase.getSceneId());
        orderParam.setScenePublishId(orderBase.getScenePublishId());
        // poi
        orderParam.setDepartPoi(orderBase.getDepartPoi());
        orderParam.setDestPoi(orderBase.getDestPoi());
        Map<String, Object> estimate = coreOrderService.estimate(orderParam, null);
        estimate.put("date", date);
        return estimate;
    }

    /**
     * //极速派单调度 下单校验 企业条件
     *
     * @param companyId
     * @return
     */
    private boolean checkCompany(Long companyId) {
        try {
            RequestCacheDto cacheBase = new RequestCacheDto();
            cacheBase.setKey(KEY_CRITERIA_COMPANY);
            cacheBase.setKeyParameterValue(companyId.toString());
            cacheBase.setSqlParameterValue(companyId.toString());
            cacheBase.setItem("dispatchresponsesettings.enabled");
            BaseResponse baseResponse = hcglFeign.getCache(cacheBase);
            if (baseResponse.getCode() == 0) {
                JSONObject resultObject = new JSONObject(baseResponse.getData());
                if (resultObject.containsKey("dispatchresponsesettings.enabled")) {
                    JSONObject jsonObject = resultObject.getJSONObject("dispatchresponsesettings.enabled");
                    if (jsonObject.containsKey("value")) {
                        String isOpen = jsonObject.getStr("value");
                        if (StringUtils.equals(isOpen, "True")) {
                            log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, isOpen);
                            return true;
                        }
                        log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, isOpen);
                    } else {
                        log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, "value不存在");

                    }
                }
                if (resultObject.containsKey("value")) {
                    String isOpen = resultObject.getStr("value");
                    if (StringUtils.equals(isOpen, "True")) {
                        log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, isOpen);
                        return true;
                    }
                    log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, isOpen);
                } else {
                    log.info("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启 companyId:{},结果:{}", companyId, "value不存在");

                }
            } else {
                log.error("isOpen ===> 极速派单调度 下单校验 企业条件 调用缓存服务返回错误：{}", baseResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("isOpen ===> 极速派单调度 下单校验 企业条件 是否开启", e);
        }
        return false;
    }

    /**
     * //极速派单调度 下单校验 用户条件
     *
     * @param userId
     * @return
     */
    private boolean checkUser(Long userId) {
        try {
            RequestCacheDto cacheBase = new RequestCacheDto();
            cacheBase.setKey(KEY_CRITERIA_USER);
            cacheBase.setKeyParameterValue(userId.toString());
            BaseResponse baseResponse = hcglFeign.getCache(cacheBase);
            if (baseResponse.getCode() == 0) {
                JSONObject resultObject = new JSONObject(baseResponse.getData());
                if (resultObject.containsKey("dispatchFlag")) {
                    boolean isOpen = resultObject.getBool("dispatchFlag", false);
                    if (isOpen) {
                        log.info("isOpenByUserId ===> 判断用户极速派单调度 是否开启 userId:{},结果:{}", userId, isOpen);
                        return true;
                    }
                    log.info("isOpenByUserId ===> 判断用户极速派单调度 是否开启 userId:{},结果:{}", userId, isOpen);
                } else {
                    log.info("isOpenByUserId ===> 判断用户极速派单调度 是否开启 userId:{},结果:{}", userId, "dispatchFlag不存在");
                }
            } else {
                log.error("isOpenByUserId ===> 调用缓存服务返回错误：{}", baseResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("isOpenByUserId ===> 判断用户极速派单调度 是否开启", e);
        }
        return false;
    }


    @Override
    public boolean checkUserTemp(Long companyId, Long userId) {
        try {
            Object companyRuleObj = cacheutil.getCacheInfo(REDIS_KEY_DISPATCH_COMPANY, StrUtil.toString(companyId), null, null);
            if (ObjectUtil.isNotEmpty(companyRuleObj)) {
                DispatchCompanyLogicVo companyRule = JSONUtil.toBean(JSONUtil.toJsonStr(companyRuleObj), DispatchCompanyLogicVo.class);
                boolean hasKey = redisUtil.hasKey(StrUtil.format(REDIS_KEY_DISPATCH_COMPANY_USER, userId));
                if (NumberUtil.equals(companyRule.getLogicType(), 1)) {
                    // 排除用户
                    // 如果 缓存中有这个key, 则不保存这个配置
                    // 如果 缓存中没有这个key, 则保存这个配置
                    if (!hasKey) {
                        return true;
                    }else{
                        log.info("isOpenByUserId ===> 极速派车-用户画像-用户,排除维度,缓存不为空");
                    }
                } else if (NumberUtil.equals(companyRule.getLogicType(), 2)) {
                    // 包含用户
                    // 如果 缓存中有这个key, 则保存这个配置
                    // 如果 缓存中没有这个key, 则不保存这个配置
                    if (hasKey) {
                        return true;
                    }else{
                        log.info("isOpenByUserId ===> 极速派车-用户画像-用户,包含维度,缓存为空");
                    }
                }

            }else{
                log.info("isOpenByUserId ===> 极速派车-用户画像-企业维度,缓存为空");
            }
        } catch (Exception e) {
            log.error("isOpenByUserId ===> 判断用户极速派单调度 是否开启", e);
        }
        return false;
    }

    /**
     * //极速派单调度 下单校验 订单条件
     *
     * @return
     */
    public OrderDispatchConfigVo checkOrder(RequestPlaceOrderDto requestPlaceOrderDto, CreateOrderParam orderParam) {
        try {
            OrderDispatchConfigVo result = null;
            Long companyId = orderParam.getCompanyId();
            RequestCacheDto cacheBase = new RequestCacheDto();
            cacheBase.setKey(KEY_CRITERIA_ORDER);
            cacheBase.setKeyParameterValue(companyId.toString());
            cacheBase.setSqlParameterValue(companyId.toString());
            BaseResponse baseResponse = hcglFeign.getCache(cacheBase);
            if (baseResponse.getCode() == 0) {
                log.info("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置,缓存服务返回结果:[{}]", orderParam.getOrderId(),
                        JSONUtil.toJsonStr(baseResponse));
                JSONArray objects = new JSONArray(baseResponse.getData());
                if (null != objects && objects.size() > 0) {
                    for (int i = 0; i < objects.size(); i++) {
                        JSONObject object = objects.getJSONObject(i);
                        String cityCode = object.getStr("cityCode");
                        String mileage = object.getStr("mileage");
                        Long delayTime = object.getLong("delayTime",0L);
                        Long dispatchTime = object.getLong("dispatchTime");
                        String timeContext = object.getStr("timeContext");
                        String timeContextStart = timeContext.split("-")[0];
                        String timeContextEnd = timeContext.split("-")[1];
                        String mileageStart = mileage.split("-")[0];
                        String mileageEnd = mileage.split("-")[1];
                        BigDecimal dispatchFee = object.getBigDecimal("dispatchFee");

                        if (!StringUtils.equals(cityCode, orderParam.getDepartCityCode())) {
                            log.info("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置,配置项[{}],不符合原因:城市[订单:{},配置:{}]",
                                    orderParam.getOrderId(), object, orderParam.getDepartCityCode(), cityCode);
                            continue;
                        }

                        double v = Math.round(requestPlaceOrderDto.getCars().stream().filter(o -> null != o.getEstimateDistance()).mapToDouble(o -> {
                            return o.getEstimateDistance().doubleValue();
                        }).average().orElse(0) * 100.0) / 100.0f;
                        if (v < Double.parseDouble(mileageStart) || v > Double.parseDouble(mileageEnd)) {
                            log.info("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置,配置项[{}],不符合原因:公里数[订单:{},配置:{}]",
                                    orderParam.getOrderId(), object, v, mileage);
                            continue;
                        }

                        int targetHour = DateTime.now().getHours();
                        int targetMinute = DateTime.now().getMinutes();
                        int startHour = Integer.parseInt(timeContextStart.split(":")[0]);
                        int startMinute = Integer.parseInt(timeContextStart.split(":")[1]);
                        int endHour = Integer.parseInt(timeContextEnd.split(":")[0]);
                        int endMinute = Integer.parseInt(timeContextEnd.split(":")[1]);
                        LocalTime targetTime = LocalTime.of(targetHour, targetMinute);
                        LocalTime startTime = LocalTime.of(startHour, startMinute);
                        LocalTime endTime = LocalTime.of(endHour, endMinute);
                        if (targetTime.isAfter(startTime) && targetTime.isBefore(endTime)) {
                            // 目标时间在区间内
                        } else {
                            // 目标时间不在区间内
                            log.info("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置,配置项[{}],不符合原因:时间区间[订单:{},配置:{}]",
                                    orderParam.getOrderId(), object, targetHour + ":" + targetMinute, timeContext);
                            continue;
                        }

                        OrderDispatchConfigVo dispatchConfig = new OrderDispatchConfigVo();
                        dispatchConfig.setDispatchFeeAmount(dispatchFee);
                        dispatchConfig.setTimeContextStart(timeContext.split("-")[0]);
                        dispatchConfig.setTimeContextEnd(timeContext.split("-")[1]);
                        dispatchConfig.setPopupTime(dispatchTime);
                        //dispatchConfig.setDelayTime(delayTime);
                        dispatchConfig.setDelayTime(0L);
                        dispatchConfig.setMileageStart(Double.parseDouble(mileage.split("-")[0]));
                        dispatchConfig.setMileageEnd(Double.parseDouble(mileage.split("-")[1]));
                        dispatchConfig.setCityCode(cityCode);

                        Object o = redisUtil.get("dispatchConfig:outTime");
                        if (null != o) {
                            dispatchConfig.setOutTime(Long.parseLong((String) o));
                        } else {
                            dispatchConfig.setOutTime(5L);
                        }
                        result = dispatchConfig;
                    }
                    return result;
                }
            } else {
                log.info("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置失败,缓存服务返回结果:[{}]", orderParam.getOrderId(),
                        JSONUtil.toJsonStr(baseResponse));
            }
        } catch (Exception e) {
            log.error("订单号:{},需求:极速派单调度 下单校验 订单条件 获取下单匹配企业配置异常,异常原因:" + e, orderParam.getOrderId());
        }
        return null;
    }

}