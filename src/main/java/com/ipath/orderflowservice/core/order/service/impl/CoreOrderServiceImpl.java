package com.ipath.orderflowservice.core.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.order.service.CoreOrderService;
import com.ipath.orderflowservice.feignclient.ConfigurationFeign;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.CoreOrderDetail;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.CoreOrderConstant;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.SelectTakeOrderParam;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import com.ipath.orderflowservice.order.bean.param.ordercore.RePlaceOrderReq;
import com.ipath.orderflowservice.order.bean.vo.*;
import com.ipath.orderflowservice.order.dao.OrderSourceMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;
import com.ipath.orderflowservice.order.service.CacheService;
import com.ipath.orderflowservice.order.task.OrderTask;
import com.ipath.orderflowservice.order.util.CacheUtil;
import com.ipath.orderflowservice.order.util.DataUtil;
import com.ipath.orderflowservice.order.util.ResourceUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * core端订单服务 Service
 */
@Service
@Slf4j
public class CoreOrderServiceImpl implements CoreOrderService {

    /**
     * core端订单服务接口
     **/
    // 【通知取消后 重新下单接口】
    private static final String AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_PATH = "/api/v2/ordercore/rePlaceOrder";
    // 【通知取消后 取消下单接口】
    // private static final String AFTER_CORE_CANCEL_CLOSE_PATH =
    // "/api/v2/ordercore/closeOrder"; core端废弃该接口，使用cancelOrder
    private static final String AFTER_CORE_CANCEL_CLOSE_PATH = "/api/v2/ordercore/cancelOrder";

    /**
     * redis缓存key
     **/
    // redis缓存
    private static final String REDIS_KEY_AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_COUNT = "after_core_cancel_afresh_place_order_count_";

    private static final String REDIS_KEY_AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_COUNT_MAX = "after_core_cancel_afresh_place_order_count_max";

    @Autowired
    private RemoteCallFeign remoteCallFeign;
    @Autowired
    private ConfigurationFeign configurationFeign;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private OrderSourceMapper orderSourceMapper;

    @Autowired
    private ResourceUtil resourceUtil;

    @Autowired
    private LogService logService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private OrderTask orderTask;

    @Autowired
    private CacheUtil cacheUtil;

    @Value("${envConfig.receive.isGray}")
    public String receiveIsGray;

    /**
     * 下单
     *
     * @param req
     * @param orderParam
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse placeOrder(RequestPlaceOrderDto req, CreateOrderParam orderParam) throws Exception {
        BaseResponse response = null;
        Log activityLog = logService.getLog(req.getCompanyId(), req.getUserId(), Long.valueOf(req.getPartnerOrderId()),
                InterfaceEnum.CORE_ORDER_PLACEORDER);
        long startTime = System.currentTimeMillis();

        try {
            RequestPlaceOrderExtraParamsDto grayScaleExtraParamsDto = new RequestPlaceOrderExtraParamsDto();
            grayScaleExtraParamsDto.setParam(CoreOrderConstant.GRAY_SCALE);
            grayScaleExtraParamsDto.setValue(receiveIsGray);
            req.getExtraParams().add(grayScaleExtraParamsDto);

            RemoteCallDto remoteCallDto = new RemoteCallDto();
            // 中台暂不支持接站，此处将接站置为预约
            if (req.getServiceType() == 7) {
                req.setServiceType((short) 2);
            }

            // 根据动态码去重
            if (CollectionUtil.isNotEmpty(req.getCars())) {
                List<SelectedCar> cars = req.getCars();
                List<SelectedCar> newCars = new ArrayList<>(cars.stream()
                        .collect(Collectors.toMap(
                                SelectedCar::getDynamicCode, // 根据某个字段去重
                                car -> car, // 值为原对象
                                (existing, replacement) -> existing // 处理冲突，保留第一个出现的对象
                        ))
                        .values());
                req.setCars(newCars);
            }

            // 设置poi信息
            req.setDepartPoi(orderParam.getDepartPoi());
            req.setDestPoi(orderParam.getDestPoi());
            JSONObject paramObject = new JSONObject(req);
            paramObject.remove("orderParams"); // 仅ka端使用此参数
            remoteCallDto.setContent(paramObject.toString());
            remoteCallDto.setPath("/api/v2/ordercore/placeOrder");
            dataUtil.setRemoteCallDtoHeaders(remoteCallDto);

            activityLog.setBody(JSONUtil.toJsonStr(paramObject));

            response = remoteCallFeign.call(remoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

        } catch (Exception e) {
            // 日志(保存错误)
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }

        if (response.getCode() != 0) {
            var message = response.getMessage();
            if (StrUtil.isNotEmpty(message) && "预估结果已过期".equals(message)) {
                String resource = resourceUtil.getResource(orderParam.getUserId(), orderParam.getCompanyId(), 0,
                        "ipath_order_estimate_expire");
                if (StrUtil.isNotEmpty(resource)) {
                    message = resource;
                }

                logService.saveWarningLogAsync(activityLog, message);
                throw new BusinessException(message);
            } else {
                if (StrUtil.isNotBlank(response.getMessage())) {
                    logService.saveErrorLogAsync(activityLog, response.getMessage());
                } else {
                    logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
                }

                throw new BusinessException("下单失败，请重新预估");
            }
        }

        // 日志(保存正常)
        logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        return response;
    }

    /**
     * 下单
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse placeOrderRes(RequestPlaceOrderDto req) throws Exception {
        BaseResponse response = null;
        try {

            // 灰度标识 start add by huzhen 2024/09/24

            RequestPlaceOrderExtraParamsDto placeOrderExtraParamsDto = new RequestPlaceOrderExtraParamsDto();
            placeOrderExtraParamsDto.setParam(CoreOrderConstant.GRAY_SCALE);
            placeOrderExtraParamsDto.setValue(receiveIsGray);

            if (req.getExtraParams() == null) {
                List<RequestPlaceOrderExtraParamsDto> extraParams = new ArrayList<>();
                extraParams.add(placeOrderExtraParamsDto);
                req.setExtraParams(extraParams);
            } else {
                long count = req.getExtraParams().stream().filter(o -> {
                    if (StringUtils.equals(o.getParam(), CoreOrderConstant.GRAY_SCALE)) {
                        return false;
                    }
                    return false;
                }).count();
                if (count == 0) {
                    req.getExtraParams().add(placeOrderExtraParamsDto);
                }
            }

            // 灰度标识 end

            RemoteCallDto remoteCallDto = new RemoteCallDto();
            // 中台暂不支持接站，此处将接站置为预约
            if (req.getServiceType() == 7) {
                req.setServiceType((short) 2);
            }

            // 根据动态码去重
            if (CollectionUtil.isNotEmpty(req.getCars())) {
                List<SelectedCar> cars = req.getCars();
                List<SelectedCar> newCars = new ArrayList<>(cars.stream()
                        .collect(Collectors.toMap(
                                SelectedCar::getDynamicCode, // 根据某个字段去重
                                car -> car, // 值为原对象
                                (existing, replacement) -> existing // 处理冲突，保留第一个出现的对象
                        ))
                        .values());
                req.setCars(newCars);
            }

            JSONObject paramObject = new JSONObject(req);
            paramObject.remove("orderParams"); // 仅ka端使用此参数
            remoteCallDto.setContent(paramObject.toString());
            remoteCallDto.setPath("/api/v2/ordercore/placeOrder");
            dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception e) {
            log.info("placeOrderRes ===> 下单异常：{}", e.getMessage());
        }

        // 日志(保存正常)
        // logService.saveLogAsync(log, JSONUtil.toJsonStr(response), new Date());
        return response;
    }

    @Override
    public JSONObject estimate(RequestEstimateDto req, CreateOrderParam orderParam, String traceId,
            Boolean isAppendEstimate) throws Exception {
        BaseResponse response = null;
        Log activityLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                null == orderParam.getOrderId() ? InterfaceEnum.CORE_ORDER_ESTIMATE
                        : InterfaceEnum.CORE_ORDER_CHANGE_DEST_ESTIMATE,
                traceId);
        long startTime = System.currentTimeMillis();

        try {
            // 此次预估可用的平台
            List<Integer> availableCarSources = cacheService.getCarSource(orderParam.getCompanyId());
            if (null != availableCarSources && availableCarSources.size() > 0) {
                req.setIncludeCarSources(availableCarSources);
            }

            String path = null;
            if (StringUtil.isNullOrEmpty(req.getCoreOrderId())) {
                path = "/api/v2/ordercore/estimate";
            } else {
                path = "/api/v2/ordercore/changeDestEstimate";
            }

            // 中台暂不支持接站，此处将接站置为预约
            if (req.getServiceType() == 7) {
                req.setServiceType((short) 2);
            }

            req.setDepartPoi(orderParam.getDepartPoi());
            req.setDestPoi(orderParam.getDestPoi());
            req.setTraceId(traceId);

            RemoteCallDto remoteCallDto = new RemoteCallDto();
            JSONObject paramObject = new JSONObject(req);
            remoteCallDto.setPath(path);
            remoteCallDto.setContent(paramObject.toString());
            dataUtil.setRemoteCallDtoHeaders(remoteCallDto);

            activityLog.setBody(JSONUtil.toJsonStr(paramObject));
            response = remoteCallFeign.call(remoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
        } catch (Exception e) {
            // 日志(保存错误)
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }

        if (response.getCode() != 0) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (ObjectUtil.isNull(response.getMessage())) {
                logService.saveErrorLogAsync(activityLog, String.valueOf(response.getData()));
            } else {
                logService.saveErrorLogAsync(activityLog, response.getMessage());
                throw new BusinessException(response.getMessage());
            }
        }

        // 日志(保存正常)
        JSONObject result = new JSONObject(response.getData());

        String estimateId = result.getStr("estimateId");
        activityLog.setMappingId(estimateId);

        logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        return result;
    }

    /**
     * core端通知取消后 重新下单接口
     *
     * @param rePlaceOrderReq
     * @return
     * @throws Exception
     */
    @Override
    public Boolean afterCoreCancelAfreshPlaceOrder(RePlaceOrderReq rePlaceOrderReq, Long orderId) throws Exception {

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath(AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_PATH);
        remoteCallDto.setContent(JSONUtil.toJsonStr(rePlaceOrderReq));
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            log.error(
                    "afterCoreCancelClose======>【Feign】【服务：core端订单服务】【接口：重新下单接口】【单号：coreOrderId:{},orderId:{}】 【返回异常：{}】",
                    rePlaceOrderReq.getCoreOrderId(),
                    orderId,
                    response.getMessage());
            return false;
        }

        return true;
    }

    /**
     * core端通知取消后 取消下单接口
     *
     * @param coreOrderId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean afterCoreCancelClose(String coreOrderId, Long orderId) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("coreOrderId", coreOrderId);

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath(AFTER_CORE_CANCEL_CLOSE_PATH);
        remoteCallDto.setContent(JSONUtil.toJsonStr(jsonObject.toString()));
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            log.error(
                    "afterCoreCancelClose======>【Feign】【服务：core端订单服务】【接口：取消下单接口】【单号：coreOrderId:{},orderId:{}】 【返回异常：{}】",
                    coreOrderId,
                    orderId,
                    response.getMessage());
            return false;
        }
        return true;
    }

    /**
     * core端通知取消后 重新下单 次数校验是否通过接口
     * <p>
     * 说明：
     * 重复下单次数 十分钟内达到1次后终止重复下单，取消通知core端服务
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean afterCoreCancelAfreshPlaceOrderCheck(Long orderId) throws Exception {
        String key = REDIS_KEY_AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_COUNT + orderId;
        if (!redisUtil.hasKey(key)) {
            // 十分钟允许重复一次
            redisUtil.set(key, 1, 10 * 60L);
        } else {
            int count = Integer.valueOf(redisUtil.get(key).toString());
            if (count >= 1) {
                log.info("afterCoreCancelAfreshPlaceOrderCheck======>重新下单次数已经超出10分内限制次数:orderId:{}", orderId);
                return false;
            }
            redisUtil.set(key, count + 1, 10 * 60L);
        }
        return true;
    }

    /**
     * 重新派单最大次数
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean afterCoreCancelAfreshPlaceOrderCheckMax(Long orderId) throws Exception {
        String key = REDIS_KEY_AFTER_CORE_CANCEL_AFRESH_PLACE_ORDER_COUNT_MAX + orderId;
        if (!redisUtil.hasKey(key)) {
            // 两天内重派2次最多 （这里的需求是一单最多只能重派两次）
            redisUtil.set(key, 1, 2 * 24 * 60 * 60L);
        } else {
            int count = Integer.valueOf(redisUtil.get(key).toString());
            if (count >= 2) {
                log.info("afterCoreCancelAfreshPlaceOrderCheck======>重新下单次数已经超出最大限制次数:orderId:{}", orderId);
                redisUtil.delete(key);
                return false;
            }
            redisUtil.set(key, count + 1, 2 * 24 * 60 * 60L);
        }
        return true;
    }

    /**
     * core端 重新派单重新估价
     *
     * @param orderParam  下单参数
     * @param excludeCars 结果中排除订单中已选的车型
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> estimate(CreateOrderParam orderParam, List<SelectedCar> excludeCars) throws Exception {
        // 用sceneId从.net接口查询用户场景金额
        RequestSceneInfoDto requestSceneInfoDto = new RequestSceneInfoDto();
        BeanUtil.copyProperties(orderParam, requestSceneInfoDto, false);
        requestSceneInfoDto.setOrderTime(orderParam.getDepartTime());
        BaseResponse sceneResponse = configurationFeign.getSceneInfo(requestSceneInfoDto);
        if (sceneResponse.getCode() != 0) {
            throw new BusinessException("调用getSceneInfo接口失败:" + sceneResponse.getMessage());
        }

        JSONObject dataObject = JSONUtil.parseObj(sceneResponse.getData());
        SceneInfo sceneInfo = JSONUtil.toBean(dataObject, SceneInfo.class); // 无报销信息表示全公司承担，报销比例为0表示全个人承担
        redisUtil.set(CacheConsts.REDIS_KEY_SCENE_INFO_PREFIX + orderParam.getSceneId().toString(),
                sceneInfo.getScene(), CacheConsts.ORDER_CACHE_EXPIRE_TIME);
        redisUtil.set(CacheConsts.REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX + orderParam.getScenePublishId().toString(),
                sceneInfo.getReimModel(), CacheConsts.ORDER_CACHE_EXPIRE_TIME);
        // JSONObject jsonObject = new JSONObject();
        RequestEstimateDto requestEstimateDto = new RequestEstimateDto();

        // 如果传了订单id，说明订单已经创建，认为是修改目的地重新估价
        if (orderParam.getOrderId() != null) {
            Long userId = orderParam.getUserId();
            Long orderId = orderParam.getOrderId();
            String coreOrderId = null;

            CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(userId, orderId);
            if (cacheOrder != null) {
                coreOrderId = cacheOrder.getCoreOrderId();
            } else {
                OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
                coreOrderId = orderSource.getCoreOrderId();
            }
            requestEstimateDto.setCoreOrderId(coreOrderId);
        }

        if (StrUtil.isEmpty(orderParam.getPassengerPhone())) {
            orderParam.setPassengerPhone("13800138000");
        }

        BeanUtil.copyProperties(orderParam, requestEstimateDto);
        String path = null;

        // 此次预估可用的平台
        List<Integer> availableCarSources = cacheService.getCarSource(orderParam.getCompanyId());
        if (availableCarSources == null || availableCarSources.size() == 0) {
            throw new BusinessException("未配置用车平台");
        }
        requestEstimateDto.setIncludeCarSources(availableCarSources);

        if (StringUtil.isNullOrEmpty(requestEstimateDto.getCoreOrderId())) {
            path = "/api/v2/ordercore/estimate";
        } else {
            path = "/api/v2/ordercore/changeDestEstimate";
        }

        RemoteCallDto remoteCallDto = new RemoteCallDto();

        if (null == requestEstimateDto.getExtraParams()) {
            requestEstimateDto.setExtraParams(new ArrayList<>());
        }
        requestEstimateDto.setDestPoi(orderParam.getDestPoi());
        requestEstimateDto.setDepartPoi(orderParam.getDepartPoi());

        JSONObject paramObject = new JSONObject(requestEstimateDto);
        remoteCallDto.setPath(path);
        remoteCallDto.setContent(paramObject.toString());
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
        BaseResponse resp = remoteCallFeign.call(remoteCallDto);

        if (resp.getCode() != 0) {
            throw new BusinessException(resp.getMessage());
        }
        JSONObject result = new JSONObject(resp.getData());

        // 订单首次估价处理结果缓存起来
        String estimateId = result.getStr("estimateId");
        JSONArray rows = result.getJSONArray("rows");
        // 保存预估订单id,用于重新派单
        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_ORDERID_PREFIX + orderParam.getOrderId(),
                orderParam.getEstimateId(), 160L);

        Map<String, Object> map = new HashMap<>(2);
        map.put("estimateId", estimateId);
        map.put("rows", rows);
        return map;
    }

    /**
     * 根据coreOrderId获取 core端订单信息
     *
     * @param coreOrderId
     * @return
     */
    @Override
    public CoreOrderDetail getOrderDetail(String coreOrderId) {
        // 调用中台接口查询订单详情
        RequestOrderQueryDto requestOrderQueryDto = new RequestOrderQueryDto();
        requestOrderQueryDto.setCoreOrderId(coreOrderId);

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        JSONObject paramObject = new JSONObject(requestOrderQueryDto);
        remoteCallDto.setContent(paramObject.toString());
        remoteCallDto.setPath("/api/v2/ordercore/getOrderDetail");

        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);

        BaseResponse response = remoteCallFeign.call(remoteCallDto);

        if (response.getCode() != 0) {
            log.info("updateOrderDetail ==> call getOrderDetail api failed: " + response.getMessage());
            return null;
        }
        CoreOrderDetail coreOrderDetail = JSONUtil.toBean(JSONUtil.toJsonStr(response.getData()),
                CoreOrderDetail.class);
        return coreOrderDetail;
    }

    /**
     * 根据coreOrderId获取 core端订单信息
     *
     * @param coreOrderId
     * @return
     */
    @Override
    public JSONObject getOrderDetailJson(String coreOrderId) {
        // 调用中台接口查询订单详情
        RequestOrderQueryDto requestOrderQueryDto = new RequestOrderQueryDto();
        requestOrderQueryDto.setCoreOrderId(coreOrderId);
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        JSONObject paramObject = new JSONObject(requestOrderQueryDto);
        remoteCallDto.setContent(paramObject.toString());
        remoteCallDto.setPath("/api/v2/ordercore/getOrderDetail");
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
        BaseResponse response = remoteCallFeign.call(remoteCallDto);

        if (response.getCode() != 0) {
            log.info("updateOrderDetail ==> call getOrderDetail api failed: " + response.getMessage());
            return null;
        }
        // CoreOrderDetail coreOrderDetail =
        // JSONUtil.toBean(JSONUtil.toJsonStr(response.getData()),
        // CoreOrderDetail.class);
        JSONObject dataObject = JSONUtil.toBean(JSONUtil.toJsonStr(response.getData()), JSONObject.class);
        return dataObject;
    }

    @Override
    public BaseResponse changeDest(CreateOrderParam orderParam, String coreOrderId) {
        BaseResponse response = null;
        Log activityLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                InterfaceEnum.CORE_ORDER_CHANGEDEST);
        long startTime = System.currentTimeMillis();

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
            JSONObject paramObject = new JSONObject(orderParam);
            paramObject.remove("orderParams"); // 仅ka端使用此参数
            paramObject.set("coreOrderId", coreOrderId);
            remoteCallDto.setPath(
                    "/api/v2/ordercore/changeDest");
            remoteCallDto.setContent(paramObject.toString());

            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception e) {
            log.error("changeDest ===>修改目的地下单异常", e);
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw e;
        }

        if (null != response && response.getCode() == 0) {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        } else {
            logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }
        return response;
    }

    @Override
    public BaseResponse cancelOrder(Long companyId, Long userId, Long orderId, String coreOrderId, String reason,
            Short preState) {
        BaseResponse response = null;
        Log activityLog = logService.getLog(companyId, userId, orderId,
                InterfaceEnum.ORDER_CORE_CANCEL_ORDER);
        long startTime = System.currentTimeMillis();

        try {
            String path = "/api/v2/ordercore/cancelOrder";
            if (preState >= 2) {
                path = "/api/v2/ordercore/syncCancelOrder";
                activityLog.setInterfacePath("ordercore.syncCancelOrder");
            }
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            JSONObject paramObject = new JSONObject();
            paramObject.set("coreOrderId", coreOrderId);
            paramObject.set("reason", reason);
            remoteCallDto.setPath(path);
            remoteCallDto.setContent(paramObject.toString());
            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception e) {
            log.error("cancelOrder ===>取消订单异常", e);
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw e;
        }

        if (null != response && response.getCode() == 0) {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        } else {
            logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }
        return response;
    }
}
