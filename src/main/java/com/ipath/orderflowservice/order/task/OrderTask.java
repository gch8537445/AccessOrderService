package com.ipath.orderflowservice.order.task;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.booking.service.BookingService;
import com.ipath.orderflowservice.feignclient.*;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.NotifyReportType;
import com.ipath.orderflowservice.order.bean.param.*;
import com.ipath.orderflowservice.order.dao.*;
import com.ipath.orderflowservice.order.dao.bean.*;
import com.ipath.orderflowservice.order.dao.vo.OrderForReport;
import com.ipath.orderflowservice.order.producer.DelayProducer;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.order.bean.vo.LocationVo;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private UserOrderMapper userOrderMapper;

    @Autowired
    private CompanyCallbackConfigMapper companyCallbackConfigMapper;
    @Autowired
    private RemoteCallFeign remoteCallFeign;
    @Autowired
    private SnowFlakeUtil snowFlakeUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private LogService logService;
    @Autowired
    private DelayProducer delayProducer;
    @Value("${rocketmq.topic.order-info}")
    private String TOPIC_ORDER_INFO;
    @Value("${rocketmq.topic.socket-order-info}")
    private String TOPIC_SOCKET_ORDER_INFO;
    @Value("${rocketmq.topic.order-notify}")
    private String TOPIC_ORDER_NOTIFY;
    @Value("${rocketmq.topic.cds-notify}")
    private String TOPIC_CDS_NOTIFY;
    @Value("${rocketmq.topic.workflow-notify}")
    private String TOPIC_WORKFLOW_NOTIFY;
    @Value("${rocketmq.tags.order-notify}")
    private String TAG_ORDER_NOTIFY;
    @Value("${rocketmq.tags.order-reject-pay}")
    private String TAG_ORDER_REJECT_PAY;
    @Value("${rocketmq.tags.order-notify-cnpc}")
    private String TAG_ORDER_NOTIFY_CNPC;
    @Value("${rocketmq.tags.socket-order-info}")
    private String TAG_SOCKET_ORDER_INFO;
    @Value("${rocketmq.tags.detail-result}")
    private String TAG_DETAIL_RESULT;
    @Value("${rocketmq.tags.coupon-consume-notify}")
    private String TAG_COUPON_CONSUME_NOTIFY;
    @Value("${rocketmq.tags.extra-notify}")
    private String TAG_EXTRA_NOTIFY;
    @Value("${rocketmq.tags.bill-notify}")
    private String TAG_BILL_NOTIFY;
    @Value("${rocketmq.tags.order-notify-third}")
    private String TAG_ORDER_NOTIFY_THIRD;
    @Value("${rocketmq.tags.order-notify-h5}")
    private String TAG_ORDER_NOTIFY_STANDARD_H5;
    @Value("${rocketmq.tags.report-order}")
    private String TAG_REPORT_ORDER;
    @Value("${rocketmq.tags.report-approval}")
    private String TAG_REPORT_APPROVAL;
    @Value("${rocketmq.tags.process-notify}")
    private String TAG_PROCESS_NOTIFY;
    @Value("${rocketmq.tags.complaint-notify}")
    private String TAG_Complaint_NOTIFY;
    @Value("${rocketmq.tags.report-bill}")
    private String TAG_REPORT_BILL;
    @Value("${rocketmq.tags.report-appendplaceorder}")
    private String TAG_REPORT_APPENDPLACEORDER;
    @Value("${rocketmq.tags.after-approval-notify}")
    private String TAG_AFTER_APPROVAL_NOTIFY;
    @Value("${rocketmq.tags.pay-refund-notify}")
    private String TAG_PAY_REFUND_NOTIFY;
    @Value("${rocketmq.tags.workflow-order}")
    private String TAG_WORKFLOW_NOTIFY;

    @Value("${rocketmq.cancel.topic}")
    private String TOPIC_ORDER_CANCEL_NOTIFY;
    @Value("${rocketmq.cancel.tag}")
    private String TAG_ORDER_CANCEL;

    @Value("${rocketmq.append.topic}")
    private String TOPIC_ORDER_APPEND_NOTIFY;
    @Value("${rocketmq.append.tag}")
    private String TAG_ORDER_APPEND;

    @Value("${rocketmq.changeDest.topic}")
    private String TOPIC_ORDER_CHANGE_DEST_NOTIFY;
    @Value("${rocketmq.changeDest.tag}")
    private String TAG_ORDER_CHANGE_DEST;

    @Value("${rocketmq.paid.topic}")
    private String TOPIC_ORDER_PAID_NOTIFY;
    @Value("${rocketmq.paid.tag}")
    private String TAG_ORDER_PAID;

    @Value("${rocketmq.complaint.topic}")
    private String TOPIC_ORDER_COMPLAINT_NOTIFY;
    @Value("${rocketmq.complaint.tag}")
    private String TAG_ORDER_COMPLAINT;

    @Value("${rocketmq.completion.topic}")
    private String TOPIC_ORDER_COMPLETION_NOTIFY;
    @Value("${rocketmq.completion.tag}")
    private String TAG_COMPLETION_PAID;

    @Value("${grayTestSwitch.settle}")
    private boolean settleGrayTestSwitch;// 结算灰度测试开关，当灰度测试开关开启后，检查缓存中配置的灰度测试企业

    @Value("${rocketmq.log.topic}")
    private String TOPIC_LOG_NOTIFY;
    @Value("${rocketmq.log.tag}")
    private String TAG_LOG_INFO;

    @Value("${rocketmq.orderStatusChg.topic}")
    private String TOPIC_ORDER_STATUS_NOTIFY;
    @Value("${rocketmq.orderStatusChg.tag}")
    private String TAG_ORDER_STATUS_INFO;

    @Value("${spring.profiles.active}")
    private String active;

    private List<String> kaList = List.of("test", "prod");

    @Async
    public void asyncPostProcessPlaceOrder(CreateOrderParam orderParam, BigDecimal frozenAmount,
            OrderSource orderSource) throws Exception {
        // 如果使用预约管家，通知预约服务
        if (orderParam.getExtraServices() != null && orderParam.getExtraServices().size() > 0) {
            int businessType = 0;
            for (ExtraService extraService : orderParam.getExtraServices()) {
                if ("ES0005".equals(extraService.getCode())) {
                    businessType = 1;
                    break;
                } else if ("ES0006".equals(extraService.getCode())) {
                    businessType = 2;
                    break;
                }
            }

            if (businessType > 0) {
                bookingService.startService(orderParam, orderSource.getOrderId(), orderSource.getCoreOrderId(),
                        orderSource.getEstimateDistance() == null ? 0 : orderSource.getEstimateDistance(),
                        businessType);
            }
        }

        saveHistoryLocation(orderParam);
        // 保存订单最近场景和用车参数，每个用户只保存最近一条记录
        saveRecentOrder(orderParam);

        // 通知.net服务
        RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();
        BeanUtil.copyProperties(orderParam, requestOrderNotifyDto, false);
        requestOrderNotifyDto.setOrderId(orderSource.getOrderId());
        requestOrderNotifyDto.setEstimatePrice(orderSource.getEstimatePrice());
        requestOrderNotifyDto.setCoreOrderId(orderSource.getCoreOrderId());
        requestOrderNotifyDto.setFrozenAmount(orderSource.getEstimatePrice());
        requestOrderNotifyDto.setOrderSourceId(orderSource.getId());
        requestOrderNotifyDto.setEventType((short) 1); // 1 下单
        notifyConfigureService(requestOrderNotifyDto);

        // 由于优惠券在预估时匹配，所以下单时可能存在多个优惠券的情况
        List<Long> couponIds = new ArrayList<>();
        for (int i = 0; i < orderParam.getCars().size(); i++) {
            if (orderParam.getCars().get(i).getCouponId() != null &&
                    orderParam.getCars().get(i).getCouponId() > 0L &&
                    !couponIds.contains(orderParam.getCars().get(i).getCouponId())) {
                couponIds.add(orderParam.getCars().get(i).getCouponId());
            }
        }
        if (couponIds.size() > 0) {
            requestOrderNotifyDto.setCouponIds(couponIds);
        }
        // 由于bill和coupon可能会相应时间长了会影响用户主动取消状态，调整先通知报表服务
        // notifyReportService(requestOrderNotifyDto, null, orderParam);
        notifyBillService(requestOrderNotifyDto);
        notifyCouponService(requestOrderNotifyDto);

        sendOrderStatusChangedNotifyMsg(orderParam.getOrderId(), (short) 1, orderParam.getCompanyId());
    }

    private void saveHistoryLocation(CreateOrderParam orderParam) throws Exception {
        // 起止点添加到历史记录中
        LocationVo location = new LocationVo();
        location.setLocation(orderParam.getPickupLocation());
        location.setLocationName(orderParam.getPickupLocationName());
        location.setCityCode(orderParam.getDepartCityCode());
        // location.setCityName(orderParam.getDepartCityName());
        location.setLat(orderParam.getDepartLat());
        location.setLng(orderParam.getDepartLng());
        location.setPoi(orderParam.getDepartPoi());
        this.addHistoryLocation(String.valueOf(orderParam.getUserId()), location);

        location = new LocationVo();
        location.setLocation(orderParam.getDestLocation());
        location.setLocationName(orderParam.getDestLocationName());
        location.setCityCode(orderParam.getDestCityCode());
        // address.setCityName(orderParam.getFromCityName());
        location.setLat(orderParam.getDestLat());
        location.setLng(orderParam.getDestLng());
        location.setPoi(orderParam.getDestPoi());
        this.addHistoryLocation(String.valueOf(orderParam.getUserId()), location);
    }

    /**
     * 添加或更新用户最近订单用车和场景信息
     */
    private void saveRecentOrder(CreateOrderParam orderParam) throws Exception {

        Boolean isInsertRecord = false;
        UserOrder userOrder = userOrderMapper.selectByUserId(orderParam.getUserId());
        // 首次下单
        if (userOrder == null) {
            isInsertRecord = true;
            userOrder = new UserOrder();
            userOrder.setId(snowFlakeUtil.getNextId());
            userOrder.setUserId(orderParam.getUserId());

            // 保存用户手机号
            RequestSetUserMobileDto requestSetUserMobileDto = new RequestSetUserMobileDto();
            requestSetUserMobileDto.setUserId(orderParam.getUserId());
            requestSetUserMobileDto.setMobile(orderParam.getPassengerPhone());
            // userFeign.setUserMobileIfAbsense(requestSetUserMobileDto);
        }

        userOrder.setRecentOrderId(orderParam.getOrderId());
        JSONObject recentOrderParam = new JSONObject();
        recentOrderParam.set("cars", orderParam.getCars());

        Map<String, Object> sceneMap = new HashMap<>();
        sceneMap.put("id", orderParam.getSceneId());
        sceneMap.put("publishId", orderParam.getScenePublishId());
        sceneMap.put("nameCn", orderParam.getSceneNameCn());
        sceneMap.put("nameEn", orderParam.getSceneNameEn());
        recentOrderParam.set("scene", sceneMap);

        userOrder.setRecentOrderParam(recentOrderParam);
        userOrder.setUpdateTime(new Date());
        if (isInsertRecord) {
            userOrderMapper.insertSelective(userOrder);
        } else {
            userOrderMapper.updateByPrimaryKeySelective(userOrder);
        }
    }

    /**
     * 通知configure服务
     */
    public void notifyConfigureServiceForReject(RequestOrderPayEventDto payEventDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(payEventDto);
        // log.info("notifyConfigureService.pay ==> msg={}", msgStr);
        String key = payEventDto.getOrderId().toString() + "_" + payEventDto.getIsReject().toString();

        Log iLog = logService.getLog(payEventDto.getCompanyId(), payEventDto.getUserId(),
                payEventDto.getOrderId(), InterfaceEnum.ORDER_NOTIFY_CONFIGURATION_MQ);
        iLog.setInterfaceName(iLog.getInterfaceName() + ":" + "审批拒绝后支付");
        iLog.setBody(msgStr);

        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
        String destination = TOPIC_ORDER_NOTIFY + ":" + TAG_ORDER_REJECT_PAY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
                logService.saveLogAsync(iLog,
                        StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_ORDER_REJECT_PAY));
            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
                logService.saveErrorLogAsync(iLog,
                        StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_ORDER_REJECT_PAY));
            }
        });
    }

    // 通知cds-mst服务
    public void notifyCdsMstServiceForReject(RequestOrderPayEventDto payEventDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(payEventDto);
        // log.info("notifyCdsMstServiceForReject.pay ==> msg={}", msgStr);
        String key = payEventDto.getOrderId().toString() + "_notifyCdsMstServiceForReject";

        Log iLog = logService.getLog(payEventDto.getCompanyId(), payEventDto.getUserId(),
                payEventDto.getOrderId(), InterfaceEnum.ORDER_NOTIFY_CDS_STATUS_CHG_MQ);
        iLog.setInterfaceName(iLog.getInterfaceName() + ":审批拒绝后支付");
        iLog.setBody(msgStr);

        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();

        String destination = TOPIC_CDS_NOTIFY + ":" + TAG_ORDER_REJECT_PAY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {

            }

            public void onException(Throwable throwable) {
            }
        });
    }

    /**
     * 通知configure服务
     */
    public void notifyConfigureService(RequestOrderNotifyDto requestOrderNotifyDto) throws Exception {
        // 不使用hutool的jsonObject，否则日期和long需要单独处理
        // 使用jackson的序列化，使用common中的jackson配置，日期格式化和long格式化成字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(requestOrderNotifyDto);
        String key = requestOrderNotifyDto.getOrderId().toString() + "_"
                + requestOrderNotifyDto.getEventType().toString();
        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();

        Log iLog = logService.getLog(requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
                requestOrderNotifyDto.getOrderId(), InterfaceEnum.ORDER_NOTIFY_CONFIGURATION_MQ);
        iLog.setInterfaceName(iLog.getInterfaceName() + ":" + requestOrderNotifyDto.getEventType());
        iLog.setBody(msgStr);

        String destination = TOPIC_ORDER_NOTIFY + ":" + TAG_ORDER_NOTIFY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
                logService.saveLogAsync(iLog, StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_ORDER_NOTIFY));
            }

            public void onException(Throwable throwable) {
                logService.saveErrorLogAsync(iLog,
                        StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_ORDER_NOTIFY));
            }
        });
    }

    /**
     * 通知coupon-service（跨域）
     * 
     * @param requestOrderNotifyDto
     * @throws Exception
     */
    public void notifyCouponService(RequestOrderNotifyDto requestOrderNotifyDto) throws Exception {
        // 不使用hutool的jsonObject，否则日期和long需要单独处理
        // 使用jackson的序列化，使用common中的jackson配置，日期格式化和long格式化成字符串

        RequestCouponNotifyDto requestCouponNotifyDto = new RequestCouponNotifyDto();
        BeanUtils.copyProperties(requestOrderNotifyDto, requestCouponNotifyDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(requestCouponNotifyDto);
        // log.info("notifyCouponService ==> msg={}", msgStr);
        String key = requestOrderNotifyDto.getOrderId().toString() + "_"
                + requestOrderNotifyDto.getEventType().toString();

        RequestPublishMessageDto couponMessage = new RequestPublishMessageDto();
        couponMessage.setMessageId("notifyCouponService:" + snowFlakeUtil.getNextId());
        couponMessage.setCompanyId(requestOrderNotifyDto.getCompanyId());
        couponMessage.setTopic(TOPIC_ORDER_NOTIFY);
        couponMessage.setTag(TAG_COUPON_CONSUME_NOTIFY);
        couponMessage.setKey(key);
        couponMessage.setMessageBody(msgStr);
        JSONObject paramObject = new JSONObject(couponMessage);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
                requestOrderNotifyDto.getOrderId(),
                InterfaceEnum.ORDER_BRIDGE_PUBLISH_MESSAGE);
        activityLog.setInterfacePath(activityLog.getInterfacePath() + ".couponservice");
        activityLog.setInterfaceName(activityLog.getInterfaceName() + "_优惠券服务:" + requestOrderNotifyDto.getEventType());
        activityLog.setBody(JSONUtil.toJsonStr(paramObject));

        BaseResponse response = null;
        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/bridge/publishMessage");
            remoteCallDto.setContent(paramObject.toString());
            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("通知优惠券服务出现异常【OrderTask.notifyCouponService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), ex);
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        if (response.getCode() != 0) {
            logService.saveErrorLogAsync(activityLog, new Exception(response.getMessage()));
            log.error("通知优惠券服务出现异常【OrderTask.notifyCouponService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), new Exception(JSONUtil.toJsonStr(response)));
        } else {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }

        Log mqLog = logService.getLog(requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
                requestOrderNotifyDto.getOrderId(), InterfaceEnum.ORDER_NOTIFY_COUPON_MQ);
        mqLog.setInterfaceName(mqLog.getInterfaceName() + ":" + requestOrderNotifyDto.getEventType());
        mqLog.setBody(msgStr);

        String destination = TOPIC_ORDER_NOTIFY + ":" + TAG_COUPON_CONSUME_NOTIFY;

        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
                logService.saveLogAsync(mqLog,
                        StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_COUPON_CONSUME_NOTIFY));
            }

            public void onException(Throwable throwable) {
                logService.saveErrorLogAsync(mqLog,
                        StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_NOTIFY, TAG_COUPON_CONSUME_NOTIFY));
            }
        });
    }

    /**
     * 通知extral-service（跨域）
     * 
     * @param requestOrderNotifyDto
     * @throws Exception
     */
    public void notifyExtralService(RequestOrderNotifyDto requestOrderNotifyDto) throws Exception {
        // 不使用hutool的jsonObject，否则日期和long需要单独处理
        // 使用jackson的序列化，使用common中的jackson配置，日期格式化和long格式化成字符串

        RequestExtralNotifyDto requestExtralNotifyDto = new RequestExtralNotifyDto();
        BeanUtils.copyProperties(requestOrderNotifyDto, requestExtralNotifyDto);
        requestExtralNotifyDto.setUserName(requestExtralNotifyDto.getPassengerName());

        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(requestExtralNotifyDto);
        // log.info("notifyExtralService ==> msg={}", msgStr);
        String key = requestOrderNotifyDto.getOrderId().toString() + "_"
                + requestOrderNotifyDto.getEventType().toString();

        RequestPublishMessageDto exralMessage = new RequestPublishMessageDto();
        exralMessage.setMessageId("notifyExtralService:" + snowFlakeUtil.getNextId());
        exralMessage.setCompanyId(requestOrderNotifyDto.getCompanyId());
        exralMessage.setTopic(TOPIC_ORDER_NOTIFY);
        exralMessage.setTag(TAG_EXTRA_NOTIFY);
        exralMessage.setKey(key);
        exralMessage.setMessageBody(msgStr);
        JSONObject paramObject = new JSONObject(exralMessage);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
                requestOrderNotifyDto.getOrderId(),
                InterfaceEnum.ORDER_BRIDGE_PUBLISH_MESSAGE);
        activityLog.setInterfacePath(activityLog.getInterfacePath() + ".extralservice");
        activityLog.setInterfaceName(activityLog.getInterfaceName() + "_增值服务:" + requestOrderNotifyDto.getEventType());
        activityLog.setBody(JSONUtil.toJsonStr(paramObject));

        BaseResponse response = null;
        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/bridge/publishMessage");
            remoteCallDto.setContent(paramObject.toString());
            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("通知增值服务出现异常【OrderTask.notifyExtralService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), ex);
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        if (response.getCode() != 0) {
            logService.saveErrorLogAsync(activityLog, new Exception(response.getMessage()));
            log.error("通知增值服务出现异常【OrderTask.notifyExtralService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), new Exception(response.getMessage()));
        } else {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }
    }

    /**
     * 通知bill-service（跨域）
     * 
     * @param requestOrderNotifyDto
     * @throws Exception
     */
    public void notifyBillService(RequestOrderNotifyDto requestOrderNotifyDto) throws Exception {
        // 不使用hutool的jsonObject，否则日期和long需要单独处理
        // 使用jackson的序列化，使用common中的jackson配置，日期格式化和long格式化成字符串
        log.info("notifyBillService ==> {}", JSONUtil.toJsonStr(requestOrderNotifyDto));
        RequestBillNotifyBaseDto requestBillNotifyDto = null;
        switch (requestOrderNotifyDto.getEventType()) {
            case -100:// 下单失败
                requestBillNotifyDto = new RequestPrepayOrderExceptionBillNotifyDto();
                break;
            case 1:
                requestBillNotifyDto = new RequestBillNotifyPlaceOrderDto();
                break;
            case 2:
                requestBillNotifyDto = new RequestBillNotifyTakeOrderDto();
                break;
            case 5:
            case 6:
                requestBillNotifyDto = new RequestSettleOrderBillNotifyDto();
                break;
            case 7:
                requestBillNotifyDto = new RequestBillNotifyCancelOrderDto();
                break;
            case 100:
                requestBillNotifyDto = new RequestBillNotifyAppendOrderDto();
                break;
            default:
                requestBillNotifyDto = new RequestBillNotifyBaseDto();
        }

        BeanUtils.copyProperties(requestOrderNotifyDto, requestBillNotifyDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(requestBillNotifyDto);
        // log.info("notifyBillService ==> msg={}", msgStr);
        String key = requestOrderNotifyDto.getOrderId().toString() + "_"
                + requestOrderNotifyDto.getEventType().toString();

        RequestPublishMessageDto billMessage = new RequestPublishMessageDto();
        billMessage.setMessageId("notifyBillService:" + snowFlakeUtil.getNextId());
        billMessage.setCompanyId(requestOrderNotifyDto.getCompanyId());
        billMessage.setTopic(TOPIC_ORDER_NOTIFY);
        billMessage.setTag(TAG_BILL_NOTIFY);
        billMessage.setKey(key);
        billMessage.setMessageBody(msgStr);
        JSONObject paramObject = new JSONObject(billMessage);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
                requestOrderNotifyDto.getOrderId(),
                InterfaceEnum.ORDER_BRIDGE_PUBLISH_MESSAGE);
        activityLog.setInterfacePath(activityLog.getInterfacePath() + ".billservice");
        activityLog.setInterfaceName(activityLog.getInterfaceName() + "_账单服务:" + requestOrderNotifyDto.getEventType());
        activityLog.setBody(JSONUtil.toJsonStr(billMessage));

        BaseResponse response = null;
        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/bridge/publishMessage");
            remoteCallDto.setContent(paramObject.toString());
            response = remoteCallFeign.call(remoteCallDto);
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);

            log.error("通知账单服务出现异常【OrderTask.notifyBillService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), ex);
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        if (response.getCode() != 0) {
            logService.saveErrorLogAsync(activityLog, response.getMessage());
            log.error("通知账单服务出现异常【OrderTask.notifyBillService】，companyId：{}，userId：{}，orderId：{}", requestOrderNotifyDto.getCompanyId(), requestOrderNotifyDto.getUserId(),
            requestOrderNotifyDto.getOrderId(), new Exception(response.getMessage()));
        } else {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }
    }

    public void notifyWebsocketService(String msg, String key, Long companyId, Boolean isGray)
            throws Exception {
        // log.info("notifyWebsocketService ==> send msg=" + msg);
        String topic = TOPIC_SOCKET_ORDER_INFO;
        String tag = TAG_SOCKET_ORDER_INFO;
        if (BooleanUtil.isTrue(isGray) && kaList.contains(active)) {
            topic = topic + "-gray";
        }
        String[] keyArray = key.split("_");
        Long orderId = null;
        int status = 0;
        if (keyArray.length == 2) {
            orderId = Long.valueOf(keyArray[0]);
            status = Integer.valueOf(keyArray[1]);
        }
        RequestPublishMessageDto publishMessageDto = new RequestPublishMessageDto();
        publishMessageDto.setMessageId("notifyWebsocketService:" + snowFlakeUtil.getNextId());
        publishMessageDto.setCompanyId(companyId);
        publishMessageDto.setTopic(topic);
        publishMessageDto.setTag(tag);
        publishMessageDto.setKey(key);
        publishMessageDto.setMessageBody(msg);
        JSONObject paramObject = new JSONObject(publishMessageDto);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(companyId, null, orderId, InterfaceEnum.ORDER_BRIDGE_PUBLISH_MESSAGE);
        activityLog.setInterfacePath(activityLog.getInterfacePath() + ".websocketService");
        activityLog.setInterfaceName(activityLog.getInterfaceName() + "_websocket:" + status);
        activityLog.setBody(JSONUtil.toJsonStr(publishMessageDto));

        BaseResponse response = null;
        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/bridge/publishMessage");
            remoteCallDto.setContent(paramObject.toString());
            response = remoteCallFeign.call(remoteCallDto);
            if (response.getCode() != 0) {
                log.info(response.getMessage());
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);

            log.error("创建包车订单通知core报表服务遇到异常", ex);
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        if (response.getCode() != 0) {
            logService.saveErrorLogAsync(activityLog, response.getMessage());
        } else {
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
        }
    }

    /**
     * 行后审批通知
     * 审批外放
     */
    public void sendAfterApprovalNotifyMsg(Long workflowId, Long orderId, Long userId, Long sceneId, String sceneName,
            Long companyId, BigDecimal companyBearAmount, String useCarReason, Boolean isResubmit) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("workflowId", workflowId);
        jsonObject.set("orderId", orderId);
        jsonObject.set("userId", userId);
        jsonObject.set("sceneId", sceneId);
        jsonObject.set("sceneName", sceneName);
        jsonObject.set("companyId", companyId);
        jsonObject.set("useCarReason", useCarReason);
        jsonObject.set("companyBearAmount", companyBearAmount);
        jsonObject.set("isResubmit", isResubmit);
        String msg = jsonObject.toString();
        String key = orderId + "_afterApproval";
        log.info("sendAfterApprovalNotifyMsg ==> msg={}", msg);
        Message message = MessageBuilder.withPayload(msg).setHeader("KEYS", key).build();
        String destination = TOPIC_CDS_NOTIFY + ":" + TAG_AFTER_APPROVAL_NOTIFY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
            }
        }, 3000L, 3); // delay level 3: 10s
    }

    /**
     * 订单状态变更通知
     */
    public void sendOrderStatusChangedNotifyMsg(Long orderId, Short orderState, Long companyId) {
        String key = orderId + "_" + orderState;
        log.info("sendOrderStatusChangedNotifyMsg ==>callback order msg start. companyId={},key:{}", companyId, key);

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("orderId", orderId);
        jsonObject.set("status", orderState);
        jsonObject.set("companyId", companyId);
        String msg = jsonObject.toString();
        Message message = MessageBuilder.withPayload(msg).setHeader("KEYS", key).build();

        CompanyCallbackConfig topic = this.getCompanyCallbackConfig2(companyId, "order");
        String destination = null;
        if (null == topic) {
            return;
        } else {
            destination = TOPIC_CDS_NOTIFY + ":" + TAG_ORDER_NOTIFY_STANDARD_H5;

        }
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
            }
        }, 3000L, 2); // delay level 2: 5s 等待主从数据库同步
    }

    /**
     * 获取订单回传配置
     * 
     * @param companyId
     * @param callbackType
     * @return
     *         kakarotto 订单回传配置 common 获取订单回传配置
     */
    public CompanyCallbackConfig getCompanyCallbackConfig2(Long companyId, String callbackType) {
        String cacheCallbackConfigKey = CacheConsts.REDIS_KEY_CALLBACK_CONFIG_PREFIX + companyId.toString();
        List<CompanyCallbackConfig> callbackConfigs = new ArrayList<>();
        CompanyCallbackConfig callbackConfig = null;
        if (redisUtil.hasKey(cacheCallbackConfigKey)) {
            JSONArray jsonArray = new JSONArray(redisUtil.get(cacheCallbackConfigKey));
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CompanyCallbackConfig companyCallbackConfig = new CompanyCallbackConfig();
                    companyCallbackConfig.setCompanyId(jsonObject.getLong("companyId"));
                    companyCallbackConfig.setCallbackPath(jsonObject.getStr("callbackPath"));
                    companyCallbackConfig.setCompanyCode(jsonObject.getStr("companyCode"));
                    companyCallbackConfig.setCallbackType(jsonObject.getStr("callbackType"));
                    companyCallbackConfig.setId(jsonObject.getLong("id"));
                    companyCallbackConfig.setAdditionalValue(jsonObject.getStr("additionalValue"));
                    companyCallbackConfig.setAppId(jsonObject.getStr("appId"));
                    companyCallbackConfig.setNeedBack(jsonObject.getBool("needBack"));
                    companyCallbackConfig.setServiceName(jsonObject.getStr("serviceName"));
                    callbackConfigs.add(companyCallbackConfig);
                }
            }
            log.info("===> redis缓存key:【{}】,存在,值:{}", cacheCallbackConfigKey, jsonArray);
        } else {
            log.info("===> redis缓存key:【{}】,不存在", cacheCallbackConfigKey);
            callbackConfigs = companyCallbackConfigMapper.selectCompanyCallbackConfigMappingByCompanyId(companyId);
            if (callbackConfigs != null && callbackConfigs.size() > 0) {
                redisUtil.set(cacheCallbackConfigKey, JSONUtil.toJsonStr(callbackConfigs),
                        CacheConsts.STABLE_CACHE_EXPIRE_TIME);
            } else {
                redisUtil.set(cacheCallbackConfigKey, JSONUtil.toJsonStr(callbackConfigs),
                        CacheConsts.TEMP_CACHE_EXPIRE_TIME);
            }
            log.info("===>  redis缓存key:【{}】重新加载数据：{}", cacheCallbackConfigKey, JSONUtil.toJsonStr(callbackConfigs));
        }

        if (callbackConfigs != null && callbackConfigs.size() > 0) {
            callbackConfig = callbackConfigs.stream()
                    .filter(entry -> entry.getNeedBack() == true && entry.getCallbackType().equals(callbackType))
                    .findFirst().orElse(null);
        }

        return callbackConfig;
    }

    /**
     * 退款通知
     */
    public void sendRefundNotifyMsg(OrderPayRefund orderPayRefund) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("orderId", orderPayRefund.getOrderId());
        jsonObject.set("companyId", orderPayRefund.getCompanyId());
        jsonObject.set("payRefundAmount", orderPayRefund.getPayRefundAmount());
        String msg = jsonObject.toString();
        String key = orderPayRefund.getOrderId() + "_payRefund";
        log.info("sendRefundNotifyMsg ==> msg={}", msg);
        Message message = MessageBuilder.withPayload(msg).setHeader("KEYS", key).build();

        String destination = TOPIC_CDS_NOTIFY + ":" + TAG_PAY_REFUND_NOTIFY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {

            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
            }
        }, 3000L, 3); // delay level 3: 10s
    }

    /**
     * 发送流程消息
     */
    public void sendWorkflowApplyMsg(WorkflowParam workflowParam) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("businessType", workflowParam.getBusinessType());
        jsonObject.set("businessId", workflowParam.getBusinessId());
        jsonObject.set("companyId", workflowParam.getCompanyId());
        jsonObject.set("workflowId", workflowParam.getWorkflowId());
        jsonObject.set("sceneId", workflowParam.getSceneId());
        jsonObject.set("userId", workflowParam.getUserId());
        jsonObject.set("action", workflowParam.getAction());
        jsonObject.set("resubmit", workflowParam.getResubmit());
        jsonObject.set("comment", workflowParam.getComment());
        jsonObject.set("customInfo", workflowParam.getCustomInfo());
        jsonObject.set("amount", workflowParam.getAmount());
        String msg = jsonObject.toString();
        String key = workflowParam.getBusinessId() + "_" + workflowParam.getAction();
        this.sendWorkflowApplyMsgAsyncSend(workflowParam, msg, key);
    }

    private void sendWorkflowApplyMsgAsyncSend(WorkflowParam workflowParam, String msg, String key) {
        log.info("sendWorkflowApplyMsg ==> msg={}", msg);
        Message message = MessageBuilder.withPayload(msg).setHeader("KEYS", key).build();
        String destination = TOPIC_WORKFLOW_NOTIFY + ":" + TAG_WORKFLOW_NOTIFY;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
            }
        }, 3000L, workflowParam.getAction() == 1 ? 1 : 0);
    }

    // 获取订单回传配置
    public CompanyCallbackConfig getCompanyCallbackConfig(Long companyId, String callbackType) {
        String cacheCallbackConfigKey = CacheConsts.REDIS_KEY_CALLBACK_CONFIG_PREFIX + companyId.toString();
        List<CompanyCallbackConfig> callbackConfigs = new ArrayList<>();
        CompanyCallbackConfig callbackConfig = null;
        if (redisUtil.hasKey(cacheCallbackConfigKey)) {
            JSONArray jsonArray = new JSONArray(redisUtil.get(cacheCallbackConfigKey));
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CompanyCallbackConfig companyCallbackConfig = new CompanyCallbackConfig();
                    companyCallbackConfig.setCompanyId(jsonObject.getLong("companyId"));
                    companyCallbackConfig.setCallbackPath(jsonObject.getStr("callbackPath"));
                    companyCallbackConfig.setCompanyCode(jsonObject.getStr("companyCode"));
                    companyCallbackConfig.setCallbackType(jsonObject.getStr("callbackType"));
                    companyCallbackConfig.setId(jsonObject.getLong("id"));
                    companyCallbackConfig.setAdditionalValue(jsonObject.getStr("additionalValue"));
                    companyCallbackConfig.setAppId(jsonObject.getStr("appId"));
                    companyCallbackConfig.setNeedBack(jsonObject.getBool("needBack"));
                    callbackConfigs.add(companyCallbackConfig);
                }
            }
        }

        if (callbackConfigs != null && callbackConfigs.size() > 0) {
            callbackConfig = callbackConfigs.stream()
                    .filter(entry -> entry.getNeedBack() == true && entry.getCallbackType().equals(callbackType))
                    .findFirst().orElse(null);
        }

        return callbackConfig;
    }

    @Async
    /**
     * 包车订单通知报表
     * 
     * @param orderParam
     * @param coreOrderId
     * @param estimatePrice
     */
    public void sendToReportForRental(CreateOrderParam orderParam, OrderSource orderSource) {
        // 将信息缓存起来
        OrderForReport orderForReport = new OrderForReport();
        orderForReport.setServiceType(orderParam.getServiceType());
        orderForReport.setAccountId(orderParam.getAccountId());
        orderForReport.setDepartCityName(orderParam.getDepartCityName());
        orderForReport.setDestCityName(orderParam.getDestCityName());
        orderForReport.setIsPrepay(false);
        orderForReport.setIsUpgrade(false);
        orderForReport.setProjectId(orderParam.getProjectId());
        orderForReport.setProjectName("");
        orderForReport.setUseCarReason(orderParam.getUseCarReason());
        orderForReport.setSceneId(orderParam.getSceneId());
        orderForReport.setSceneCode(orderParam.getSceneCode());
        orderForReport.setSceneName(orderParam.getSceneNameCn());
        orderForReport.setCustomInfo(orderParam.getCustomInfo());
        orderForReport.setName(orderParam.getNameCn());
        orderForReport.setPhone(orderParam.getUserPhone());
        orderForReport.setEmergencyPhone(orderParam.getEmergencyPhone());
        orderForReport.setPassengerName(orderParam.getPassengerName());
        orderForReport.setEstimatePrice(orderSource.getEstimatePrice());

        redisUtil.set(CacheConsts.REDIS_KEY_ORDER_FOR_REPORT_PREFIX + String.valueOf(orderSource.getOrderId()),
                JSONUtil.toJsonStr(orderForReport), CacheConsts.TEN_SECOND);

        JSONObject orderStatusVo = new JSONObject();
        orderStatusVo.set("companyId", orderParam.getCompanyId());
        orderStatusVo.set("partnerOrderId", orderSource.getOrderId());
        orderStatusVo.set("coreOrderId", orderSource.getCoreOrderId());
        orderStatusVo.set("userId", orderParam.getUserId());
        orderStatusVo.set("status", 1);

        JSONObject pickupLocation = new JSONObject();
        pickupLocation.set("lat", orderParam.getDepartLat());
        pickupLocation.set("lng", orderParam.getDepartLng());
        pickupLocation.set("address", orderParam.getPickupLocation());
        pickupLocation.set("name", orderParam.getPickupLocationName());

        JSONObject destLocation = new JSONObject();
        destLocation.set("lat", orderParam.getDestLat());
        destLocation.set("lng", orderParam.getDestLng());
        destLocation.set("address", orderParam.getDestLocation());
        destLocation.set("name", orderParam.getDestLocationName());

        JSONObject orderInfo = new JSONObject();
        orderInfo.set("serviceType", 20);
        orderInfo.set("cityId", orderParam.getDepartCityCode());
        orderInfo.set("endCityId", orderParam.getDestCityCode());
        orderInfo.set("passengerPhone", orderParam.getPassengerPhone());
        orderInfo.set("orderTime", orderParam.getDepartTime());
        orderInfo.set("startLocation", pickupLocation);
        orderInfo.set("endLocation", destLocation);
        orderInfo.set("charteredBusType", orderParam.getCharteredBusType());

        JSONObject orderDetail = new JSONObject();
        orderDetail.set("driver", null);
        orderDetail.set("fee", null);
        orderDetail.set("order", orderInfo);

        orderStatusVo.set("orderDetail", orderDetail);

        delayProducer.sendDelayReport(orderStatusVo);

        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/handleStatusChg");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(orderStatusVo));
            remoteCallFeign.call(remoteCallReportDto);
        } catch (Exception ex) {
            log.error("创建包车订单通知core报表服务遇到异常", ex);
        }
    }

    /**
     * 非订单状态变更时通知报表服务
     * 状态字段统一为 100 根据子状态判断各个类型
     *
     * @param reportBaseDto
     * @throws Exception
     */
    public void notifyReportServiceNonStatusChanged(RequestReportNotifyBaseDto reportBaseDto,
            NotifyReportType notifyReportType, JSONObject jsonObject) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        if (notifyReportType == NotifyReportType.SET_USE_CAR_REASON) {
            if (false == jsonObject.containsKey("useCarReason")) {
                return;
            }
            RequestReportNotifySetUseCarReasonDto useCarReasonDto = new RequestReportNotifySetUseCarReasonDto();
            useCarReasonDto.setUserCarReason(jsonObject.getStr("useCarReason"));
            useCarReasonDto.setId(reportBaseDto.getId());
            useCarReasonDto.setStatus((short) 100);
            useCarReasonDto.setSubStatus(notifyReportType.getCode().shortValue());

            reportBaseDto = useCarReasonDto;
        }

        String msgStr = objectMapper.writeValueAsString(reportBaseDto);
        log.info("notifyReportServiceNonStatusChanged ==> msg={}", msgStr);

        String key = reportBaseDto.getId() + "_" + notifyReportType.getName();

        RequestPublishMessageDto publishMessage = new RequestPublishMessageDto();
        publishMessage.setMessageId("notifyReportServiceNonStatusChanged:" + snowFlakeUtil.getNextId());
        publishMessage.setTopic(TOPIC_ORDER_NOTIFY);
        publishMessage.setTag(TAG_REPORT_ORDER);
        publishMessage.setKey(key);
        publishMessage.setMessageBody(msgStr);
        JSONObject paramObject = new JSONObject(publishMessage);
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath("/api/v2/bridge/publishMessage");
        remoteCallDto.setContent(paramObject.toString());
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            log.info(response.getMessage());
        }

        String destination = TOPIC_ORDER_NOTIFY + ":" + TAG_REPORT_ORDER;
        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
            }

            public void onException(Throwable throwable) {
                log.error("notifyLog fail; {}", throwable.getMessage());
            }
        });
    }

    /**
     * 订单投诉通知报表服务
     * 
     * @param complaint
     * @param companyId
     * @throws Exception
     */
    public void notifyReportServiceComplaint(RequestBillNotifyComplaintDto complaint, Long companyId) throws Exception {
        Log iLog = logService.getLog(companyId, null, complaint.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_COMPLAINT);
        iLog.setBody(JSONUtil.toJsonStr(complaint));
        try {
            complaint.setCompanyId(companyId);
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/handleComplaint");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(complaint));
            remoteCallFeign.call(remoteCallReportDto);
        } catch (Exception ex) {
            log.error("订单【{}】投诉时，调用接口【OrderTask.notifyReportServiceComplaint】通知core报表时，遇到异常:", complaint.getId(), ex);
            
            logService.saveErrorLogAsync(iLog, ex);
        }

        Log mqLog = logService.getLog(companyId, null, complaint.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_COMPLAINT_MQ);
        mqLog.setBody(JSONUtil.toJsonStr(complaint));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(complaint);
            // log.info("notifyReportServiceComplaint ==> msg={}", msgStr);
            String key = complaint.getOrderId() + "_complaint";

            String destination = TOPIC_ORDER_COMPLAINT_NOTIFY + ":" + TAG_ORDER_COMPLAINT;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    logService.saveLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_COMPLAINT_NOTIFY, TAG_ORDER_COMPLAINT));
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    logService.saveErrorLogAsync(mqLog, throwable.getMessage());
                }
            });
        } catch (Exception ex) {
            log.error("订单【{}】投诉时，发送mq消息通知ka报表时，遇到异常", complaint.getId(), ex);
            logService.saveErrorLogAsync(mqLog, ex);
        }
    }

    /**
     * 修改目的地通知报表服务
     * 
     * @param requestReportNotifyChangeDest
     * @throws Exception
     */
    public void notifyReportServiceChangeDest(RequestReportNotifyChangeDest requestReportNotifyChangeDest)
            throws Exception {
        Log iLog = logService.getLog(requestReportNotifyChangeDest.getCompanyId(), null,
                requestReportNotifyChangeDest.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_CHANGE_DEST);
        iLog.setBody(JSONUtil.toJsonStr(requestReportNotifyChangeDest));
        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/changeDest");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(requestReportNotifyChangeDest));
            remoteCallFeign.call(remoteCallReportDto);
            logService.saveLogAsync(iLog, "");
        } catch (Exception ex) {
            log.error("订单【{}】修改目的地，调用接口通知core报表时，遇到异常", requestReportNotifyChangeDest.getId(), ex);
            logService.saveErrorLogAsync(iLog, ex);
        }

        Log mqLog = logService.getLog(requestReportNotifyChangeDest.getCompanyId(), null,
                requestReportNotifyChangeDest.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_CHANGE_DEST_MQ);
        mqLog.setBody(JSONUtil.toJsonStr(requestReportNotifyChangeDest));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(requestReportNotifyChangeDest);
            // log.info("notifyReportServiceChangeDest ==> msg={}", msgStr);
            String key = requestReportNotifyChangeDest.getId() + "_" + requestReportNotifyChangeDest.getStatus();

            String destination = TOPIC_ORDER_CHANGE_DEST_NOTIFY + ":" + TAG_ORDER_CHANGE_DEST;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    logService.saveLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_CHANGE_DEST_NOTIFY, TAG_ORDER_CHANGE_DEST));
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    logService.saveErrorLogAsync(mqLog, throwable.getMessage());
                }
            });
        } catch (Exception ex) {
            log.error("订单【{}】修改目的地，发送mq消息通知ka报表时，遇到异常", requestReportNotifyChangeDest.getId(), ex);
            logService.saveErrorLogAsync(mqLog, ex);
        }
    }

    /**
     * 订单取消通知报表服务
     * 
     * @param reportDto
     * @throws Exception
     */
    public void notifyReportServiceCancel(RequestReportNotifyCancelOrderDto reportDto) throws Exception {
        if (reportDto.getStatus() != 7)
            return;

        Log iLog = logService.getLog(reportDto.getCompanyId(), null,
                reportDto.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_CANCEL_ORDER);
        iLog.setBody(JSONUtil.toJsonStr(reportDto));

        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/cancelOrder");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(reportDto));
            remoteCallFeign.call(remoteCallReportDto);
            logService.saveLogAsync(iLog, "");
        } catch (Exception ex) {
            log.error("取消订单【{}】时，调用接口通知core报表时，遇到异常", reportDto.getId(), ex);
            logService.saveErrorLogAsync(iLog, ex);
        }

        Log mqLog = logService.getLog(reportDto.getCompanyId(), null,
                reportDto.getId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_CANCEL_ORDER_MQ);
        mqLog.setBody(JSONUtil.toJsonStr(reportDto));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(reportDto);
            // log.info("cancelOrderMsg ==> msg={}", msgStr);
            String key = reportDto.getId() + "_" + reportDto.getStatus();

            String cancelTopic = TOPIC_ORDER_CANCEL_NOTIFY + ":" + TAG_ORDER_CANCEL;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(cancelTopic, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    logService.saveLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_CANCEL_NOTIFY, TAG_ORDER_CANCEL));
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    logService.saveErrorLogAsync(mqLog, throwable.getMessage());
                }
            });
        } catch (Exception ex) {
            log.error("取消订单【{}】时，发送mq消息通知ka报表时，遇到异常", reportDto.getId(), ex);
            logService.saveErrorLogAsync(mqLog, ex);
        }
    }

    /**
     * 追加车型通知报表
     * 
     * @param requestOrderNotifyDto
     * @throws Exception
     */
    public void notifyReportServiceAppendPlaceOrder(RequestOrderNotifyDto requestOrderNotifyDto) throws Exception {
        String key = requestOrderNotifyDto.getOrderId() + "_1";

        RequestAppendPlaceOrderForReportDto appendPlaceOrderForReportDto = new RequestAppendPlaceOrderForReportDto();
        BeanUtil.copyProperties(requestOrderNotifyDto, appendPlaceOrderForReportDto);
        appendPlaceOrderForReportDto.setAdditionalEstimatePrice(requestOrderNotifyDto.getEstimatePrice());

        Log iLog = logService.getLog(appendPlaceOrderForReportDto.getCompanyId(), null,
                appendPlaceOrderForReportDto.getOrderId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_APPEND_PLACE_ORDER);
        iLog.setBody(JSONUtil.toJsonStr(appendPlaceOrderForReportDto));

        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/appendPlaceOrder");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(appendPlaceOrderForReportDto));
            remoteCallFeign.call(remoteCallReportDto);
            logService.saveLogAsync(iLog, "");

        } catch (Exception ex) {
            log.error("订单【{}】追加下单时，调用接口通知core报表时，遇到异常", requestOrderNotifyDto.getOrderId(), ex);
            logService.saveErrorLogAsync(iLog, ex);
        }

        Log mqLog = logService.getLog(appendPlaceOrderForReportDto.getCompanyId(), null,
                appendPlaceOrderForReportDto.getOrderId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_APPEND_PLACE_ORDER_MQ);
        mqLog.setBody(JSONUtil.toJsonStr(appendPlaceOrderForReportDto));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(appendPlaceOrderForReportDto);
            String destination = TOPIC_ORDER_APPEND_NOTIFY + ":" + TAG_ORDER_APPEND;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    logService.saveLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_APPEND_NOTIFY, TAG_ORDER_APPEND));
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    logService.saveErrorLogAsync(mqLog, throwable.getMessage());
                }
            });
        } catch (Exception ex) {
            log.error("订单【{}】追加下单时，发送mq消息通知ka报表时，遇到异常", requestOrderNotifyDto.getOrderId(), ex);
            logService.saveErrorLogAsync(mqLog, ex);
        }
    }

    /**
     * 个人已付通知报表
     * 
     * @param paidDto
     * @throws Exception
     */
    public void notifyReportServicePaid(RequestReportNotifyPaidDto paidDto) throws Exception {
        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/handlePay");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(paidDto));
            remoteCallFeign.call(remoteCallReportDto);

            if (null != paidDto.getOrderIds()) {
                for (Long orderId : paidDto.getOrderIds()) {
                    Log iLog = logService.getLog(paidDto.getCompanyId(), null, orderId,
                            InterfaceEnum.ORDER_NOTIFY_REPORT_PAID);
                    iLog.setBody(JSONUtil.toJsonStr(paidDto));
                    logService.saveLogAsync(iLog, "");
                }
            }
        } catch (Exception ex) {
            log.error("个人支付通知core报表服务遇到异常", ex);

            if (null != paidDto.getOrderIds()) {
                for (Long orderId : paidDto.getOrderIds()) {
                    Log iLog = logService.getLog(paidDto.getCompanyId(), null, orderId,
                            InterfaceEnum.ORDER_NOTIFY_REPORT_PAID);
                    iLog.setBody(JSONUtil.toJsonStr(paidDto));
                    logService.saveLogAsync(iLog, "");
                }
            }
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(paidDto);
            // log.info("notifyReportServicePaid ==> msg={}", msgStr);
            String key = paidDto.getTransNo() + "_paid";

            String destination = TOPIC_ORDER_PAID_NOTIFY + ":" + TAG_ORDER_PAID;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    if (null != paidDto.getOrderIds()) {
                        for (Long orderId : paidDto.getOrderIds()) {
                            Log iLog = logService.getLog(paidDto.getCompanyId(), null, orderId,
                                    InterfaceEnum.ORDER_NOTIFY_REPORT_PAID_MQ);
                            iLog.setBody(JSONUtil.toJsonStr(paidDto));
                            logService.saveLogAsync(iLog,
                                    StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_PAID_NOTIFY, TAG_ORDER_PAID));
                        }
                    }
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    if (null != paidDto.getOrderIds()) {
                        for (Long orderId : paidDto.getOrderIds()) {
                            Log iLog = logService.getLog(paidDto.getCompanyId(), null, orderId,
                                    InterfaceEnum.ORDER_NOTIFY_REPORT_PAID_MQ);
                            iLog.setBody(JSONUtil.toJsonStr(paidDto));
                            logService.saveErrorLogAsync(iLog, throwable.getMessage());
                        }
                    }
                }
            });
        } catch (Exception ex) {
            log.error("个人支付通知ka报表服务遇到异常", ex);
            if (null != paidDto.getOrderIds()) {
                for (Long orderId : paidDto.getOrderIds()) {
                    Log iLog = logService.getLog(paidDto.getCompanyId(), null, orderId,
                            InterfaceEnum.ORDER_NOTIFY_REPORT_PAID_MQ);
                    iLog.setBody(JSONUtil.toJsonStr(paidDto));
                    logService.saveErrorLogAsync(iLog, ex);
                }
            }
        }
    }

    /**
     * 订单完成时通知报表，例如合规预警....
     * 
     * @param otherDto
     * @throws Exception
     */
    public void notifyReportServiceCompleted(RequestNotifyReportForCompletionDto otherDto) throws Exception {
        Log iLog = logService.getLog(otherDto.getCompanyId(), null, otherDto.getOrderId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_COMPLETION);
        iLog.setBody(JSONUtil.toJsonStr(otherDto));
        try {
            RemoteCallDto remoteCallReportDto = new RemoteCallDto();
            remoteCallReportDto.setPath("/api/v2/report/compensation/handleCompletedOrder");
            remoteCallReportDto.setContent(JSONUtil.toJsonStr(otherDto));
            remoteCallFeign.call(remoteCallReportDto);
            logService.saveLogAsync(iLog, null);
        } catch (Exception ex) {
            log.error("订单【{}】完成时，调用接口通知core报表时，遇到异常:{}", otherDto.getOrderId(), ex);
            logService.saveErrorLogAsync(iLog, ex);
        }

        Log mqLog = logService.getLog(otherDto.getCompanyId(), null, otherDto.getOrderId(),
                InterfaceEnum.ORDER_NOTIFY_REPORT_COMPLETION_MQ);
        mqLog.setBody(JSONUtil.toJsonStr(otherDto));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(otherDto);
            // log.info("notifyReportServiceCompleted ==> msg={}", msgStr);
            String key = otherDto.getOrderId() + "_completion";

            String destination = TOPIC_ORDER_COMPLETION_NOTIFY + ":" + TAG_COMPLETION_PAID;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    logService.saveLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_COMPLETION_NOTIFY, TAG_COMPLETION_PAID));
                }

                public void onException(Throwable throwable) {
                    log.error("notifyLog fail; {}", throwable.getMessage());
                    logService.saveErrorLogAsync(mqLog,
                            StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_COMPLETION_NOTIFY, TAG_COMPLETION_PAID));
                }
            });
        } catch (Exception ex) {
            logService.saveErrorLogAsync(mqLog,
                    StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_COMPLETION_NOTIFY, TAG_COMPLETION_PAID));
            log.error("订单【{}】完成时，发送mq消息通知ka报表时，遇到异常:", otherDto.getOrderId(), ex);
        }
    }

    /**
     * 发送订单状态变更消息
     * 
     * @param orderId            订单id
     * @param previousOrderState 订单上一状态
     * @param orderState         订单状态
     * @param replaceOrderFlag   改派标识 true-改派 false-未改派
     */
    @Async
    public void sendOrderStatusMQ(Long orderId, Short previousOrderState, Short orderState, Boolean replaceOrderFlag,
            String phoneForUser) {
        Log iLog = logService.getLog(null, null, orderId, InterfaceEnum.ORDER_NOTIFY_CDS_STATUS_CHG_MQ);
        iLog.setInterfaceName(iLog.getInterfaceName() + ":" + orderState);
        try {
            JSONObject orderStatusJsonObject = new JSONObject();
            orderStatusJsonObject.set("orderId", orderId);
            orderStatusJsonObject.set("previousOrderState", previousOrderState);
            orderStatusJsonObject.set("orderState", orderState);
            orderStatusJsonObject.set("replaceOrderFlag", replaceOrderFlag);
            orderStatusJsonObject.set("phoneForUser", phoneForUser);
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(orderStatusJsonObject);
            // log.info("sendOrderStatusMQ ==> msg={}", msgStr);
            String key = orderId + "_" + orderState;

            iLog.setBody(msgStr);

            String destination = TOPIC_ORDER_STATUS_NOTIFY + ":" + TAG_ORDER_STATUS_INFO;
            Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
            // TODO add by syt 状态5延时回传订单信息，防止订单结算未完成，导致推送金额空，注意后面需要优化！！！
            if (orderState == 5) {
                rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                    public void onSuccess(SendResult sendResult) {
                        logService.saveLogAsync(iLog,
                                StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_STATUS_NOTIFY, TAG_ORDER_STATUS_INFO));
                    }

                    public void onException(Throwable throwable) {
                        log.error("sendOrderStatusMQ fail; {}", throwable.getMessage());
                        logService.saveErrorLogAsync(iLog,
                                StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_STATUS_NOTIFY, TAG_ORDER_STATUS_INFO));
                    }
                }, 3000, 2);
            } else {
                rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                    public void onSuccess(SendResult sendResult) {
                        logService.saveLogAsync(iLog,
                                StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_STATUS_NOTIFY, TAG_ORDER_STATUS_INFO));
                    }

                    public void onException(Throwable throwable) {
                        log.error(
                                "发送订单状态变更消息出现异常【OrderTask->sendOrderStatusMQ.onException,orderId:{}, previousOrderState:{}, orderState:{}, replaceOrderFlag:{}, phoneForUser:{}】",
                                orderId, previousOrderState, orderState, replaceOrderFlag, phoneForUser,
                                new Exception(throwable.getMessage()));
                        logService.saveErrorLogAsync(iLog,
                                StrUtil.format("topic:{},tag:{}", TOPIC_ORDER_STATUS_NOTIFY, TAG_ORDER_STATUS_INFO));
                    }
                });
            }
        } catch (Exception ex) {
            logService.saveErrorLogAsync(iLog, ex);
            log.error(
                    "发送订单状态变更消息出现异常【OrderTask->sendOrderStatusMQ,orderId:{}, previousOrderState:{}, orderState:{}, replaceOrderFlag:{}, phoneForUser:{}】",
                    orderId, previousOrderState, orderState, replaceOrderFlag, phoneForUser, ex);
        }
    }

    private Integer addHistoryLocation(String userId, LocationVo location) throws Exception {
        String key = CacheConsts.REDIS_KEY_HISTORY_LOCATION_PREFIX + ":userid:" + userId + ":city:"
                + location.getCityCode();
        long count = redisUtil.listGetSize(key);

        // 查找删除已经存在相同位置名称的地点
        List<Object> objectList = redisUtil.listGetRange(key, 0, count);
        for (Object object : objectList) {
            LocationVo hisLocation = (LocationVo) object;
            if (hisLocation.getLocationName().equals(location.getLocationName())) {
                redisUtil.listRemove(key, 1, hisLocation);
                count -= 1;
                break;
            }
        }

        if (count >= 10) {
            redisUtil.listRightPop(key);
        }
        redisUtil.listLeftPush(key, location);
        return 0;
    }
}