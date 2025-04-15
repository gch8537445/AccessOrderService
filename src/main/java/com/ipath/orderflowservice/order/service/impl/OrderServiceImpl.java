package com.ipath.orderflowservice.order.service.impl;

import static java.lang.Long.max;
import static java.lang.Long.parseLong;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.bean.CustomException;
import com.ipath.common.util.RedisUtil;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.cds.mst.service.CdsMstSerice;
import com.ipath.orderflowservice.core.bill.service.BillService;
import com.ipath.orderflowservice.core.booking.service.BookingService;
import com.ipath.orderflowservice.core.order.service.CoreOrderService;
import com.ipath.orderflowservice.core.tencent.bean.LocationResResult;
import com.ipath.orderflowservice.core.tencent.service.CoreTencentService;
import com.ipath.orderflowservice.core.track.bean.dto.LastDriverPositionDto;
import com.ipath.orderflowservice.core.track.service.TrackService;
import com.ipath.orderflowservice.feignclient.ConfigurationFeign;
import com.ipath.orderflowservice.feignclient.CouponConsumeFeign;
import com.ipath.orderflowservice.feignclient.OrderValidationFeign;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.CdsMstDto;
import com.ipath.orderflowservice.feignclient.dto.CompanyAbnormalItems;
import com.ipath.orderflowservice.feignclient.dto.CompanyAbnormalRules;
import com.ipath.orderflowservice.feignclient.dto.ReimModel;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.feignclient.dto.RequestAppendPlaceOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestBillNotifyComplaintDto;
import com.ipath.orderflowservice.feignclient.dto.RequestChangeCouponState;
import com.ipath.orderflowservice.feignclient.dto.RequestCheckOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestCompanyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestEstimateDto;
import com.ipath.orderflowservice.feignclient.dto.RequestNotifyReportForCompletionDto;
import com.ipath.orderflowservice.feignclient.dto.RequestOrderNotifyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestOrderPayEventDto;
import com.ipath.orderflowservice.feignclient.dto.RequestOrderQueryDto;
import com.ipath.orderflowservice.feignclient.dto.RequestPlaceOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestPlaceOrderExtraParamsDto;
import com.ipath.orderflowservice.feignclient.dto.RequestReportNotifyCancelOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestReportNotifyChangeDest;
import com.ipath.orderflowservice.feignclient.dto.RequestReportNotifyPaidDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSceneInfoDto;
import com.ipath.orderflowservice.feignclient.dto.RequestUsageStateDto;
import com.ipath.orderflowservice.feignclient.dto.RequestUserCouponDto;
import com.ipath.orderflowservice.feignclient.dto.ServiceFeeModel;
import com.ipath.orderflowservice.feignclient.dto.SettleReq;
import com.ipath.orderflowservice.feignclient.dto.TaxFeeModel;
import com.ipath.orderflowservice.feignclient.dto.UpdateUsageCountDto;
import com.ipath.orderflowservice.feignclient.dto.UpgradeDto;
import com.ipath.orderflowservice.feignclient.dto.UpgradeModel;
import com.ipath.orderflowservice.feignclient.dto.UsageRecordDto;
import com.ipath.orderflowservice.ka.configuration.service.ConfigurationService;
import com.ipath.orderflowservice.ka.coupon.service.CouponService;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.IncreaseAmountRule;
import com.ipath.orderflowservice.order.bean.RedisCpol;
import com.ipath.orderflowservice.order.bean.RedisCpolRegulationInfo;
import com.ipath.orderflowservice.order.bean.RedisCustomInfoH5;
import com.ipath.orderflowservice.order.bean.bo.PartnerRegulationCompanyConfig;
import com.ipath.orderflowservice.order.bean.bo.RegulationBo;
import com.ipath.orderflowservice.order.bean.bo.RegulationConfigBo;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.CarLevel;
import com.ipath.orderflowservice.order.bean.constant.CompanyConstant;
import com.ipath.orderflowservice.order.bean.constant.ComplaintTypeConst;
import com.ipath.orderflowservice.order.bean.constant.CoreOrderConstant;
import com.ipath.orderflowservice.order.bean.constant.IncreaseAmountConstant;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.constant.ResourceKeyConsts;
import com.ipath.orderflowservice.order.bean.constant.UserConstant;
import com.ipath.orderflowservice.order.bean.param.AppendCarTypeParam;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.ExtraService;
import com.ipath.orderflowservice.order.bean.param.FeedbackParam;
import com.ipath.orderflowservice.order.bean.param.KeyValue;
import com.ipath.orderflowservice.order.bean.param.OrderExtraParameter;
import com.ipath.orderflowservice.order.bean.param.OrderIdParam;
import com.ipath.orderflowservice.order.bean.param.OrderPayDetail;
import com.ipath.orderflowservice.order.bean.param.OrderPayNotifyParam;
import com.ipath.orderflowservice.order.bean.param.ReorderParam;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import com.ipath.orderflowservice.order.bean.param.UpgradeParam;
import com.ipath.orderflowservice.order.bean.param.WorkflowParam;
import com.ipath.orderflowservice.order.bean.param.ordercore.RePlaceOrderReq;
import com.ipath.orderflowservice.order.bean.vo.CacheCarTypeLabelEstimateResult;
import com.ipath.orderflowservice.order.bean.vo.CacheCarTypeLabelOrder;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.bean.vo.CacheEstimateResult;
import com.ipath.orderflowservice.order.bean.vo.CacheICarOrder;
import com.ipath.orderflowservice.order.bean.vo.CacheOrder;
import com.ipath.orderflowservice.order.bean.vo.CacheUserInfo;
import com.ipath.orderflowservice.order.bean.vo.ComplaintInfo;
import com.ipath.orderflowservice.order.bean.vo.CouponResult;
import com.ipath.orderflowservice.order.bean.vo.DriverInfo;
import com.ipath.orderflowservice.order.bean.vo.EstimateCar;
import com.ipath.orderflowservice.order.bean.vo.EstimatePriceResult;
import com.ipath.orderflowservice.order.bean.vo.FeeItem;
import com.ipath.orderflowservice.order.bean.vo.FeedbackVo;
import com.ipath.orderflowservice.order.bean.vo.KeyValueInfoVo;
import com.ipath.orderflowservice.order.bean.vo.KeyValueVo;
import com.ipath.orderflowservice.order.bean.vo.RecommendedLocationInfoVo;
import com.ipath.orderflowservice.order.bean.vo.SceneBaseInfo;
import com.ipath.orderflowservice.order.bean.vo.SceneInfo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarLevelVo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarSourceVo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarTypeLabelCityMapping;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarTypeLabelEstimateVo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarTypeLabelVo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.TemplateSimpleVo;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.TemplateVo;
import com.ipath.orderflowservice.order.business.cartypelabel.service.CarTypeLabelService;
import com.ipath.orderflowservice.order.business.dispatch.service.DispatchService;
import com.ipath.orderflowservice.order.dao.AmgenPoSceneMapper;
import com.ipath.orderflowservice.order.dao.ComSceneMapper;
import com.ipath.orderflowservice.order.dao.OrderApplyMapper;
import com.ipath.orderflowservice.order.dao.OrderBaseMapper;
import com.ipath.orderflowservice.order.dao.OrderChangeDestLogMapper;
import com.ipath.orderflowservice.order.dao.OrderComplaintMapper;
import com.ipath.orderflowservice.order.dao.OrderExtraServiceMapper;
import com.ipath.orderflowservice.order.dao.OrderFeedbackMapper;
import com.ipath.orderflowservice.order.dao.OrderSourceMapper;
import com.ipath.orderflowservice.order.dao.OrderSummaryMapper;
import com.ipath.orderflowservice.order.dao.UserBaseMapper;
import com.ipath.orderflowservice.order.dao.bean.ComScene;
import com.ipath.orderflowservice.order.dao.bean.CompanyCallbackConfig;
import com.ipath.orderflowservice.order.dao.bean.CompanyPreApprovalSetting;
import com.ipath.orderflowservice.order.dao.bean.ItemValue;
import com.ipath.orderflowservice.order.dao.bean.OrderApply;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.dao.bean.OrderChangeDestLog;
import com.ipath.orderflowservice.order.dao.bean.OrderComplaint;
import com.ipath.orderflowservice.order.dao.bean.OrderExtraService;
import com.ipath.orderflowservice.order.dao.bean.OrderFeedback;
import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import com.ipath.orderflowservice.order.dao.bean.OrderMeeting;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;
import com.ipath.orderflowservice.order.dao.bean.OrderSummary;
import com.ipath.orderflowservice.order.dao.bean.SceneItemValue;
import com.ipath.orderflowservice.order.dao.bean.UserBase;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitMappingVo;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitVo;
import com.ipath.orderflowservice.order.dao.vo.OrderForReport;
import com.ipath.orderflowservice.order.enums.CompanyLimitTypexEnum;
import com.ipath.orderflowservice.order.producer.DelayProducer;
import com.ipath.orderflowservice.order.service.CacheService;
import com.ipath.orderflowservice.order.service.CipherService;
import com.ipath.orderflowservice.order.service.ComSceneService;
import com.ipath.orderflowservice.order.service.CompanyService;
import com.ipath.orderflowservice.order.service.JingDongService;
import com.ipath.orderflowservice.order.service.OrderCheckService;
import com.ipath.orderflowservice.order.service.OrderLimitService;
import com.ipath.orderflowservice.order.service.OrderMeetingService;
import com.ipath.orderflowservice.order.service.OrderService;
import com.ipath.orderflowservice.order.service.RegulationService;
import com.ipath.orderflowservice.order.service.SendMsgService;
import com.ipath.orderflowservice.order.service.SystemService;
import com.ipath.orderflowservice.order.service.UserService;
import com.ipath.orderflowservice.order.task.OrderTask;
import com.ipath.orderflowservice.order.util.CacheUtil;
import com.ipath.orderflowservice.order.util.DataUtil;
import com.ipath.orderflowservice.order.util.ResourceUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ordersService")
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal FROZEN_AMOUNT_RATE = new BigDecimal("1"); // 冻结金额为最大估计金额的倍数

    @Value("${ipath.specialCustomer}")
    private Long IPATH_SPECIAL_CUSTOMER;
    @Value("${ipath.carLevelName}")
    private String IPATH_CAR_LEVEL_NAME;
    @Value("${rocketmq.topic.order-info}")
    private String TOPIC_ORDER_INFO;
    @Value("${rocketmq.tags.detail-result}")
    private String TAG_DETAIL_RESULT;
    @Value("${rocketmq.tags.location-result}")
    private String TAG_LOCATION_RESULT;
    @Value("${rocketmq.tags.approval-result-notify}")
    private String TAG_APPROVAL_RESULT_NOTIFY;
    @Value("${rocketmq.tags.pay-result-notify}")
    private String TAG_PAY_RESULT_NOTIFY;
    @Value("")
    private String MOBILE_URL;
    @Value("${notifyUrl.notifyPay}")
    private String notifyPay;
    @Value("${rePlaceOrderAfterCoreCancelSwitch}")
    private boolean rePlaceOrderAfterCoreCancelSwitch;
    @Value("${spring.profiles.active}")
    private String active;
    @Value("${sendMsg.mail.to}")
    private String to;
    @Value("${customer.service.phone}")
    private String customerServicePhone;
    @Value("${scene.overtime}")
    private String overtimeCode;

    @Value("${rocketmq.booking.topic}")
    private String TOPIC_ORDER_CHG_TO_REPORT;
    @Value("${rocketmq.booking.tag}")
    private String TAG_ORDER_CHG_TO_REPORT;

    @Value("${serviceMessage.tencent.companyId}")
    private String tencentCompanyId;

    @Value("${serviceMessage.tencent.processOperationNotificationTemplateId}")
    private String tencentProcessOperationNotificationTemplateId;

    @Value("${serviceMessage.wpp.companyId}")
    private String wppCompanyId;

    @Value("${serviceMessage.wpp.processOperationNotificationTemplateId}")
    private String wppProcessOperationNotificationTemplateId;

    @Value("${company.merk}")
    private String merkCompanyId;

    @Autowired
    @Lazy
    private OrderServiceImpl orderServiceImpl; // ** 注入自己，用来解决spring-AOP调用内部函数事务不开启问题。不使用AspectJ的原因是不支持动态数据源切面拦截 **
    @Autowired
    private SendMsgService msgService;
    @Autowired
    private OrderBaseMapper orderBaseMapper;
    @Autowired
    private OrderExtraServiceMapper orderExtraServiceMapper;
    @Autowired
    private OrderSourceMapper orderSourceMapper;
    @Autowired
    private OrderApplyMapper orderApplyMapper;
    @Autowired
    private OrderFeedbackMapper orderFeedbackMapper;
    @Autowired
    private OrderComplaintMapper orderComplaintMapper;
    @Autowired
    private OrderChangeDestLogMapper orderChangeDestLogMapper;
    @Autowired
    private AmgenPoSceneMapper amgenPoSceneMapper;
    @Autowired
    private ConfigurationFeign configurationFeign;
    @Autowired
    private OrderValidationFeign orderValidationFeign;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderTask orderTask;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BillService billService;
    @Autowired
    private SnowFlakeUtil snowFlakeUtil;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private RemoteCallFeign remoteCallFeign;
    @Autowired
    private CouponConsumeFeign couponConsumeFeign;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ComSceneMapper comSceneMapper;
    @Autowired
    private UserBaseMapper userBaseMapper;

    @Autowired
    private OrderLimitService orderLimitService;

    @Autowired
    private CoreOrderService coreOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private JingDongService jingDongService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OrderCheckService orderCheckService;

    @Autowired
    private OrderSummaryMapper orderSummaryMapper;

    @Autowired
    private DelayProducer delayProducer;

    @Autowired
    private OrderMeetingService orderMeetingService;

    @Autowired
    private ResourceUtil resourceUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private CdsMstSerice cdsMstSerice;

    @Autowired
    private LogService logService;

    @Autowired
    private CacheService cacheService;

    private static List<String> EXTRAL_FEE_CODES = ImmutableList.of("clean_fee", "air_service_fee", "other_fee",
            "bridge_fee", "toll_fee", "high_speed_fee", "park_fee");

    @Autowired
    private RegulationService regulationService;

    @Autowired
    private ComSceneService comSceneService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private CoreTencentService coreTencentService;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private CarTypeLabelService carTypeLabelService;

    @Autowired
    private CouponService couponService;

    @Value("${envConfig.receive.isGray}")
    public boolean receiveIsGray;

    /**
     * 调用中台接口估价
     *
     * @param orderParam       下单参数
     * @param isAppendEstimate 是否追加车型预估价格
     * @param excludeCars      结果中排除订单中已选的车型
     * @return
     * @throws Exception
     */
    public Map<String, Object> estimate(CreateOrderParam orderParam, Boolean isAppendEstimate,
            List<SelectedCar> excludeCars) throws Exception {

        long startTime = System.currentTimeMillis();

        String traceId = IdUtil.simpleUUID();

        Log activityLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                null == orderParam.getOrderId() ? InterfaceEnum.ORDER_ESTIMATE
                        : InterfaceEnum.ORDER_CHANGE_DEST_ESTIMATE,
                traceId);
        activityLog.setBody(JSONUtil.toJsonStr(orderParam));

        // 清空默认地址
        userService.deleteOrderLimitInfoDefAddress(orderParam.getCompanyId(), orderParam.getUserId(),
                orderParam.getDepartLat(), orderParam.getDepartLng());

        // 用sceneId从.net接口查询用户场景金额
        BaseResponse sceneResponse = configurationService.getSceneInfo(orderParam, traceId);
        JSONObject dataObject = JSONUtil.parseObj(sceneResponse.getData());
        SceneInfo sceneInfo = JSONUtil.toBean(dataObject, SceneInfo.class); // 无报销信息表示全公司承担，报销比例为0表示全个人承担
        if (null != sceneInfo.getScene().getSuggestLevel()) {
            if (sceneInfo.getScene().getSuggestLevel().containsKey("cartype")) {
                sceneInfo.getScene()
                        .setRecommendCarType(sceneInfo.getScene().getSuggestLevel().getJSONArray("cartype"));
            }
            if (sceneInfo.getScene().getSuggestLevel().containsKey("content")) {
                sceneInfo.getScene().setRecommendCarTypeTip(sceneInfo.getScene().getSuggestLevel().getStr("content"));
            }
        }

        redisUtil.set(CacheConsts.REDIS_KEY_SCENE_INFO_PREFIX + orderParam.getSceneId().toString(),
                sceneInfo.getScene(), CacheConsts.ORDER_CACHE_EXPIRE_TIME);
        redisUtil.set(CacheConsts.REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX + orderParam.getScenePublishId().toString(),
                sceneInfo.getReimModel(), CacheConsts.ORDER_CACHE_EXPIRE_TIME);

        RequestEstimateDto requestEstimateDto = new RequestEstimateDto();

        // 如果传了订单id，说明订单已经创建，认为是修改目的地重新估价
        if (orderParam.getOrderId() != null) {
            String coreOrderId = null;
            CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(orderParam.getUserId(), orderParam.getOrderId());
            if (cacheOrder != null) {
                coreOrderId = cacheOrder.getCoreOrderId();
            } else {
                OrderSource orderSource = orderSourceMapper.selectByOrderId(orderParam.getOrderId());
                coreOrderId = orderSource.getCoreOrderId();
                // 此处上报一个查数据库的警告，供统计用。
                Log queryDbLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(),
                        orderParam.getOrderId(), InterfaceEnum.ORDER_WARNING_COMMON_MESSAGE, traceId);
                queryDbLog.setInterfacePath("order.warning.querydb.changedestestimate");
                queryDbLog.setInterfaceName("警告-查询数据库-修改目的地预估");
                logService.saveWarningLogAsync(activityLog, null);
            }
            requestEstimateDto.setCoreOrderId(coreOrderId);

            // 修改点--使用缓存
            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderParam.getOrderId());
            if (null != orderBase) {
                if (StrUtil.isNotEmpty(orderBase.getCustomInfo())) {
                    JSONObject customJsonObject = new JSONObject(orderBase.getCustomInfo());
                    if (customJsonObject.containsKey("isPassingPoint")
                            && customJsonObject.getBool("isPassingPoint", false)) {
                        orderParam.setPassingPoints(customJsonObject.getJSONArray("passingPoints"));
                    }
                }
            }
        }

        if (StrUtil.isEmpty(orderParam.getPassengerPhone())) {
            orderParam.setPassengerPhone("13800138000");
        }

        BeanUtil.copyProperties(orderParam, requestEstimateDto);

        // 获取所有可用的标签
        List<CarTypeLabelVo> carTypeLabelEnable = carTypeLabelService.getCarTypeLabelEnable(orderParam.getCompanyId());

        Map<String, Set<String>> labelCityMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(carTypeLabelEnable)) {
            labelCityMap.putAll(carTypeLabelEnable.stream()
                    .collect(Collectors.groupingBy(
                            CarTypeLabelVo::getLabelCode,
                            Collectors.mapping(CarTypeLabelVo::getCityCode, Collectors.toSet()))));
        }

        // 获取预估条件列表
        List<TemplateVo> estimateResponses = carTypeLabelService
                .getUserCarTypeLabelEstimateNew(orderParam.getUserId(), orderParam.getCompanyId());

        if (CollectionUtil.isNotEmpty(estimateResponses)) {
            // 过滤出可用标签中 城市相同的
            estimateResponses = estimateResponses.stream().filter(item -> {
                Set<String> cityCodes = labelCityMap.get(item.getOutItem1());
                if (CollectionUtil.isEmpty(cityCodes)) {
                    return true;
                } else {
                    for (String cityCode : cityCodes) {
                        List<String> cityCodeList = JSONUtil.toList(cityCode, String.class);
                        if (CollectionUtil.isEmpty(cityCodeList) || cityCodeList.contains(null)) {
                            return true;
                        }
                        if (cityCodeList.contains(orderParam.getDepartCityCode())) {
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());

            // 参数选择城市匹配的或者城市为空的
            List<TemplateSimpleVo> list = estimateResponses.stream()
                    .filter(item -> ObjectUtil.isEmpty(item.getCityCode()) ||
                            (ObjectUtil.isNotEmpty(item.getCityCode())
                                    && item.getCityCode().equals(orderParam.getDepartCityCode())))
                    .map(item -> {
                        TemplateSimpleVo vo = new TemplateSimpleVo();
                        vo.setId(item.getId());
                        vo.setAdjustAmount(NumberUtil.toBigDecimal(item.getOutItem3()));
                        vo.setAmountRange(item.getOrderAmount());
                        vo.setDistanceRange(item.getDistance());
                        vo.setDurationRange(item.getDuration());
                        return vo;
                    }).collect(Collectors.toList());

            JSONObject userData = new JSONObject();
            userData.set("template", list);
            requestEstimateDto.setUserData(userData);
        }

        // 向中台预估
        JSONObject result = coreOrderService.estimate(requestEstimateDto, orderParam, traceId, isAppendEstimate);

        List<CacheEstimateResult> carList = processICarEstimateResult(orderParam.getCompanyId(), result, excludeCars,
                sceneInfo, orderParam.getUserId(), orderParam.getEstimateId(), orderParam, traceId);

        boolean englishLanguage = userService.isEnglishLanguage(orderParam.getCompanyId(), orderParam.getUserId());
        if (englishLanguage) {
            carList = carList.stream().map(o -> {
                String reimbursementCn = o.getReimbursementCn();
                if (StringUtils.isNotBlank(reimbursementCn)) {
                    reimbursementCn = reimbursementCn.replace("报销全部", "All Reimburseable");
                    reimbursementCn = reimbursementCn.replace("报销", "Reimburse");
                }
                o.setReimbursementCn(reimbursementCn);
                o.setNameCn(CarLevel.getName(o.getId(), 2));
                o.setNameEn(CarLevel.getName(o.getId(), 2));
                return o;
            }).collect(Collectors.toList());
        }

        // 处理名称
        handleSourceName(carList);

        // 订单首次估价处理结果缓存起来
        String estimateId = result.getStr("estimateId");

        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId, JSONUtil.toJsonStr(carList),
                CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);

        // 预估自定义显示
        handletCustomEstiamte(orderParam, carList);

        // 将预估时的途经点缓存起来
        handletPassingPoints(orderParam, estimateId, carList);

        // 预估标签车型自定义显示
        Set<CarTypeLabelEstimateVo> carLabelTypeEstimateVos = handleCarTypeLabelEstimate(estimateId, orderParam,
                carTypeLabelEnable,
                carList,
                estimateResponses);

        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_CAR_TYPE_LABEL_PREFIX + estimateId,
                JSONUtil.toJsonStr(carLabelTypeEstimateVos),
                CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);

        // 处理车型联动问题

        List<CacheCarTypeLabelEstimateResult> labels = handleLabels(carLabelTypeEstimateVos);

        // 获取预估价中返回的第一个预估里程和时间不为空的
        // 按车型级别整理到各自的数组里面
        Integer estimateDistance = 0, estimateTime = 0;
        JSONArray carArray = JSONUtil.parseArray(result.get("rows"));
        if (null != carArray && carArray.size() > 0) {
            for (int i = 0; i < carArray.size(); i++) {
                JSONObject carObject = (JSONObject) carArray.get(i);
                if (carObject.containsKey("estimateDistance") && null != carObject.get("estimateDistance")) {
                    estimateDistance = carObject.getBigDecimal("estimateDistance").multiply(new BigDecimal(1000))
                            .intValue();
                }
                if (carObject.containsKey("estimateTravelTime") && null != carObject.get("estimateTravelTime")) {
                    estimateTime = carObject.getBigDecimal("estimateTravelTime").multiply(new BigDecimal(60))
                            .intValue();
                }

                if (estimateDistance.compareTo(0) == 1 && estimateTime.compareTo(0) == 1) {
                    orderParam.setDistance(estimateDistance);
                    orderParam.setEstimateTime(estimateTime);
                    break;
                }
            }
        }

        orderParam.setEstimateExpireTime(DateUtil.offsetMinute(new Date(), 3));
        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + estimateId, orderParam,
                CacheConsts.ORDER_CACHE_EXPIRE_TIME);
        if (orderParam.getOrderId() == null) {
            redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId, JSONUtil.toJsonStr(carList),
                    CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);
        }

        setEstimateCarsIsCheck(carList, labels, sceneInfo);

        Map<String, Object> map = new HashMap<>();
        map.put("scene", sceneInfo.getScene());
        map.put("cars", carList);
        map.put("labels", labels);
        map.put("estimateId", estimateId);
        if (orderParam.getPreDepartApplyId() != null) {
            map.put("preDepartApplyId", orderParam.getPreDepartApplyId());
        }

        int carMode = 0;
        String val = cacheService.getDispatchMode(orderParam.getCompanyId());
        if (StrUtil.isNotEmpty(val)) {
            try {
                JSONObject paraJsonObject = JSONUtil.parseObj(val);
                if (null != paraJsonObject) {
                    if (paraJsonObject.getBool("enabled", false)) {
                        if (redisUtil.hashHasKey(CacheConsts.REDIS_KEY_USER_DISPATCH_MODE,
                                String.valueOf(orderParam.getUserId()))) {
                            carMode = (int) redisUtil.hashGet(CacheConsts.REDIS_KEY_USER_DISPATCH_MODE,
                                    String.valueOf(orderParam.getUserId()));
                            carMode = carMode == 0 ? 1 : carMode;// 此处判断一下，保证企业开启后，不返回0
                        } else {
                            carMode = 1;
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("处理预估出现异常【OrderService->estimate,para:{}】", val, ex);
            }
        }

        map.put("carMode", carMode);

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        activityLog.setMappingId(estimateId);
        logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(map));

        return map;
    }

    private void handleSourceName(List<CacheEstimateResult> carList) {
        if (ObjectUtil.isNotEmpty(carList)) {
            List<CarSourceVo> sourceInfo = carTypeLabelService.getSourceInfo();
            List<CarLevelVo> carLevelInfo = carTypeLabelService.getCarLevelInfo();
            if (ObjectUtil.isNotEmpty(sourceInfo) && ObjectUtil.isNotEmpty(carLevelInfo)) {
                for (CacheEstimateResult cacheEstimateResult : carList) {
                    for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                        try {
                            CarSourceVo carSourceVo = sourceInfo.stream().filter(item -> StrUtil
                                    .equals(item.getSourceCode(), StrUtil.toString(estimateCar.getCarSourceId())))
                                    .findAny()
                                    .orElse(null);
                            if (ObjectUtil.isNotEmpty(carSourceVo)) {
                                CarLevelVo carLevelVo = carLevelInfo.stream()
                                        .filter(item -> StrUtil.equals(StrUtil.toString(carSourceVo.getSupplierId()),
                                                item.getSupplierId())
                                                && StrUtil.equals(item.getCarTypeCode(), estimateCar.getSubCarType()))
                                        .findAny().orElse(null);
                                if (ObjectUtil.isNotEmpty(carLevelVo)) {
                                    estimateCar.setCarSource(StrUtil.format("{}{}", carSourceVo.getSourceName(),
                                            carLevelVo.getCarTypeName()));
                                }
                            }
                        } catch (Exception e) {
                            log.error("变更运力名称信息失败: ", e);
                        }

                    }
                }
            }
        }
    }

    private List<CacheCarTypeLabelEstimateResult> handleLabels(Set<CarTypeLabelEstimateVo> carLabelTypeEstimateVos) {
        List<CacheCarTypeLabelEstimateResult> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(carLabelTypeEstimateVos)) {
            CacheCarTypeLabelEstimateResult object = new CacheCarTypeLabelEstimateResult();
            object.setDynamicCode(snowFlakeUtil.getNextId().toString());
            object.setNameCn("推荐");
            object.setNameEn("recommend");
            object.setData(carLabelTypeEstimateVos);
            result.add(object);
        }
        return result;
    }

    /**
     * 处理标签车型预估信息
     *
     * @param orderParam 预估参数
     * @param carList    中台预估结果解析后的数据
     * @return
     */
    private Set<CarTypeLabelEstimateVo> handleCarTypeLabelEstimate(String estimateId, CreateOrderParam orderParam,
            List<CarTypeLabelVo> carTypeLabelEnable,
            List<CacheEstimateResult> carList, List<TemplateVo> estimateResponses) {
        Set<CarTypeLabelEstimateVo> result = null;
        try {

            boolean show = showCarLabel(estimateId, orderParam);
            if (show) {
                result = carTypeLabelService.getEstimateResponseNew(orderParam, carTypeLabelEnable, estimateResponses,
                        carList);
            } else {
                log.info("用户符合升舱条件, 不显示标签");
            }
        } catch (Exception e) {
            log.info("处理车型标签预估异常: ", e);
        }

        return result;
    }

    private boolean showCarLabel(String estimateId, CreateOrderParam orderParam) {
        try {
            UpgradeDto upgradeDto = new UpgradeDto();
            upgradeDto.setUserId(orderParam.getUserId());
            upgradeDto.setCompanyId(orderParam.getCompanyId());
            BaseResponse baseResponse = systemService.getUserUpgrade(upgradeDto);
            if (baseResponse.getCode() == 0) {
                JSONObject jsonObject = new JSONObject(baseResponse.getData());
                Boolean isUpgrade = jsonObject.getBool("isUpgrade");
                if (isUpgrade) {
                    log.info("预估id: {}, 用户符合升舱条件", estimateId);
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            log.info("处理车型标签预估异常: ", e);
        }
        return false;
    }

    /**
     * 下单
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> placeOrder(CreateOrderParam orderParam, Boolean isCheckPrePay, Long prePayOrderId)
            throws Exception {

        String traceId = IdUtil.simpleUUID();
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                InterfaceEnum.ORDER_PLACEORDER, traceId);
        activityLog.setMappingId(orderParam.getEstimateId());
        activityLog.setBody(JSONUtil.toJsonStr(orderParam));
        try {
            checkOrderBasicParam(orderParam);
            // 判断当前用户是否有进行中的订单 add by syt 2023-03-9
            boolean existsRunningOrders = orderLimitService.existsRunningOrders(orderParam.getCompanyId(),
                    orderParam.getUserId());
            if (existsRunningOrders) {// 下单限制的客户，每次登录最多下一单，且进行中的订单只有一笔，目前通过缓存获取进行中的订单
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveWarningLogAsync(activityLog, "当前存在进行中的订单，不可再次下单，请先取消！");
                throw new BusinessException("当前存在进行中的订单，不可再次下单，请先取消！");
            }

            // 判断重复叫车
            if (orderParam.getConfirm() == null || !orderParam.getConfirm()) {
                // todo 重复叫车
            }

            if (orderParam.getEstimateId() == null) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveWarningLogAsync(activityLog, "estimateId参数不能为空");
                throw new BusinessException("estimateId参数不能为空");
            }
            if (orderParam.getCars() == null) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveWarningLogAsync(activityLog, "cars参数不能为空");
                throw new BusinessException("cars参数不能为空");
            }
            if (orderParam.getPassengerPhone() == null || !Validator.isMobile(orderParam.getPassengerPhone())) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveWarningLogAsync(activityLog, "手机号不正确");
                throw new BusinessException("手机号不正确");
            }
            // 默认立即用车
            if (orderParam.getDepartTime() == null) {
                orderParam.setDepartTime(new Date());
            }

            // h5客户跳转时携带的自定义参数，海格开始标准化
            this.setOrderParamByCustomInfo(orderParam.getCompanyId(), orderParam.getUserId(), orderParam);

            // 是否存在未支付订单
            if (orderLimitService.isOpenUnpaidOrderCompanys(orderParam.getCompanyId())) {
                boolean existUnpaid = userService.existUnpaid(orderParam.getUserId(), orderParam.getCompanyId());
                log.info("placeOrder===>当前存在未支付的订单{}", existUnpaid);
                if (existUnpaid) {
                    long endTime = System.currentTimeMillis();
                    activityLog.setResMillsecond(endTime - startTime);
                    logService.saveWarningLogAsync(activityLog, "当前存在未支付的订单，不可再次下单，请先支付！");
                    throw new BusinessException("当前存在未支付的订单，不可再次下单，请先支付！");
                }
            }

            // 从缓存中获取预估参数，将预估距离和预估时间同步过来 add by hz 2023-01-28
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId())) {
                CreateOrderParam cacheOrderParam = (CreateOrderParam) redisUtil
                        .get(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId());
                if (cacheOrderParam != null) {
                    orderParam.setDistance(cacheOrderParam.getDistance());
                    orderParam.setEstimateTime(cacheOrderParam.getEstimateTime());
                }
            }

            int maxCarLevel = 0;// 用户选中的最高车型
            List<Long> couponIds = new ArrayList<>();// 预估使用的优惠券
            List<SelectedCar> upgradeCars = new ArrayList<>();// 用户选中的最高车型对应的车型数据
            BigDecimal maxEstimatePrice = BigDecimal.ZERO;

            for (SelectedCar car : orderParam.getCars()) {
                if (car.getCouponId() != null && car.getCouponId() != 0 && !couponIds.contains(car.getCouponId())) {
                    couponIds.add(car.getCouponId());
                }

                if (maxEstimatePrice.compareTo(car.getEstimatePrice()) == -1) {
                    maxEstimatePrice = car.getEstimatePrice();
                }
                // 获取选中最高的carLevel，免费升舱用
                if (maxCarLevel == CarLevel.LUXURY.getCode()) {
                    upgradeCars.clear();
                    continue;
                }

                if (maxCarLevel < car.getCarLevel()) {
                    upgradeCars.clear();
                    maxCarLevel = car.getCarLevel();
                    upgradeCars.add(car);
                } else if (maxCarLevel == car.getCarLevel()) {
                    upgradeCars.add(car);
                }
            }
            if (couponIds.size() > 0) {
                if (orderParam.getExtraParameter() == null) {
                    orderParam.setExtraParameter(new OrderExtraParameter());
                }

                JSONObject couponIdObject = new JSONObject();
                couponIdObject.set("couponIds", couponIds);

                if (orderParam.getExtraParameter().getCustomInfo() == null) {
                    orderParam.getExtraParameter().setCustomInfo(couponIdObject);
                } else {
                    orderParam.getExtraParameter().getCustomInfo().set("couponIds", couponIds);
                }
            }

            BigDecimal extraFee = BigDecimal.ZERO;
            if (orderParam.getExtraServices() != null && orderParam.getExtraServices().size() != 0) {
                extraFee = getExtralFee(orderParam.getCompanyId(), orderParam.getExtraServices());
            }

            // 调用.net接口检查订单参数
            maxEstimatePrice = maxEstimatePrice.add(extraFee);
            BigDecimal frozenAmount = maxEstimatePrice.setScale(2, RoundingMode.HALF_UP);
            RequestCheckOrderDto requestCheckOrderDto = new RequestCheckOrderDto();
            BeanUtil.copyProperties(orderParam, requestCheckOrderDto, false);
            // 20240604 法本下单时所属项目和所属部门验证添加
            if (ObjectUtil.isNotEmpty(orderParam.getExtraParameter())
                    && ObjectUtil.isNotEmpty(orderParam.getExtraParameter().getCustomInfo())) {
                JSONObject jsonObject = new JSONObject(orderParam.getExtraParameter().getCustomInfo());
                Object customCarInfoObj = JSONUtil.getByPath(jsonObject, "customCarInfo", null);
                if (ObjectUtil.isNotEmpty(customCarInfoObj)) {
                    String customCarInfo = JSONUtil.toJsonStr(customCarInfoObj);
                    requestCheckOrderDto.setCustomInfo(customCarInfo);
                }
            }
            requestCheckOrderDto.setFrozenAmount(frozenAmount);
            requestCheckOrderDto.setCheckType((short) 2);

            // 获取合作方管控配置
            RegulationBo regulationConfig = regulationService.getRegulationConfig();
            // 是否开启由合作方管控配置
            boolean isPartnerRegulation = regulationConfig.getPartnerRegulationCompanyList()
                    .contains(orderParam.getCompanyId());
            BaseResponse response = null;
            // 如果企业没开启自定义条件限制直接返回
            if (orderLimitService.isOpenOrderLimitConfig(orderParam.getCompanyId())) {
                // 自定义限制条件参数拼装
                this.setOrderParamByOrderLimit(requestCheckOrderDto, orderParam);
                response = orderValidationFeign.checkOrder(requestCheckOrderDto);
            } else if (isPartnerRegulation) {
                List<PartnerRegulationCompanyConfig> prcConfigList = regulationConfig
                        .getPartnerRegulationCompanyCofigList();
                PartnerRegulationCompanyConfig config = prcConfigList.stream()
                        .filter(o -> o.getCompanyId().equals(orderParam.getCompanyId())).collect(Collectors.toList())
                        .get(0);
                CdsMstDto cdsMstDto = new CdsMstDto();
                cdsMstDto.setOrderParam(orderParam);
                cdsMstDto.setCheckOrderParam(requestCheckOrderDto);
                cdsMstDto.setCustomer(config.getCustomer());
                response = cdsMstSerice.createOrder(cdsMstDto, orderParam);
                JSONObject msgJSONObject = new JSONObject(response.getData());
                String supplyOrderId = msgJSONObject.getStr("supplyOrderId");
                orderParam.setPartnerOrderId(supplyOrderId);
            } else {
                response = configurationService.checkOrder(requestCheckOrderDto, traceId);
                if (response.getCode() == 1009 || response.getCode() == 1010 || response.getCode() == 1011) {
                    throw new CustomException(response.getCode(), response.getMessage());
                }
            }

            if (response.getCode() != 0) {
                if (null != requestCheckOrderDto.getAllowExcess() && requestCheckOrderDto.getAllowExcess()) {
                    // 根据编码判断是否提示
                    boolean isError = regulationService.isErrorByCode(orderParam, response.getCode());
                    if (isError) {
                        throw new CustomException(response.getCode(), response.getMessage());
                    }
                } else {
                    if (response.getCode() == 3 && ("用户未设置账户,暂时无法用车.".equals(response.getMessage())
                            || "The user has not set up an account and is temporarily unable to use the vehicle."
                                    .equals(response.getMessage()))) {
                        log.info("checkOrder 无默认账户，也可以下单，结算时，传企业默认账户:{}", response.getMessage());
                        // 获取默认账户填充账户新
                        Long accountId = billService.getDefaultAccountId(orderParam.getCompanyId());
                        orderParam.setAccountId(accountId);
                        billService.insertAccountUser(orderParam.getCompanyId(), orderParam.getUserId(), accountId);
                    } else {
                        if (response.getCode() == 20001) {
                            long endTime = System.currentTimeMillis();
                            activityLog.setResMillsecond(endTime - startTime);
                            logService.saveWarningLogAsync(activityLog, response.getMessage());
                            throw new CustomException(response.getCode(), response.getMessage());
                        }

                        long endTime = System.currentTimeMillis();
                        activityLog.setResMillsecond(endTime - startTime);
                        logService.saveWarningLogAsync(activityLog, response.getMessage());
                        throw new BusinessException(response.getMessage());
                    }
                }
            }

            // 此处处理一下个人单次额度
            BigDecimal onceAmount = BigDecimal.ZERO;
            if (response.getData() != null) {
                JSONObject extralObject = new JSONObject(response.getData());
                if (extralObject != null && extralObject.containsKey("onceAmount")) {
                    onceAmount = extralObject.getBigDecimal("onceAmount");
                }
            }

            Object cacheEstimateResultList = redisUtil
                    .get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + orderParam.getEstimateId());
            if (null == cacheEstimateResultList) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveWarningLogAsync(activityLog, "预估已失效，请重新预估");
                throw new BusinessException("预估已失效，请重新预估");
            }
            List<CacheEstimateResult> estimateResultList = JSONUtil.toList(cacheEstimateResultList.toString(),
                    CacheEstimateResult.class);

            // 判断公司是否开启大额预付设置 add by syt 2023-03-9
            boolean isPrepay = cacheService.isPrepay(orderParam.getCompanyId());
            if (isCheckPrePay && isPrepay) {
                // 预估参数缓存
                CreateOrderParam estimateParam = null;
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId())) {
                    estimateParam = (CreateOrderParam) redisUtil
                            .get(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId());
                }

                if (estimateParam == null) {
                    long endTime = System.currentTimeMillis();
                    activityLog.setResMillsecond(endTime - startTime);
                    logService.saveWarningLogAsync(activityLog, "预估已失效，请重新预估");
                    throw new BusinessException("预估已失效，请重新预估");
                }

                Long orderId = snowFlakeUtil.getNextId();
                BigDecimal prePaySettingAmount = cacheService.getPrepayAmount(orderParam.getCompanyId());
                BigDecimal prePayAmount = BigDecimal.ZERO;// 预付金额

                for (SelectedCar car : orderParam.getCars()) {
                    for (CacheEstimateResult cacheEstimateResult : estimateResultList) {
                        if (cacheEstimateResult.getId() == car.getCarLevel()) {
                            ReimModel reimModel = cacheEstimateResult.getReimModel();
                            BigDecimal individualAmount = BigDecimal.ZERO;
                            if (car.getEstimatePrice().compareTo(BigDecimal.ZERO) > 0) {
                                if (reimModel != null) {
                                    if (reimModel.getReimModel() == (short) 1) {// 按金额报销
                                        if (car.getEstimatePrice().compareTo(reimModel.getValue()) > 0) {
                                            individualAmount = car.getEstimatePrice()
                                                    .subtract(reimModel.getValue());
                                        }
                                    } else {// 按比例报销
                                        individualAmount = car.getEstimatePrice()
                                                .multiply(new BigDecimal(100)
                                                        .subtract(reimModel.getValue() == null ? BigDecimal.ZERO
                                                                : reimModel.getValue()))
                                                .divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                                    }
                                }
                            }

                            if (prePayAmount.compareTo(individualAmount) < 0) {
                                prePayAmount = individualAmount;
                            }
                            break;
                        }
                    }
                }

                if (prePayAmount.compareTo(prePaySettingAmount) >= 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("prePayFlag", true);
                    map.put("amount", prePayAmount);
                    if (orderParam.getServiceType() == (short) 2) {
                        Date deadLine = DateUtil.offsetMinute(orderParam.getDepartTime(), -31);
                        Long minute = DateUtil.between(new Date(), deadLine, DateUnit.MINUTE, false);
                        if (minute.compareTo(15L) >= 0) {
                            map.put("time", DateUtil.format(DateUtil.offsetMinute(new Date(), 15),
                                    "yyyy/MM/dd HH:mm:ss"));
                        } else {
                            map.put("time",
                                    DateUtil.format(DateUtil.offsetMinute(new Date(), minute.intValue()),
                                            "yyyy/MM/dd HH:mm:ss"));
                        }
                    } else {
                        map.put("time",
                                DateUtil.format(DateUtil.offsetMinute(new Date(), 15), "yyyy/MM/dd HH:mm:ss"));
                    }
                    map.put("orderId", String.valueOf(orderId));
                    if (orderParam.getPreDepartApplyId() != null) {
                        map.put("preDepartApplyId", orderParam.getPreDepartApplyId());
                    }

                    redisUtil.set(CacheConsts.REDIS_KEY_PREPAY_ORDER_PREFIX + orderId, prePayAmount,
                            CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);

                    // 根据订单类型设置缓存过期时间
                    if (orderParam.getServiceType() == 1) {

                    } else if (orderParam.getServiceType() == 2) {

                    } else {

                    }

                    if (estimateParam != null) {
                        estimateParam.setCars(orderParam.getCars());
                        estimateParam.setPrePayFlag(true);
                        estimateParam.setPrePayAmount(prePayAmount);
                        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId, estimateParam,
                                CacheConsts.ORDER_CACHE_EXPIRE_TIME);

                        orderParam.setPrePayFlag(true);
                        orderParam.setPrePayAmount(prePayAmount);
                        orderParam.setEstimateExpireTime(estimateParam.getEstimateExpireTime());
                        redisUtil.set(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId, orderParam,
                                CacheConsts.ORDER_CACHE_EXPIRE_TIME);
                    }

                    return map;
                }
            }

            for (SelectedCar selectedCar : orderParam.getCars()) {
                selectedCar.setActualEstimatePrice(selectedCar.getEstimatePrice());

                for (CacheEstimateResult result : estimateResultList) {
                    for (EstimateCar estimateCar : result.getList()) {
                        if (estimateCar.getDynamicCode().equals(selectedCar.getDynamicCode())) {
                            selectedCar.setEstimateDistance(estimateCar.getEstimateDistance());
                            selectedCar.setEstimateTime(estimateCar.getEstimateTime());
                            break;
                        }
                    }
                }

                if (selectedCar.getCouponId() != null && selectedCar.getCouponAmount() != null
                        && selectedCar.getCouponAmount().compareTo(BigDecimal.ZERO) == 1) {
                    for (CacheEstimateResult result : estimateResultList) {
                        for (EstimateCar estimateCar : result.getList()) {
                            if (estimateCar.getDynamicCode().equals(selectedCar.getDynamicCode())) {
                                selectedCar.setActualEstimatePrice(estimateCar.getActualEstimatePrice());
                                break;
                            }
                        }
                    }
                }
            }

            if (orderParam.getExtraParameter() == null) {
                orderParam.setExtraParameter(new OrderExtraParameter());
            }

            if (isPartnerRegulation) {
                List<SelectedCar> collect1 = orderParam.getCars().stream().map(o -> {
                    for (CacheEstimateResult result : estimateResultList) {
                        for (EstimateCar estimateCar : result.getList()) {
                            if (estimateCar.getDynamicCode().equals(o.getDynamicCode())) {
                                o.setSelfPayingRate(estimateCar.getSelfPayingRate());
                                o.setSelfPayingAmount(estimateCar.getSelfPayingAmount());
                                o.setSelfPayingUpgradeCarType(estimateCar.getSelfPayingUpgradeCarType());
                                break;
                            }
                        }
                    }
                    return o;
                }).collect(Collectors.toList());
                orderParam.setCars(collect1);
            }

            // 将选择的车型加到customInfo中
            if (orderParam.getExtraParameter().getCustomInfo() == null) {
                JSONObject selectedCarObject = new JSONObject();
                selectedCarObject.set("selectedCars", orderParam.getCars());
                orderParam.getExtraParameter().setCustomInfo(selectedCarObject);
            } else {
                orderParam.getExtraParameter().getCustomInfo().set("selectedCars", orderParam.getCars());
            }

            if (orderParam.getExtraServices() != null && orderParam.getExtraServices().size() > 0) {
                orderParam.getExtraParameter().getCustomInfo().set("extraServices", orderParam.getExtraServices());
            }

            if (onceAmount.compareTo(BigDecimal.ZERO) == 1) {// 将此次下单的单次金额记录下来
                orderParam.getExtraParameter().getCustomInfo().set("onceAmount", onceAmount);
            }

            String value = amgenPoSceneMapper.selectPoAndCostCenterBySceneId(orderParam.getSceneId());
            if (StringUtils.isNotEmpty(value) && value.contains("|")) {
                int firstIndex = value.indexOf("|");
                if (firstIndex > 0 && firstIndex != value.length() - 1) {
                    orderParam.getExtraParameter().getCustomInfo().set("poCode", value.substring(0, firstIndex));
                    orderParam.getExtraParameter().getCustomInfo().set("costCenterName",
                            value.substring(firstIndex + 1));
                }
            }

            // 判断是否公司是否开启免费升舱 add by syt 2023-03-9
            boolean isOpenUpgradeCarLevlCompanys = cacheService.isOpenUpgrade(orderParam.getCompanyId());

            // 判断是否开启免费升舱权限,如果开启，不管是否升舱成功，信息都要记录下来，供以后查询
            List<SelectedCar> copySelectedCars = new ArrayList<>();// 用来记录升舱前选择的车型，如果是升舱订单，调用中台后，将选择的车型替换为原来值
            boolean isMatchUpgradeCarLevl = false;// 是否有升舱车型
            int delayTime = 0;// vip免费升舱延时秒数
            int callType = 1; // 是否延时呼叫 1-否 2-是
            String sort = null;// 延时呼叫顺序

            // 双选司机 升舱判断

            // 获取下单时 选择的增值服务
            List<ExtraService> extraServices = orderParam.getExtraServices();

            // 判断是否为双选司机
            boolean isUserSelectionDrivers = false;
            if (CollectionUtils.isNotEmpty(extraServices)) {
                List<ExtraService> collect = extraServices.stream().filter(
                        o -> StringUtils.equals(o.getCode(), "ES0008"))
                        .collect(Collectors.toList());
                isUserSelectionDrivers = CollectionUtils.isNotEmpty(collect);
            }

            // 双选司机的订单 不走升舱逻辑
            if (!isUserSelectionDrivers && isOpenUpgradeCarLevlCompanys && maxCarLevel != CarLevel.LUXURY.getCode()
                    && upgradeCars.size() > 0) {
                // 获取免费升舱配置信息
                List<UpgradeParam> carList = new ArrayList<>();
                UpgradeDto upgradeDto = new UpgradeDto();
                upgradeDto.setUserId(orderParam.getUserId());
                upgradeDto.setCompanyId(orderParam.getCompanyId());
                upgradeDto.setUpgradeCarLevel(maxCarLevel);
                for (SelectedCar car : upgradeCars) {
                    UpgradeParam upgradeParam = new UpgradeParam();
                    upgradeParam.setCarSourceId(car.getCarSourceId());
                    upgradeParam.setEstimatePrice(car.getActualEstimatePrice());
                    carList.add(upgradeParam);
                }
                upgradeDto.setCars(carList);

                BaseResponse baseResponse = systemService.getUpgrade(upgradeDto);
                if (baseResponse.getCode() == 0) {
                    JSONObject jsonObject = new JSONObject(baseResponse.getData());
                    if (jsonObject.getBool("isUpgrade")) {// 可以免费升舱
                        // 获取vip配置项中的免费升舱 add by huzhen 2024/06/11 start
                        try {
                            Map<String, Object> delaySeting = cacheService
                                    .getUpgradeDelaySetting(orderParam.getCompanyId());
                            if (null != delaySeting) {
                                if (delaySeting.containsKey("calltype")) {
                                    callType = Integer.valueOf(delaySeting.get("calltype").toString());
                                }
                                if (delaySeting.containsKey("delaytime")) {
                                    delayTime = Integer.valueOf(delaySeting.get("delaytime").toString());
                                }
                                if (delaySeting.containsKey("calltype")) {
                                    sort = delaySeting.get("sort").toString();
                                }
                            }
                        } catch (Exception ex) {
                            // 日志上报
                            log.error("处理下单[获取vip免费升舱配置]出现异常【OrderService->placeOrder,para:{}】",
                                    JSONUtil.toJsonStr(upgradeDto), ex);
                        }
                        // 获取vip配置项中的免费升舱 add by huzhen 2024/06/11 end

                        // 获取升舱前车型名称
                        String oldLevelName = CarLevel.getName(maxCarLevel);

                        int upgradeCarLevel = jsonObject.getInt("upgradeCarLevel");
                        BigDecimal maxDiscountAmount = jsonObject.getBigDecimal("maxDiscountAmount");
                        List<Integer> carSources = jsonObject.getBeanList("carSources", Integer.class);

                        // 从缓存获取预估车型结果
                        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + orderParam.getEstimateId())) {
                            copySelectedCars.addAll(orderParam.getCars());

                            // 将更高一级车型加到下单选择的cars里
                            for (CacheEstimateResult cacheEstimateResult : estimateResultList) {
                                if (cacheEstimateResult.getId() == upgradeCarLevel) {

                                    List<UpgradeParam> upgradeParamList = new ArrayList<>();

                                    List<EstimateCar> cars = cacheEstimateResult.getList();
                                    for (EstimateCar estimateCar : cars) {
                                        if (carSources.contains(estimateCar.getCarSourceId())) {
                                            SelectedCar selectedCar = new SelectedCar();
                                            BeanUtil.copyProperties(estimateCar, selectedCar, "couponId",
                                                    "couponAmount");
                                            selectedCar.setUpgradeValue(1);
                                            selectedCar.setNewCarLevelName(oldLevelName);
                                            orderParam.getCars().add(selectedCar);

                                            UpgradeParam upgradeParam = new UpgradeParam();
                                            upgradeParam.setEstimatePrice(estimateCar.getActualEstimatePrice());
                                            upgradeParam.setCarSourceId(estimateCar.getCarSourceId());
                                            upgradeParamList.add(upgradeParam);
                                        }
                                    }

                                    if (upgradeParamList.size() > 0) {
                                        isMatchUpgradeCarLevl = true;
                                        JSONObject upgradeObject = new JSONObject();

                                        LinkedHashMap<String, Object> upgradeMap = new LinkedHashMap<>();
                                        upgradeMap.put("isUpgradeSuccess", false);
                                        upgradeMap.put("upgradeCarLevel", upgradeCarLevel);
                                        upgradeMap.put("maxDiscountAmount", maxDiscountAmount);
                                        upgradeMap.put("upgradeCars", upgradeParamList);
                                        upgradeObject.set("upgrade", upgradeMap);

                                        if (orderParam.getExtraParameter() == null) {
                                            orderParam.setExtraParameter(new OrderExtraParameter());
                                        }
                                        if (orderParam.getExtraParameter().getCustomInfo() == null) {
                                            orderParam.getExtraParameter().setCustomInfo(upgradeObject);
                                        } else {
                                            orderParam.getExtraParameter().getCustomInfo().set("upgrade", upgradeMap);
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        // 如果未匹配到升舱车型，并且原始预估价也已经缓存，检测原始预估价是否存在满足条件的升舱 2024-04-21 add by huzhen
                        if (!isMatchUpgradeCarLevl && redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_ORIGINAL_PREFIX
                                + String.valueOf(orderParam.getEstimateId()))) {
                            JSONArray originalEstimateJsonArray = JSONUtil
                                    .parseArray(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_ORIGINAL_PREFIX
                                            + String.valueOf(orderParam.getEstimateId())));
                            if (null != originalEstimateJsonArray && originalEstimateJsonArray.size() > 0) {
                                List<UpgradeParam> upgradeParamList = new ArrayList<>();

                                for (int i = 0; i < originalEstimateJsonArray.size(); i++) {

                                    EstimatePriceResult estimatePriceResult = JSONUtil.toBean(
                                            originalEstimateJsonArray.getJSONObject(i),
                                            EstimatePriceResult.class);
                                    if (null == estimatePriceResult)
                                        continue;

                                    if (estimatePriceResult.getCarType() == upgradeCarLevel) {
                                        if (carSources.contains(estimatePriceResult.getCarSource())) {
                                            SelectedCar selectedCar = new SelectedCar();
                                            selectedCar.setDynamicCode(estimatePriceResult.getDynamicCode());
                                            selectedCar.setEstimateTime(estimatePriceResult.getEstimateTravelTime());
                                            selectedCar.setCarSourceId(estimatePriceResult.getCarSource());
                                            selectedCar.setEstimateDistance(estimatePriceResult.getEstimateDistance());
                                            selectedCar.setCarLevel(estimatePriceResult.getCarType());
                                            selectedCar.setEstimatePrice(estimatePriceResult.getEstimatePrice());
                                            selectedCar.setCarLevelName(estimatePriceResult.getCarTypeName());
                                            selectedCar.setActualEstimatePrice(estimatePriceResult.getEstimatePrice());
                                            selectedCar.setUpgradeValue(1);
                                            selectedCar.setNewCarLevelName(oldLevelName);
                                            orderParam.getCars().add(selectedCar);

                                            UpgradeParam upgradeParam = new UpgradeParam();
                                            upgradeParam.setEstimatePrice(estimatePriceResult.getEstimatePrice());
                                            upgradeParam.setCarSourceId(estimatePriceResult.getCarSource());
                                            upgradeParamList.add(upgradeParam);
                                        }
                                    }
                                }

                                if (upgradeParamList.size() > 0) {
                                    isMatchUpgradeCarLevl = true;
                                    JSONObject upgradeObject = new JSONObject();

                                    JSONObject upgradeJSONObject = new JSONObject();
                                    upgradeJSONObject.set("isUpgradeSuccess", false);
                                    upgradeJSONObject.set("upgradeCarLevel", upgradeCarLevel);
                                    upgradeJSONObject.set("maxDiscountAmount", maxDiscountAmount);
                                    upgradeJSONObject.set("upgradeCars", upgradeParamList);
                                    upgradeObject.set("upgrade", upgradeJSONObject);

                                    if (orderParam.getExtraParameter() == null) {
                                        orderParam.setExtraParameter(new OrderExtraParameter());
                                    }
                                    if (orderParam.getExtraParameter().getCustomInfo() == null) {
                                        orderParam.getExtraParameter().setCustomInfo(upgradeObject);
                                    } else {
                                        orderParam.getExtraParameter().getCustomInfo().set("upgrade",
                                                upgradeJSONObject);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.setCustomInfoRecommendedLocation(orderParam);

            // base表
            Long orderId = orderServiceImpl.makeOrder(orderParam, extraFee, prePayOrderId); // 调用启用事务的内部函数，不能用this调用

            // 如果orderId对应的预估参数缓存和下单参数缓存不存在的话
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId()) &&
                    !redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId)) {
                CreateOrderParam newEstimateParam = (CreateOrderParam) redisUtil
                        .get(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderParam.getEstimateId());
                newEstimateParam.setCars(orderParam.getCars());
                redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId, newEstimateParam,
                        CacheConsts.ORDER_CACHE_EXPIRE_TIME);

                if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId)) {
                    redisUtil.set(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId, orderParam,
                            CacheConsts.ORDER_CACHE_EXPIRE_TIME);
                }
            }

            orderParam.setOrderId(orderId);
            // begin 针对南航的特殊处理逻辑
            if (IPATH_SPECIAL_CUSTOMER != null && orderParam.getCompanyId().equals(IPATH_SPECIAL_CUSTOMER)) {
                for (SelectedCar selectedCar : orderParam.getCars()) {
                    if ("普通优选".equals(selectedCar.getCarLevelName())
                            && selectedCar.getCarLevel() == CarLevel.COMFORTABLE.getCode()) {
                        selectedCar.setCarLevel(CarLevel.NORMAL.getCode());
                    }
                }
            }
            // end

            // 调用core接口下单
            RequestPlaceOrderDto requestPlaceOrderDto = new RequestPlaceOrderDto();
            BeanUtil.copyProperties(orderParam, requestPlaceOrderDto);
            requestPlaceOrderDto.setTraceId(traceId);
            requestPlaceOrderDto.setPartnerOrderId(String.valueOf(orderParam.getOrderId()));
            requestPlaceOrderDto.setUserAI(true); // 是否智能下单 (文档上没有写)
            if (orderParam.getServiceType() == 6) { // 6 接机
                requestPlaceOrderDto.setFlightDelayTime(10); // 航班到达后多少分钟出发，只能取10-90之间，10的整数倍数字
            }

            // vip免费升舱配置，如果配置了延时策略，需要将参数告诉中台 add by huzhen 2024/06/11 start
            if (callType == 2 && StrUtil.isNotEmpty(sort) && delayTime > 0) {
                String[] sortArray = sort.split(",");
                if (null != sortArray && sortArray.length > 0) {
                    int index = 0;
                    for (int i = 0; i < sortArray.length; i++) {
                        Integer carLevelId = Integer.valueOf(sortArray[i]);
                        List<SelectedCar> cars = requestPlaceOrderDto.getCars().stream()
                                .filter(t -> t.getCarLevel().equals(carLevelId)).collect(Collectors.toList());
                        if (null != cars && cars.size() > 0) {
                            for (SelectedCar sc : cars) {
                                sc.setDelay(index * delayTime);
                            }
                            index++;
                        }
                    }
                }
            }
            // vip免费升舱配置，如果配置了延时策略，需要将参数告诉中台 add by huzhen 2024/06/11 end

            // 代叫车并没有标识，由于缓存逻辑不完整 此处通过缓存获取用户手机号和下单传过来的乘客手机号码做比较，如果不一致，再查库 我赌测试不会把手机号改成代叫车人电话
            // 然后乘客电话写自己的手机号
            boolean closedReplaceBooking = false;
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_CLOSE_REPLACE_BOOKING_PREFIX
                    + String.valueOf(orderParam.getCompanyId()))) {
                closedReplaceBooking = Boolean.parseBoolean(
                        String.valueOf(redisUtil.get(CacheConsts.REDIS_KEY_ORDER_CLOSE_REPLACE_BOOKING_PREFIX
                                + String.valueOf(orderParam.getCompanyId()))));
            }
            if (!closedReplaceBooking) {
                boolean isLoadUserPhone = false;
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_USER_INFO + orderParam.getUserId())) {
                    String phone = String
                            .valueOf(redisUtil.get(CacheConsts.REDIS_KEY_USER_INFO + orderParam.getUserId()));
                    if (!requestPlaceOrderDto.getPassengerPhone().equals(phone)) {
                        isLoadUserPhone = true;
                    } else {
                        requestPlaceOrderDto.setUserPhone(phone);
                    }
                } else {
                    isLoadUserPhone = true;
                }
                if (isLoadUserPhone) {
                    UserBase userBase = userBaseMapper.selectByPrimaryKey(orderParam.getUserId());
                    if (null != userBase && StrUtil.isNotEmpty(userBase.getPhone())) {
                        redisUtil.set(CacheConsts.REDIS_KEY_USER_INFO + orderParam.getUserId(), userBase.getPhone());
                        requestPlaceOrderDto.setUserPhone(userBase.getPhone());
                    }
                }
            }

            /**
             *
             * 途经点判断
             * 只向首汽下单，将来要是支持的途径点多的话，此逻辑就要去掉了
             */
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX
                    + String.valueOf(orderParam.getEstimateId()))) {
                List<SelectedCar> cacheCars = JSONUtil
                        .toList(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX
                                + String.valueOf(orderParam.getEstimateId())).toString(), SelectedCar.class);
                List<Integer> carLevels = new ArrayList<>();

                for (SelectedCar sc : requestPlaceOrderDto.getCars()) {
                    Long cnt = cacheCars.stream().filter(t -> t.getCarLevel() == sc.getCarLevel()).count();
                    if (cnt > 0 && !carLevels.contains(sc.getCarLevel()))
                        carLevels.add(sc.getCarLevel());
                }

                if (carLevels.size() == 0) {
                    long endTime = System.currentTimeMillis();
                    activityLog.setResMillsecond(endTime - startTime);
                    logService.saveWarningLogAsync(activityLog, "当前选择的车型不支持途经点");
                    throw new BusinessException("当前选择的车型不支持途经点");
                }

                List<SelectedCar> cars = cacheCars.stream().filter(t -> carLevels.contains(t.getCarLevel()))
                        .collect(Collectors.toList());
                requestPlaceOrderDto.setCars(cars);

                // 根据订单id再缓存一份，供追加车型时使用
                redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX + String.valueOf(orderId),
                        JSONUtil.toJsonStr(cacheCars), CacheConsts.ONE_HOUR);
            }

            if (redisUtil.hasKey(
                    CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + String.valueOf(orderParam.getEstimateId()))) {
                JSONArray jsonArray = JSONUtil
                        .parseArray(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX
                                + String.valueOf(orderParam.getEstimateId())).toString());
                requestPlaceOrderDto.setPassingPoints(jsonArray);
            }

            // add by kakarotto 平台取消重新下单需求 添加下面参数后方可重新派单
            // 重新派单参数start
            List<RequestPlaceOrderExtraParamsDto> extraParams = requestPlaceOrderDto.getExtraParams();
            if (CollectionUtils.isEmpty(extraParams)) {
                extraParams = new ArrayList<>();
            }

            // 保存预估订单id,用于重新派单
            redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_ORDERID_PREFIX + orderId, orderParam.getEstimateId(), 162L);
            // 重新派单参数end

            // 勾选绿色出行
            Boolean isTickedGreenTravel = orderParam.getExtraParameter().getCustomInfo().getBool("isTickedGreenTravel");
            extraParams.add(this.getExtraParams(CoreOrderConstant.GREEN_TRAVEL,
                    (null != isTickedGreenTravel && isTickedGreenTravel) ? "true" : "false"));

            // 判断预约管家
            boolean isBooking = false;
            if (CollectionUtils.isNotEmpty(extraServices)) {
                List<ExtraService> collect = extraServices.stream().filter(
                        o -> StringUtils.equals(o.getCode(), "ES0005") || StringUtils.equals(o.getCode(), "ES0006"))
                        .collect(Collectors.toList());
                isBooking = CollectionUtils.isNotEmpty(collect);
            }

            if (isUserSelectionDrivers) {
                extraParams.add(this.getExtraParams(CoreOrderConstant.USER_SELECTION_DRIVERS, "true"));
            }

            // 获取用户十分钟内取消的车牌号
            String cancelVehicleno = userService.getCancelVehicleno(orderParam.getUserId());
            if (StringUtils.isNotBlank(cancelVehicleno) && !isBooking) {
                extraParams.add(this.getExtraParams(CoreOrderConstant.EXCLUDE_VEHICLE_NO, cancelVehicleno));
                redisUtil.set(OrderConstant.ORDER_IS_RE_PLACE_ORDER + orderId, "", CacheConsts.ONE_WEEK);
                log.info("获取用户十分钟内取消的车牌号===>[orderId:{},userId:{},cancelVehicleno:{}]", orderParam.getOrderId(),
                        orderParam.getUserId(), cancelVehicleno);
            }
            // 判断是否开启重新派单
            if (rePlaceOrderAfterCoreCancelSwitch && !isBooking) {
                redisUtil.set(OrderConstant.ORDER_IS_OPEN_RE_PLACE_ORDER + orderId, "", CacheConsts.ONE_WEEK);
            }
            // 判断重新下单参数
            if ((rePlaceOrderAfterCoreCancelSwitch || StringUtils.isNotBlank(cancelVehicleno)) && !isBooking) {
                extraParams.add(this.getExtraParams(CoreOrderConstant.RE_PLACE_ORDER, "true"));
            }

            // vip 全平台握手配置 add by huzhen 2024/06/11 start
            try {

                boolean handshake = cacheService.isOpenHandshake(orderParam.getCompanyId());
                if (handshake) {
                    extraParams.add(this.getExtraParams(CoreOrderConstant.JOIN_NON_HANDSHAKE_DRIVERS, "true"));
                }
            } catch (Exception ex) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveErrorLogAsync(activityLog, "获取vip全平台握手配置出现异常");
                log.error("处理下单[获取vip全平台握手配置]出现异常【OrderService->placeOrder,para:{}】",
                        orderParam.getCompanyId(), ex);
            }
            // vip 全平台握手配置 add by huzhen 2024/06/11 end

            // 只允许新能源车型接单
            boolean isOnlyAllowGreenTravel = orderLimitService.isOnlyAllowGreenTravel(orderParam.getCompanyId());
            if (isOnlyAllowGreenTravel) {
                extraParams.add(this.getExtraParams(CoreOrderConstant.GREEN_ONLY, "true"));
            }

            if (null != orderParam.getExtraParameter().getCustomInfo()) {
                int carMode = orderParam.getExtraParameter().getCustomInfo().getInt("carMode", 1);

                extraParams.add(this.getExtraParams(CoreOrderConstant.ORDER_TAKEN_MODE, String.valueOf(carMode)));

                int thresholdTime = cacheService.getThresholdTimeOfMultiDriverTakeOrder(orderParam.getCompanyId());
                if (thresholdTime > 0) {
                    extraParams.add(this.getExtraParams(CoreOrderConstant.THRESHOLD_TIME,
                            String.valueOf(thresholdTime * 1000)));
                }

                redisUtil.hashPut(CacheConsts.REDIS_KEY_USER_DISPATCH_MODE, String.valueOf(orderParam.getUserId()),
                        carMode);
            } else {
                if (orderParam.getServiceType() == 1) {
                    extraParams.add(this.getExtraParams(CoreOrderConstant.ORDER_TAKEN_MODE, "2"));
                } else {
                    extraParams.add(this.getExtraParams(CoreOrderConstant.ORDER_TAKEN_MODE, "1"));
                }
            }

            requestPlaceOrderDto.setExtraParams(extraParams);

            // 需求:极速派单调度 下单
            // 需求:极速派单调度 update by syt,20250320 需求变更，不需要延时。需求ID: 1000241,需求名：
            // 调度费派单逻辑优化。需求创建人：马启洋
            // log.info("订单号:{},需求:极速派单调度 下单,开始===>", orderParam.getOrderId());
            String coreOrderId = dispatchService.placeOrder(requestPlaceOrderDto, orderParam);
            // log.info("订单号:{},需求:极速派单调度 下单,结束<===,结果:返回coreOrderId：{}",
            // orderParam.getOrderId(), coreOrderId);
            if (StringUtils.isBlank(coreOrderId)) {
                response = coreOrderService.placeOrder(requestPlaceOrderDto, orderParam);
                JSONObject dataObject = new JSONObject(response.getData());
                coreOrderId = dataObject.getStr("coreOrderId");
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
            } else {
                Log infoLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(),
                        orderParam.getOrderId(),
                        InterfaceEnum.INFO_DISPATCH, traceId);
                logService.saveLogAsync(infoLog, "触发极速派单逻辑");
            }

            // 属性浅拷贝
            // begin 针对南航的特殊处理逻辑，以后要删掉的 2022-08-05 10:07 add by huzhen
            if (IPATH_SPECIAL_CUSTOMER != null && orderParam.getCompanyId().equals(IPATH_SPECIAL_CUSTOMER)) {
                for (SelectedCar selectedCar : orderParam.getCars()) {
                    if ("普通优选".equals(selectedCar.getCarLevelName())
                            && selectedCar.getCarLevel() == CarLevel.NORMAL.getCode()) {
                        selectedCar.setCarLevel(CarLevel.COMFORTABLE.getCode());
                    }
                }
            }
            // end

            // source表
            OrderSource orderSource = new OrderSource();
            orderSource.setId(snowFlakeUtil.getNextId());
            orderSource.setUserId(orderParam.getUserId());
            orderSource.setOrderId(orderId);
            orderSource.setCoreOrderId(coreOrderId);
            orderSource.setState((short) 1);
            orderSource.setEstimatePrice(maxEstimatePrice);
            orderSource.setIpathEstimatePrice(maxEstimatePrice);
            orderSource.setPlatformEstimatePrice(maxEstimatePrice);
            orderSource.setCreateTime(new Date());
            orderSource.setUpdateTime(new Date());
            orderSource.setEstimateDistance(orderParam.getDistance());
            orderSource.setEstimateTime(orderParam.getEstimateTime());
            // 缓存order_source信息 add by kakarotto 20230331 fix_下单后查询不到订单信息
            redisUtil.set(CacheConsts.REDIS_KEY_ORDER_SOURCE + orderId, orderSource,
                    CacheConsts.TEMP_CACHE_EXPIRE_TIME.longValue());
            orderSourceMapper.insertSelective(orderSource);

            // 调用system-service更新行前申请单占用状态
            if (orderParam.getPreDepartApplyId() != null) {
                RequestUsageStateDto requestUsageStateDto = new RequestUsageStateDto();
                requestUsageStateDto.setState(1);
                requestUsageStateDto.setOrderId(orderId);
                requestUsageStateDto.setCompanyId(orderParam.getCompanyId());
                requestUsageStateDto.setUsageTime(new Date());
                requestUsageStateDto.setUsageMoney(frozenAmount);
                requestUsageStateDto.setPreDepartApplyId(orderParam.getPreDepartApplyId());
                systemService.updatePreDepartApplyState(requestUsageStateDto);
            }

            if (copySelectedCars.size() > 0) {// 如果触发免费升舱，将选择的车型替换为原来的值
                orderParam.setCars(copySelectedCars);

                if (isMatchUpgradeCarLevl) {// 存在满足升舱的车型
                    UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                    updateUsageCountDto.setUserId(orderParam.getUserId());
                    updateUsageCountDto.setCompanyId(orderParam.getCompanyId());
                    updateUsageCountDto.setAccountId(orderParam.getAccountId());
                    updateUsageCountDto.setUseUpgrade(true);
                    updateUsageCountDto.setOrderId(orderId);
                    updateUsageCountDto.setRecordDesc("placeOrder");
                    updateUsageCountDto
                            .setOrderTime(orderParam.getDepartTime() == null ? new Date() : orderParam.getDepartTime());
                    systemService.updateUsageCount(updateUsageCountDto);
                }
            }

            // 存缓存
            addOrderCache(orderId, coreOrderId, orderParam);

            // 异步处理：保存最近订单、历史地址、保存用户手机号、通知其他服务
            orderTask.asyncPostProcessPlaceOrder(orderParam, frozenAmount, orderSource);
            
            String chengCheCanHuiRen = null;
            if (orderParam.getExtraParameter() != null
                    && orderParam.getExtraParameter().getCustomInfo() != null
                    && !"".equals(orderParam.getExtraParameter().getCustomInfo())) {
                JSONObject jsonObject = orderParam.getExtraParameter().getCustomInfo();
                if (jsonObject.containsKey("customCarInfo") && jsonObject.get("customCarInfo") != null) {
                    JSONArray jsonArray = jsonObject.getJSONArray("customCarInfo");
                    if (jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            Map<String, String> map1 = new HashMap<>();
                            JSONObject jsonObject1 = new JSONObject(jsonArray.get(i));
                            if (jsonObject1.containsKey("name") && jsonObject1.get("name") != null
                                    && jsonObject1.getStr("name").equals("乘车参会人")) {
                                if (jsonObject1.containsKey("value") && jsonObject1.get("value") != null) {
                                    chengCheCanHuiRen = jsonObject1.getStr("value");
                                }
                            }
                        }
                    }
                }
            }
            // 保存最近乘车人
            if (StringUtils.equals(active, "dev") || StringUtils.equals(active, "test")
                    || StringUtils.equals(active, "uat")
                    || StringUtils.equals(active, "anjintest") || StringUtils.equals(active, "anjinprod")) {
                if (StringUtils.isNotBlank(chengCheCanHuiRen)) {
                    userService.saveUserPassenger(orderParam.getUserId(), chengCheCanHuiRen);
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("orderId", String.valueOf(orderId));
            map.put("coreOrderId", coreOrderId);
            if (orderParam.getPreDepartApplyId() != null) {
                map.put("preDepartApplyId", orderParam.getPreDepartApplyId());
            }

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            activityLog.setOrderId(orderId);
            logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(map));

            return map;
        } catch (CustomException e) {
            log.info("异常: ", e);
            if (prePayOrderId != null && prePayOrderId > 0) {
                notifyBillService(prePayOrderId);
            }

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw e;
        } catch (BusinessException e) {
            log.info("异常: ", e);
            if (prePayOrderId != null && prePayOrderId > 0) {
                notifyBillService(prePayOrderId);
            }
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            logService.saveErrorLogAsync(activityLog, e);

            throw e;
        } catch (Exception exception) {
            log.info("异常: ", exception);
            if (prePayOrderId != null && prePayOrderId > 0) {
                notifyBillService(prePayOrderId);
            }
            // throw new RuntimeException(exception.getMessage());
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, exception);
            throw new RuntimeException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
    }

    private void setCustomInfoRecommendedLocation(CreateOrderParam orderParam) {

        // 【推荐上车点埋点 add by kakarotto 20240523】1.下单判断是否推荐上车点
        boolean recommendedLocation = this.recommendedLocation(orderParam.getCompanyId(), orderParam.getUserId(),
                orderParam.getDepartLng(), orderParam.getDepartLat(),
                orderParam.getPickupLocationName(),
                orderParam.getPickupLocation());

        if (recommendedLocation) {
            if (orderParam.getExtraParameter() == null) {
                orderParam.setExtraParameter(new OrderExtraParameter());
            }
            if (orderParam.getExtraParameter().getCustomInfo() == null) {
                JSONObject customInfo = new JSONObject();
                customInfo.set("recommendedLocation", true);
                orderParam.getExtraParameter().setCustomInfo(customInfo);
            } else {
                JSONObject customInfo = orderParam.getExtraParameter().getCustomInfo();
                customInfo.set("recommendedLocation", true);
                orderParam.getExtraParameter().setCustomInfo(customInfo);
            }
        }
        log.info(JSONUtil.toJsonStr(orderParam.getExtraParameter()));
    }

    public RequestPlaceOrderExtraParamsDto getExtraParams(String param, String value) {
        RequestPlaceOrderExtraParamsDto requestPlaceOrderExtraParamsDto = new RequestPlaceOrderExtraParamsDto();
        requestPlaceOrderExtraParamsDto.setParam(param);
        requestPlaceOrderExtraParamsDto.setValue(value);
        return requestPlaceOrderExtraParamsDto;
    }

    private EstimateCar getPassingPointCarSource(Long orderId, int carLevel) {
        List<EstimateCar> passingPointCarSourceList = null;
        // 如果为空，取首次下单时缓存的支持途经点运力的预估结果
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX + orderId)) {
            passingPointCarSourceList = JSONUtil.toList(
                    JSONUtil.toJsonStr(
                            redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX + orderId)),
                    EstimateCar.class);
        }
        if (null == passingPointCarSourceList)
            return null;

        EstimateCar targetEstimateCar = null;
        for (EstimateCar ec : passingPointCarSourceList) {
            if (ec.getCarLevel() == carLevel)
                return ec;
        }

        // 暂时先不考虑无对应车型的首汽
        return null;
    }

    private void addOrderCache(Long orderId, String coreOrderId, CreateOrderParam orderParam) throws Exception {
        // 以中台订单号为key缓存订单信息
        CacheICarOrder cacheICarOrder = new CacheICarOrder();
        cacheICarOrder.setOrderId(orderId);
        cacheICarOrder.setCoreOrderId(coreOrderId);
        cacheICarOrder.setUserId(orderParam.getUserId());
        cacheICarOrder.setState((short) 1); // 等待司机接单
        cacheICarOrder.setNeedMonitor(false);

        // 用车时间距离现在的秒数，缓存时间为用车时间+ORDER_CACHE_EXPIRE_TIME
        long betweenSecond = DateUtil.between(new Date(), orderParam.getDepartTime(), DateUnit.SECOND);
        boolean redisResult = redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId, cacheICarOrder,
                betweenSecond + CacheConsts.ORDER_CACHE_EXPIRE_TIME.longValue());
        if (!redisResult) {
            log.error("addOrderCache ==> write icar order cache failed, orderId=" + orderId);
        }

        // 以用户id为key缓存订单信息
        CacheOrder cacheOrder = new CacheOrder();
        BeanUtil.copyProperties(orderParam, cacheOrder, false);
        cacheOrder.setOrderId(orderId);
        cacheOrder.setCoreOrderId(coreOrderId);
        cacheOrder.setState((short) 1); // 等待司机接单
        if (orderParam.getServiceType() != 20)
            cacheOrder.setCallingCars(getCallingCars(orderParam.getEstimateId(), orderParam.getCars()));
        try {
            cacheOrder.setCallingCarTypeLabelCars(
                    getCallingCarTypeLabelCars(orderParam.getEstimateId(), orderParam.getCars()));
        } catch (Exception e) {
            log.info("标签车型异常 ==> ", e);
        }
        // log.error("orderCache ==> 写入缓存_01,userId={},
        // orderId={},selectedCars={},,callingCars={}", orderParam.getUserId(), orderId,
        // orderParam.getCars(), cacheOrder.getCallingCars());

        // 结算所需参数
        if (orderParam.getExtraServices() != null) {
            List<Long> extraServiceList = new ArrayList<>();
            List<String> extraServiceCodeList = new ArrayList<>();
            for (ExtraService extraService : orderParam.getExtraServices()) {
                extraServiceList.add(extraService.getId());
                extraServiceCodeList.add(extraService.getCode());
            }

            if (extraServiceList.size() > 0) {
                cacheOrder.setExtraServiceIds(extraServiceList);
            }

            if (extraServiceCodeList.size() > 0) {
                cacheOrder.setExtraServiceCodes(extraServiceCodeList);
            }
        }

        if (orderParam.getCouponId() != null) {
            List<Long> couponList = new ArrayList<Long>();
            couponList.add(orderParam.getCouponId());
            cacheOrder.setCouponIds(couponList);
        }
        cacheOrder.setCityCode(orderParam.getDepartCityCode());
        cacheOrder.setCityName(orderParam.getDepartCityName());
        cacheOrder.setCityId(123456L);

        // add by guoxin
        Map<String, String> preApply = new HashMap<>();
        if (orderParam.getPreDepartApplyId() != null) {
            preApply.put("preDepartApplyId", orderParam.getPreDepartApplyId().toString());
        } else {
            preApply.put("preDepartApplyId", "");
        }
        cacheOrder.setPreApply(preApply);
        // end

        long currentExpire = redisUtil
                .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(orderParam.getUserId()));
        long newExpire = max(currentExpire, betweenSecond + CacheConsts.ORDER_CACHE_EXPIRE_TIME.longValue());
        // log.info("addOrderCache ==> orderId={}, currentExpire={}, newExpire={}",
        // orderId, currentExpire, newExpire);
        redisResult = redisUtil.hashPut(
                CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(orderParam.getUserId()),
                String.valueOf(orderId), JSONUtil.toJsonStr(cacheOrder), newExpire);
        if (!redisResult) {
            log.error("addOrderCache ==> write order cache failed, orderId=" + orderId);
        }
    }

    // 生成本地订单表记录
    @Transactional(rollbackFor = Exception.class)
    public Long makeOrder(CreateOrderParam orderParam, BigDecimal extraFee, Long prePayOrderId) throws Exception {

        long beginTimeMillis = System.currentTimeMillis();
        Long procDefId = null;
        Boolean isNeedApproval = null;
        Boolean isLoadFromDB = false;

        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_SCENE_INFO_PREFIX + orderParam.getSceneId().toString())) {
            SceneBaseInfo scene = (SceneBaseInfo) redisUtil
                    .get(CacheConsts.REDIS_KEY_SCENE_INFO_PREFIX + orderParam.getSceneId().toString());
            if (null != scene) {
                Short approvalType = scene.getApprovalType();
                if (null != approvalType && approvalType == (short) 3) {
                    JSONObject comSceneCustomInfo = new JSONObject(scene.getCustomInfo());
                    Long approvalId = comSceneCustomInfo.getLong("approvalId");
                    procDefId = approvalId;
                } else {
                    procDefId = scene.getWfReProcdefId();
                }
                isNeedApproval = scene.getIsNeedApprove();
                orderParam.setSceneCode(scene.getSceneCode());
            } else {
                isLoadFromDB = true;
            }
        } else {
            isLoadFromDB = true;
        }

        if (isLoadFromDB) {
            ComScene comScene = comSceneMapper.selectByPrimaryKey(orderParam.getSceneId());
            if (null != comScene) {
                if (comScene.getApprovalType() == (short) 3) {
                    JSONObject comSceneCustomInfo = new JSONObject(comScene.getCustomInfo());
                    Long approvalId = comSceneCustomInfo.getLong("approvalId");
                    procDefId = approvalId;
                } else {
                    procDefId = comScene.getWfReProcdefId();
                }
                isNeedApproval = comScene.getIsNeedApproval();
                orderParam.setSceneCode(comScene.getCode());
            }

            // 日志上报
        }

        CacheUserInfo cacheUserInfo = cacheService.getUserInfo(orderParam.getCompanyId(), orderParam.getUserId());

        String userPhone = (null == cacheUserInfo ? orderParam.getPassengerPhone() : cacheUserInfo.getPhone());
        orderParam.setUserPhone(userPhone);

        orderParam.setNameCn((null == cacheUserInfo ? null : cacheUserInfo.getNameCn()));
        orderParam.setEmergencyPhone((null == cacheUserInfo ? null : cacheUserInfo.getEmergencyPhone()));

        // 订单
        OrderBase order = new OrderBase();
        BeanUtil.copyProperties(orderParam, order, false);
        order.setDepartAirportCode(orderParam.getTrainDep());
        order.setArrivalAirportCode(orderParam.getTrainArr());

        JSONObject customInfo = null;

        // 处理自定义信息
        if (orderParam.getExtraParameter() != null) {
            String useCarReason = orderParam.getExtraParameter().getUserCarReason();
            if (useCarReason != null && !useCarReason.isEmpty()) {
                order.setUseCarReason(useCarReason);
                orderParam.setUseCarReason(useCarReason);
            }

            customInfo = orderParam.getExtraParameter().getCustomInfo();
            if (customInfo == null) {
                customInfo = new JSONObject();
            } else {
                customInfo.entrySet().removeIf(entry -> StrUtil.isBlankIfStr(entry.getValue()));
            }

            // 设置上一次选择的成本中心
            String costCenterCode = customInfo.getStr("costCenterCode");
            String approver = customInfo.getStr("approver");
            if (StrUtil.isNotEmpty(costCenterCode) && StrUtil.isNotEmpty(approver)) {
                redisUtil.set(CacheConsts.REDIS_KEY_USER_LAST_COST_CENTER_INFO
                        + StrUtil.toString(orderParam.getUserId()) + ":" + costCenterCode, approver,
                        CacheConsts.ONE_WEEK);
            }

        }

        if (prePayOrderId != null && prePayOrderId > 0) {
            order.setId(prePayOrderId);
            if (customInfo == null) {
                customInfo = new JSONObject();
            }

            JSONArray prepayJsonArray = new JSONArray();
            JSONObject prepayJsonObject = new JSONObject();
            prepayJsonObject.set("isPrepay", true);
            prepayJsonObject.set("prepayAmount", orderParam.getPrePayAmount());
            prepayJsonArray.add(prepayJsonObject);
            customInfo.set("prepay", prepayJsonArray);
            order.setIsPrepay(true);
        } else {
            order.setId(snowFlakeUtil.getNextId());
        }

        if (orderParam.getEmergencyPhone() != null) {
            if (customInfo == null) {
                customInfo = new JSONObject();
            }
            customInfo.putByPath("emergencyPhone", orderParam.getEmergencyPhone());
        }

        if (extraFee != null && extraFee.compareTo(BigDecimal.ZERO) == 1) {
            if (customInfo == null) {
                customInfo = new JSONObject();
            }
            customInfo.putByPath("extraFee", extraFee);
        }

        if (customInfo != null) {
            order.setCustomInfo(customInfo.toString());
        }
        order.setState((short) 1);
        order.setWorkFlowId(procDefId);
        order.setIsNeedApprove(isNeedApproval);
        order.setUserPhone(userPhone);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setPartnerOrderId(orderParam.getPartnerOrderId());
        order.setPreDepartApplyCode(orderParam.getPreDepartApplyCode());
        if (order.getCompanyId().equals(189170856287536901L) ||
                order.getCompanyId().equals(177388879878293253L) ||
                order.getCompanyId().equals(176546131868649221L)) {// 中电建，下单时，用户表里的备注信息，保存到order表的remark中
            UserBase userBase = userBaseMapper.selectByPrimaryKey(orderParam.getUserId());
            if (userBase != null && userBase.getRemark() != null) {
                order.setRemark(userBase.getRemark());
            }
        }

        // 缓存order_base信息 add by kakarotto 20230331 fix_下单后查询不到订单信息
        redisUtil.set(CacheConsts.REDIS_KEY_ORDER_BASE + order.getId(), order,
                CacheConsts.TEMP_CACHE_EXPIRE_TIME.longValue());
        // 加密地址
        cipherService.addressEncryptOrderBase(order);

        // 会议相关
        if (ObjectUtil.isNotEmpty(orderParam.getMeetingId())) {
            // 添加会议订单对应关系
            OrderMeeting orderMeeting = new OrderMeeting(snowFlakeUtil.getNextId(), order.getId(),
                    orderParam.getMeetingId(), new Date());
            orderMeetingService.insertSelective(orderMeeting);
            if (customInfo == null) {
                customInfo = new JSONObject();
            }
            customInfo.set("meetingId", orderParam.getMeetingId());
        }
        // 金杜 选择的审批人id 放到自定义信息中
        // if (ObjectUtil.isNotEmpty(orderParam.getApproverId())){
        // if (customInfo == null) {
        // customInfo = new JSONObject();
        // }
        // customInfo.set("approverId",orderParam.getApproverId());
        // }

        // 嘉宝加价规则
        RedisCpol redisCpol = orderLimitService.getRedisCpol(orderParam.getCompanyId(), orderParam.getUserId());
        log.info("redisCpol: {}", JSONUtil.toJsonStr(redisCpol));
        if (ObjectUtil.isNotEmpty(redisCpol)) {
            RedisCpolRegulationInfo regulationInfo = redisCpol.getRegulationInfo();
            if (ObjectUtil.isNotEmpty(regulationInfo)) {
                IncreaseAmountRule increaseAmountRule = regulationInfo.getIncreaseAmountRule();
                // 将加价规则设置到 orderBase的custominfo中留在状态5时使用
                if (StrUtil.isEmpty(order.getCustomInfo())) {
                    customInfo = new JSONObject();
                } else {
                    customInfo = new JSONObject(order.getCustomInfo());
                }
                customInfo.set(IncreaseAmountConstant.INCREASE_AMOUNT_RULE, increaseAmountRule);
                order.setCustomInfo(customInfo.toString());
            }
        }

        if (redisUtil.hasKey(
                CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + String.valueOf(orderParam.getEstimateId()))) {

            JSONArray pointJsonArray = JSONUtil.parseArray(redisUtil.get(
                    CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + String.valueOf(orderParam.getEstimateId())));
            if (StrUtil.isEmpty(order.getCustomInfo())) {
                customInfo = new JSONObject();
            } else {
                customInfo = new JSONObject(order.getCustomInfo());
            }
            customInfo.set("isPassingPoint", true);
            customInfo.set("passingPoints", pointJsonArray);

            order.setCustomInfo(customInfo.toString());
        }

        // 需求:极速派单:初始化customInfo极速派单调度配置
        // dispatchService.initDispatchConfigForCustomInfo(order);

        orderBaseMapper.insertSelective(order);

        orderParam.setCustomInfo(order.getCustomInfo());

        // tracer.currentSpan().tag("makeOrder_write_orderbase",
        // String.valueOf(System.currentTimeMillis() - beginTimeMillis));
        beginTimeMillis = System.currentTimeMillis();

        // 添加附加服务记录
        if (orderParam.getExtraServices() != null) {
            for (ExtraService extraService : orderParam.getExtraServices()) {
                OrderExtraService orderExtraService = new OrderExtraService();
                orderExtraService.setId(snowFlakeUtil.getNextId());
                orderExtraService.setOrderId(order.getId());
                orderExtraService.setExtraServiceId(extraService.getId());
                orderExtraService.setCreateTime(new Date());
                orderExtraServiceMapper.insertSelective(orderExtraService);
            }
        }

        return order.getId();
    }

    /**
     * 获取第三方id
     *
     * @param companyId
     * @return
     */
    private String getPartnerOrderId(Long companyId, Long userId) throws Exception {
        String partnerOrderIdCode = orderLimitService.getPartnerOrderIdCode(companyId);
        String partnerOrderId = "";
        if (StringUtils.equals(partnerOrderIdCode, "jingDong")) {
            CacheCompanyInfo companyInfo = cacheService.getCompanyInfo(companyId);
            partnerOrderId = jingDongService.getJingDongOrderId(userId, companyInfo.getCorpId());
        } else {
            throw new BusinessException("获取第三方id，未找到指定配置代码");
        }
        return partnerOrderId;
    }

    /**
     * 从预估结果缓存中选出用户选中的车型平台信息
     */
    public List<CacheEstimateResult> getCallingCars(String estimateId, List<SelectedCar> selectedCars)
            throws Exception {

        if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId)) {
            throw new BusinessException("estimateId不正确");
        }

        // List<CacheEstimateResult> cacheEstimateResults = (List<CacheEstimateResult>)
        // redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId);
        List<CacheEstimateResult> cacheEstimateResults = JSONUtil.toList(
                redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId).toString(),
                CacheEstimateResult.class);
        List<CacheEstimateResult> carsArray = new ArrayList<>();
        if (cacheEstimateResults == null) {
            return carsArray;
        }
        for (CacheEstimateResult cacheEstimateResult : cacheEstimateResults) {

            Integer carLevel = cacheEstimateResult.getId();
            List<EstimateCar> estimateCars = cacheEstimateResult.getList();

            List<EstimateCar> newEstimateCars = new ArrayList<>();
            for (EstimateCar estimateCar : estimateCars) {
                for (SelectedCar car : selectedCars) {
                    if (estimateCar.getCarSourceId().equals(car.getCarSourceId())
                            && carLevel.equals(car.getCarLevel())
                            && StrUtil.equals(estimateCar.getLabelCode(),
                                    StrUtil.isEmpty(car.getLabelCode()) ? null : car.getLabelCode())
                            && estimateCar.getCarLevelName().equals(car.getCarLevelName())
                            && StrUtil.equals(estimateCar.getSubCarType(), car.getSubCarType())) {
                        newEstimateCars.add(estimateCar);
                    }
                }
            }
            if (newEstimateCars.size() > 0) {
                CacheEstimateResult newCacheEstimateResult = new CacheEstimateResult();
                BeanUtil.copyProperties(cacheEstimateResult, newCacheEstimateResult, false);
                newCacheEstimateResult.setList(newEstimateCars);
                carsArray.add(newCacheEstimateResult);
            }
        }

        return carsArray;
    }

    public List<CarTypeLabelEstimateVo> getCallingCarTypeLabelCars(String estimateId, List<SelectedCar> selectedCars)
            throws Exception {

        if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_CAR_TYPE_LABEL_PREFIX + estimateId)) {
            throw new BusinessException("estimateId不正确");
        }

        // List<CacheEstimateResult> cacheEstimateResults = (List<CacheEstimateResult>)
        // redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId);
        List<CarTypeLabelEstimateVo> carsArray = new ArrayList<>();
        Object estimate = redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_CAR_TYPE_LABEL_PREFIX + estimateId);
        if (ObjectUtil.isEmpty(estimate)) {
            return carsArray;
        }
        List<CarTypeLabelEstimateVo> cacheEstimateResults = JSONUtil.toList(
                JSONUtil.toJsonStr(estimate),
                CarTypeLabelEstimateVo.class);
        if (cacheEstimateResults == null) {
            return carsArray;
        }
        for (CarTypeLabelEstimateVo cacheEstimateResult : cacheEstimateResults) {

            List<EstimateCar> estimateCars = cacheEstimateResult.getList();

            List<EstimateCar> newEstimateCars = new ArrayList<>();
            for (EstimateCar estimateCar : estimateCars) {
                for (SelectedCar car : selectedCars) {

                    if (estimateCar.getCarSourceId().equals(car.getCarSourceId())
                            && StrUtil.equals(estimateCar.getSubCarType(), car.getSubCarType())
                            && StrUtil.equals(estimateCar.getLabelCode(), car.getLabelCode())
                            && StrUtil.equals(estimateCar.getCarLevelName(), car.getCarLevelName())
                    // && StrUtil.equals(estimateCar.getIpathCode(), car.getIpathCode())
                    ) {
                        newEstimateCars.add(estimateCar);
                    }

                }
            }
            if (newEstimateCars.size() > 0) {
                CarTypeLabelEstimateVo newCacheEstimateResult = new CarTypeLabelEstimateVo();
                BeanUtil.copyProperties(cacheEstimateResult, newCacheEstimateResult, false);
                newCacheEstimateResult.setList(newEstimateCars);
                carsArray.add(newCacheEstimateResult);
            }
        }
        if (ObjectUtil.isNotEmpty(carsArray)) {
            carsArray = carsArray.stream()
                    .distinct()
                    .map(item -> {
                        BigDecimal max = item.getList().stream()
                                .map(EstimateCar::getEstimatePrice)
                                .distinct()
                                .max(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);

                        BigDecimal min = item.getList().stream()
                                .map(EstimateCar::getEstimatePrice)
                                .distinct()
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);

                        List<EstimateCar> collect = item.getList().stream()
                                .sorted(Comparator.comparing(EstimateCar::getEstimatePrice))
                                .collect(Collectors.toList());
                        item.setList(collect);

                        item.setPriceLab(NumberUtil.equals(max, min) ? StrUtil.toString(max)
                                : StrUtil.format("{}-{}", min, max));
                        return item;
                    })
                    .sorted(Comparator.comparing((CarTypeLabelEstimateVo item) -> item.getList().stream()
                            .min(Comparator.comparing(EstimateCar::getEstimatePrice))
                            .map(EstimateCar::getEstimatePrice)
                            .orElse(BigDecimal.ZERO))
                            .thenComparing(CarTypeLabelEstimateVo::getLabelCode))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return carsArray;
    }

    /**
     * 将中台估价接口返回数据整理成前端所需的数据，并过滤掉价格过高的车型
     * excludeCars: 要排除掉的车型
     * reimModelList: 报销比例
     */
    public List<CacheEstimateResult> processICarEstimateResult(Long companyId, JSONObject result,
            List<SelectedCar> excludeCars, SceneInfo sceneInfo, Long userId,
            String estimateId, CreateOrderParam orderParam, String traceId)
            throws Exception {
        // 获取用户可用优惠券 add by guoxin
        List<CouponResult> availableCoupons = couponService.getUserCoupons(orderParam.getCompanyId(),
                orderParam.getUserId(), orderParam.getDepartTime(), orderParam.getDepartCityCode(), traceId);

        if (null != availableCoupons && availableCoupons.size() > 0) {
            availableCoupons.sort(Comparator.comparing(CouponResult::getParValue).reversed()
                    .thenComparing(CouponResult::getValidSeconds));// 排序
        }

        BigDecimal maxAmountPerOrder = sceneInfo.getScene().getMaxAmountPerOrder();
        BigDecimal availableAmount = sceneInfo.getScene().getAvailableAmount();
        maxAmountPerOrder = maxAmountPerOrder.min(availableAmount);
        maxAmountPerOrder = maxAmountPerOrder.divide(FROZEN_AMOUNT_RATE, 2, RoundingMode.HALF_UP); // 计算倍率，预估价*倍率

        // 按车型级别整理到各自的数组里面
        JSONArray carArray = JSONUtil.parseArray(result.get("rows"));

        Map<Integer, Object> carLevelMap = new HashMap<>();
        List<TaxFeeModel> taxFeeModelList = cacheService.getTaxFee(companyId);

        List<Integer> excludeSourceCodeList = null;// 白龙马运力切换，排除的车型code
        if (StrUtil.isNotEmpty(estimateId)) {
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_EXCLUDE_PREFIX + estimateId)) {
                excludeSourceCodeList = JSONUtil.toList(
                        redisUtil.get(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_EXCLUDE_PREFIX + estimateId).toString(),
                        Integer.class);
                redisUtil.delete(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_EXCLUDE_PREFIX + estimateId);
            }
        }

        if (null == excludeSourceCodeList)
            excludeSourceCodeList = new ArrayList<>();

        // 获取场景可用的最高车型，升舱时使用。例如场景只允许普通型，那么按照之前升舱逻辑，是无法升舱的。所以需要将舒适型也缓存起来，但还不能让前端看到========start
        if (null != sceneInfo.getAvailableCarLevels() && sceneInfo.getAvailableCarLevels().size() < 4) {
            boolean isOpenUpgrade = cacheService.isOpenUpgrade(companyId);
            if (isOpenUpgrade) {
                // 将预估价都缓存起来，缓存有效期5分钟
                redisUtil.set(
                        CacheConsts.REDIS_KEY_ESTIMATE_ORIGINAL_PREFIX
                                + (StrUtil.isNotEmpty(estimateId) ? estimateId : result.getStr("estimateId")),
                        JSONUtil.toJsonStr(carArray),
                        CacheConsts.FIEVE_MINUTE);
            }
        }

        // 获取城市映射标签
        List<CarTypeLabelCityMapping> cityMapping = carTypeLabelService
                .getCityCarTypeLabelEstimate(orderParam.getCompanyId());
        // 获取场景可用的最高车型，升舱时使用。例如场景只允许普通型，那么按照之前升舱逻辑，是无法升舱的。所以需要将舒适型也缓存起来，但还不能让前端看到========end

        for (int i = 0; i < carArray.size(); i++) {
            EstimatePriceResult estimatePriceResult = JSONUtil.toBean(carArray.getJSONObject(i),
                    EstimatePriceResult.class);

            // 城市验证过滤
            if (!carTypeLabelService.checkCityMapping(orderParam, estimatePriceResult, cityMapping)) {
                // log.info("城市校验不通过: 运力信息: carSource: {}, subCarType: {}",
                // estimatePriceResult.getCarSource(),
                // estimatePriceResult.getSubCarType());
                continue;
            }

            // 过滤价格过高的，maxAmountPerOrder小于0表示不限制
            if (maxAmountPerOrder.compareTo(BigDecimal.ZERO) > 0
                    && estimatePriceResult.getEstimatePrice().compareTo(maxAmountPerOrder) > 0) {
                continue;
            }

            Integer carLevel = estimatePriceResult.getCarType();
            Integer carSourceId = estimatePriceResult.getCarSource();
            String carLevelName = estimatePriceResult.getCarTypeName();
            String ipathCode = estimatePriceResult.getIpathCode();
            String subCarType = estimatePriceResult.getSubCarType();

            // begin 针对南航的特殊处理逻辑，以后要删掉的 2022-08-05 08:33 add by huzhen
            if (IPATH_SPECIAL_CUSTOMER != null && companyId.equals(IPATH_SPECIAL_CUSTOMER)) {
                if ("普通优选".equals(carLevelName) && carLevel == CarLevel.NORMAL.getCode()) {
                    carLevel = CarLevel.COMFORTABLE.getCode();
                }
            }
            // end

            // 过滤场景不允许的车型
            Boolean isMatchCarType = false;
            if (sceneInfo.getAvailableCarLevels().size() == 0) {
                isMatchCarType = true;
            } else {
                for (Integer avalibleCarLevel : sceneInfo.getAvailableCarLevels()) {
                    if (carLevel.equals(avalibleCarLevel)) {
                        isMatchCarType = true;
                        break;
                    }
                }
            }

            // 自定义限制车型 有自定义时候只用自定义 add by kakarotto 20230330
            RedisCpol redisCpol = orderLimitService.getRedisCpol(companyId, userId);
            RedisCpolRegulationInfo regulationInfo = redisCpol.getRegulationInfo();
            if (null != regulationInfo) {
                List<String> rideTypes = regulationInfo.getRideTypes();
                if (CollectionUtils.isNotEmpty(rideTypes)) {
                    if (null != carLevel && rideTypes.contains(carLevel.toString())) {
                        isMatchCarType = true;
                    } else {
                        isMatchCarType = false;
                    }
                }
            }
            if (!isMatchCarType) {
                continue;
            }

            // 过滤场景不允许的平台
            Boolean isMatchCarSource = false;
            List<Integer> availableCarSources = cacheService.getCarSource(orderParam.getCompanyId());
            if (availableCarSources == null || availableCarSources.size() == 0) {
                isMatchCarSource = true;
            } else {
                for (Integer avalibleCarSource : availableCarSources) {
                    if (carSourceId.equals(avalibleCarSource)) {
                        isMatchCarSource = true;
                        break;
                    }
                }
            }
            if (!isMatchCarSource) {
                continue;
            }

            // 过滤订单已选车型
            if (excludeCars != null) {
                Boolean isMatch = false;
                for (SelectedCar excludeCar : excludeCars) {
                    if (NumberUtil.equals(carLevel, excludeCar.getCarLevel()) &&
                            NumberUtil.equals(carSourceId, excludeCar.getCarSourceId()) &&
                            StrUtil.equals(subCarType, excludeCar.getSubCarType())) {
                        isMatch = true;
                        break;
                    }
                    // 过滤订单已选ipathCode
                    if (ObjectUtil.equals(carLevel, excludeCar.getCarLevel()) &&
                            StrUtil.equals(excludeCar.getIpathCode(), ipathCode)) {
                        isMatch = true;
                        break;
                    }
                }
                if (isMatch) {
                    continue;
                }
            }

            // 过滤订单已选车型
            if (excludeSourceCodeList != null) {
                Boolean isMatch = false;
                for (Integer excludeSourceCode : excludeSourceCodeList) {
                    if (excludeSourceCode.equals(carSourceId)) {
                        isMatch = true;
                        break;
                    }
                }
                if (isMatch) {
                    continue;
                }
            }

            // add by guoxin
            BigDecimal price = estimatePriceResult.getEstimatePrice();
            if (taxFeeModelList != null && taxFeeModelList.size() > 0) {
                for (int j = 0; j < taxFeeModelList.size(); j++) {
                    TaxFeeModel taxFeeModel = taxFeeModelList.get(j);
                    if (taxFeeModel != null && taxFeeModel.getFeeMode() != null) {
                        if (taxFeeModel.getFeeMode() == 1 && taxFeeModel.getMergeEstimate()) {
                            price = price.add(price.multiply(taxFeeModel.getValue()).divide(BigDecimal.valueOf(100), 2,
                                    RoundingMode.HALF_UP));
                        } else if (taxFeeModel.getFeeMode() == 2 && taxFeeModel.getMergeEstimate()) {
                            price = price.add(taxFeeModel.getValue());
                        }
                    }
                }
            }
            // end

            EstimateCar estimateCar = new EstimateCar();
            estimateCar.setActualEstimatePrice(estimatePriceResult.getEstimatePrice());
            estimateCar.setSelfPayingUpgradeCarType(estimatePriceResult.getSelfPayingUpgradeCarType());
            estimateCar.setSelfPayingRate(estimatePriceResult.getSelfPayingRate());
            estimateCar.setSelfPayingAmount(estimatePriceResult.getSelfPayingAmount());
            // 此处统一处理预估价折扣 京东需求20230613
            JSONObject jsonObject = dataUtil.getEstimateDiscount(companyId);
            if (null != jsonObject && jsonObject.containsKey("discount")) {
                BigDecimal discount = jsonObject.getBigDecimal("discount");
                BigDecimal limitAmount = BigDecimal.ZERO;
                if (jsonObject.containsKey("limitAmount")) {
                    limitAmount = jsonObject.getBigDecimal("limitAmount");
                }

                if (price.compareTo(limitAmount) == 1) {
                    price = price.multiply(discount).setScale(2, RoundingMode.HALF_UP);
                }
            }

            // 获取公司预估价折扣率的配置
            price = getDiscountPrice(price, companyId, false);
            estimateCar.setSubCarType(estimatePriceResult.getSubCarType());
            estimateCar.setEstimatePrice(price);
            estimateCar.setDynamicCode(estimatePriceResult.getDynamicCode());
            estimateCar.setCarSource(estimatePriceResult.getCarSourceName()); // 运力平台名称
            estimateCar.setCarSourceImg(estimatePriceResult.getCarSourceImg());
            estimateCar.setCarSourceId(carSourceId);
            estimateCar.setCarLevel(carLevel);
            estimateCar.setCarLevelName(estimatePriceResult.getCarTypeName());
            estimateCar.setEstimateDistance(estimatePriceResult.getEstimateDistance());
            estimateCar.setEstimateTime(estimatePriceResult.getEstimateTravelTime());
            estimateCar.setIpathCode(estimatePriceResult.getIpathCode());
            estimateCar.setIsFixed(
                    ObjectUtil.isNotEmpty(estimatePriceResult.getIsFixed()) ? estimatePriceResult.getIsFixed() : false);

            if (availableCoupons != null && availableCoupons.size() > 0) {
                for (int index = 0; index < availableCoupons.size(); index++) {
                    CouponResult couponResult = availableCoupons.get(index);
                    if (couponResult.getCarLevelList() != null
                            && couponResult.getCarLevelList().size() > 0
                            && !couponResult.getCarLevelList().contains(carLevel.toString())) {
                        continue;
                    }

                    if (couponResult.getCarSourceList() != null && couponResult.getCarSourceList().size() > 0) {
                        if ("contain".equals(couponResult.getCarMode())) {
                            if (!couponResult.getCarSourceList().contains(carSourceId.toString())) {
                                continue;
                            }
                        } else if ("exclude".equals(couponResult.getCarMode())) {
                            if (couponResult.getCarSourceList().contains(carSourceId.toString())) {
                                continue;
                            }
                        }
                    }

                    if (couponResult.getThreshold() == null ||
                            couponResult.getThreshold().compareTo(BigDecimal.ZERO) == 0 ||
                            estimatePriceResult.getEstimatePrice().compareTo(couponResult.getThreshold()) >= 0) {
                        estimateCar.setCouponId(couponResult.getId());
                        estimateCar.setCouponAmount(couponResult.getParValue());

                        if (couponResult.getParValue().compareTo(estimateCar.getEstimatePrice()) > 0) {
                            estimateCar.setEstimatePrice(BigDecimal.ZERO);
                        } else {
                            estimateCar.setEstimatePrice(
                                    estimateCar.getEstimatePrice().subtract(estimateCar.getCouponAmount()));
                        }

                        break;
                    }
                }
            } else {// 如果没有，给默认值
                estimateCar.setCouponId("0");
                estimateCar.setCouponAmount(BigDecimal.ZERO);
            }

            // 按照车等级分组统计
            if (carLevelMap.containsKey(carLevel)) {
                CacheEstimateResult cacheEstimateResult = (CacheEstimateResult) carLevelMap.get(carLevel);
                List<EstimateCar> estimateCars = cacheEstimateResult.getList();

                // begin 如果ipathcode相同，缓存中配置的运力code优先 2023-11-13
                boolean isAdd = true;

                if (StrUtil.isNotEmpty(estimatePriceResult.getIpathCode()) &&
                        estimateCars.stream()
                                .anyMatch(t -> estimatePriceResult.getIpathCode().equals(t.getIpathCode()))) {
                    Integer preCarSourceId = 0;
                    // 虽然这一步没什么用，还是留着吧
                    List<EstimateCar> preEstimateCars = estimateCars.stream()
                            .filter(t -> estimatePriceResult.getIpathCode().equals(t.getIpathCode()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(preEstimateCars) &&
                            !estimatePriceResult.getCarSource().equals(preEstimateCars.get(0).getCarSourceId())) {
                        preCarSourceId = preEstimateCars.get(0).getCarSourceId();

                        List<Integer> priorCodeList = cacheService.getPriorSource();

                        if (preCarSourceId > 0 && CollectionUtils.isNotEmpty(priorCodeList)) {
                            if ((priorCodeList.contains(carSourceId) && priorCodeList.contains(preCarSourceId)) ||
                                    (!priorCodeList.contains(carSourceId) && !priorCodeList.contains(preCarSourceId))) { // 如果都是优选运力,或者都不是优选运力，选择价格低的
                                isAdd = false;
                                if (preEstimateCars.get(0).getEstimatePrice()
                                        .compareTo(estimateCar.getEstimatePrice()) > 0) {
                                    excludeSourceCodeList.add(preEstimateCars.get(0).getCarSourceId());
                                    BeanUtil.copyProperties(estimateCar, preEstimateCars.get(0));
                                }
                            } else if (priorCodeList.contains(carSourceId)) { // 如果当前是优选运力，覆盖原有选择车型
                                isAdd = false;
                                excludeSourceCodeList.add(preEstimateCars.get(0).getCarSourceId());
                                BeanUtil.copyProperties(estimateCar, preEstimateCars.get(0));
                            } else if (priorCodeList.contains(preCarSourceId)) { // 如果之前的是优选运力
                                isAdd = false;
                                excludeSourceCodeList.add(carSourceId);
                            }
                        }
                    }
                }
                // end 如果ipathcode相同，缓存中配置的运力code优先 2023-11-13

                if (isAdd)
                    estimateCars.add(estimateCar);
            } else {
                CacheEstimateResult cacheEstimateResult = new CacheEstimateResult();
                cacheEstimateResult.setId(estimateCar.getCarLevel()); // 车型level
                String name = estimateCar.getCarLevelName();

                if ("普通优选".equals(estimateCar.getCarLevelName())) {
                    if (estimateCar.getCarLevel() == CarLevel.COMFORTABLE.getCode()) {
                        name = IPATH_CAR_LEVEL_NAME;
                    } else {
                        name = "普通";
                    }
                }
                cacheEstimateResult.setNameCn(name); // 车型level中文名
                cacheEstimateResult.setNameEn(name); // 车型level英文名
                // 支付策略描述
                for (ReimModel reimModel : sceneInfo.getReimModel()) {
                    Integer reimcarLevel = reimModel.getCarLevel();
                    if (carLevel.equals(reimcarLevel)) {
                        BigDecimal value = reimModel.getValue();
                        // carMap.put("reimbursementCn", estimateResultVo.getCarLevelName() + "型报销" +
                        // String.valueOf(value) + ((model==1)?"元":"%") ); // 混合支付策略中文描述
                        cacheEstimateResult.setReimbursementCn(
                                "报销" + value.toString() + ((reimModel.getReimModel() == 1) ? "元" : "%")); // 混合支付策略中文描述
                        cacheEstimateResult.setReimbursementEn("reimbursement " + String.valueOf(value)); // todo
                        cacheEstimateResult.setReimModel(reimModel);
                        break;
                    }
                }

                List<EstimateCar> estimateCars = new ArrayList<>();
                estimateCars.add(estimateCar);
                cacheEstimateResult.setList(estimateCars);

                carLevelMap.put(estimateCar.getCarLevel(), cacheEstimateResult);
            }
        }

        // 更新每个分组的价格范围
        List<CacheEstimateResult> carsList = new ArrayList<>();
        for (Object carLevelObject : carLevelMap.values()) {
            CacheEstimateResult cacheEstimateResult = (CacheEstimateResult) carLevelObject;
            if (cacheEstimateResult.getReimbursementCn() == null
                    || cacheEstimateResult.getReimbursementCn().isEmpty()) {
                cacheEstimateResult.setReimbursementCn("报销全部");
                cacheEstimateResult.setReimbursementEn("All Reimburseable");
            }
            List<EstimateCar> estimateCars = cacheEstimateResult.getList();
            // estimateCars.stream().sorted(Comparator.comparing(EstimateCar::getEstimatePrice));
            estimateCars.sort(Comparator.comparing(EstimateCar::getEstimatePrice));

            if (estimateCars.get(0).getEstimatePrice()
                    .compareTo(estimateCars.get(estimateCars.size() - 1).getEstimatePrice()) == 0) {
                cacheEstimateResult.setPriceLab(String.valueOf(estimateCars.get(0).getEstimatePrice()));
            } else {
                cacheEstimateResult.setPriceLab(estimateCars.get(0).getEstimatePrice() + " ~ "
                        + estimateCars.get(estimateCars.size() - 1).getEstimatePrice()); // 车型估价区间 如 "10 ~ 15"
            }
            carsList.add(cacheEstimateResult);
        }

        if (null != excludeSourceCodeList && excludeSourceCodeList.size() > 0) {
            if (StrUtil.isEmpty(estimateId)) {
                estimateId = result.getStr("estimateId");
            }
            redisUtil.set(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_EXCLUDE_PREFIX + estimateId,
                    JSONUtil.toJsonStr(excludeSourceCodeList), CacheConsts.THIRTY_MINUTE_CACHE_EXPIRE_TIME);
        }
        return carsList;
    }

    private BigDecimal getDiscountPrice(BigDecimal estimatePrice, Long companyId, Boolean onlyRound) {
        String settingVal = cacheService.getEstimatePriceRule(companyId);
        if (StrUtil.isEmpty(settingVal))
            return estimatePrice;

        JSONObject estimateJsonObject = new JSONObject(settingVal);

        if (estimateJsonObject.getBool("enabled", false) == false)
            return estimatePrice;

        Boolean round = estimateJsonObject.getBool("round", false);

        int type = estimateJsonObject.getInt("type", 0);
        if (type == 0)
            return round ? (estimatePrice.setScale(0, RoundingMode.DOWN)) : estimatePrice;

        if (onlyRound)
            return round ? (estimatePrice.setScale(0, RoundingMode.DOWN)) : estimatePrice;

        JSONArray ruleJsonArray = estimateJsonObject.getJSONArray("rules");
        if (null == ruleJsonArray || ruleJsonArray.size() == 0)
            return round ? (estimatePrice.setScale(0, RoundingMode.DOWN)) : estimatePrice;

        for (int i = 0; i < ruleJsonArray.size(); i++) {
            JSONObject ruleJsonObject = ruleJsonArray.getJSONObject(i);
            BigDecimal min = ruleJsonObject.getBigDecimal("min", BigDecimal.ZERO);
            BigDecimal max = ruleJsonObject.getBigDecimal("max", new BigDecimal(9999999));

            if (min.compareTo(BigDecimal.ZERO) == 0 && max.compareTo(BigDecimal.ZERO) == 0)
                continue;

            if (estimatePrice.compareTo(min) >= 0 && estimatePrice.compareTo(max) == -1) {

                BigDecimal discount = ruleJsonObject.getBigDecimal("discount");
                if (null == discount)
                    return round ? (estimatePrice.setScale(0, RoundingMode.DOWN)) : estimatePrice;
                if (type == 1) {// 按数值
                    estimatePrice = estimatePrice.subtract(discount);
                    if (estimatePrice.compareTo(BigDecimal.ZERO) == -1)
                        estimatePrice = BigDecimal.ZERO;
                } else {// 按比例
                    if (discount.compareTo(new BigDecimal(100)) >= 0)
                        estimatePrice = BigDecimal.ZERO;
                    estimatePrice = estimatePrice.subtract(estimatePrice.multiply(discount).divide(new BigDecimal(100))
                            .setScale(2, RoundingMode.HALF_UP));
                    if (estimatePrice.compareTo(BigDecimal.ZERO) == -1)
                        estimatePrice = BigDecimal.ZERO;
                }

                break;
            }
        }

        return round ? (estimatePrice.setScale(0, RoundingMode.DOWN)) : estimatePrice;
    }

    /**
     * 订单修改目的地重新估价估价
     * 使用当前行程车型-运力平台重新估价,估价结果中estimatePrice是 已经行驶里程车费与发起估价时所处位置到新目的地估价之和，
     * 估价后要重新进行一遍下单前验证,如不复合约束,返回原因
     * 前端上传的参数，起点要用乘客修改的当前位置，终点要用修改之后的，departuretime应该赋值当时时间
     * 每个单只允许修改一次目的地
     */
    public Map<String, Object> changeDestEstimate(CreateOrderParam orderParam) throws Exception {
        if (orderParam.getOrderId() == null) {
            throw new BusinessException("缺少orderId参数");
        }

        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderParam.getOrderId());
        if (orderBase == null) {
            throw new BusinessException(OrderConstant.NOT_FOUND_ORDER);
        }
        if (orderBase.getState() != 4) {
            throw new BusinessException("只有行程中订单允许修改目的地");
        }
        if (!orderBase.getAllowChangeDest()) {
            throw new BusinessException("场景或运力平台不支持修改目的地");
        }
        if (orderParam.getDepartCityCode() == null) {
            throw new BusinessException("缺少起点城市code");
        }
        if (orderParam.getDepartLat() == null || orderParam.getDepartLng() == null) {
            throw new BusinessException("缺少起点位置参数");
        }
        if (orderParam.getDestLat() == null || orderParam.getDestLng() == null) {
            throw new BusinessException("缺少新目的地位置参数");
        }
        if (orderParam.getDestLat().equals(orderBase.getDestLat())
                && orderParam.getDestLng().equals(orderBase.getDestLng())) {
            throw new BusinessException("新目的地与原目的地位置相同");
        }

        orderParam.setDepartTime(new Date());
        orderParam.setPassengerPhone(orderBase.getPassengerPhone());
        orderParam.setFlightNumber(orderBase.getFlightNumber());
        orderParam.setFlightArrivalAirportCode(orderBase.getArrivalAirportCode());
        orderParam.setFlightDepartTime(orderBase.getDepartTime());
        orderParam.setPreDepartApplyId(orderBase.getPreDepartApplyId());
        orderParam.setSceneId(orderBase.getSceneId());
        orderParam.setScenePublishId(orderBase.getScenePublishId());
        Map<String, Object> estimateResultMap = estimate(orderParam, false, null);

        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderParam.getOrderId());
        Integer carLevel = orderSource.getCarLevel();
        List<CacheEstimateResult> cars = (List<CacheEstimateResult>) estimateResultMap.get("cars");
        if (ObjectUtil.isEmpty(cars)) {
            throw new BusinessException("预估失败");
        }
        Map<String, Object> map = new HashMap<>();
        CacheEstimateResult estimateResult = cars.get(0);
        BigDecimal newMaxEstimatePrice = BigDecimal.ZERO;// 本次最大预估价格
        for (EstimateCar car : estimateResult.getList()) {
            if (car.getCarLevel() == carLevel) {
                if (newMaxEstimatePrice.compareTo(car.getEstimatePrice()) == -1) {
                    newMaxEstimatePrice = car.getEstimatePrice();
                }
                map.put("estimatePrice", car.getEstimatePrice());
                map.put("dynamicCode", car.getDynamicCode());
                map.put("carSource", cacheService.getSourceName(car.getCarSourceId(), 1));
                map.put("carSourceId", car.getCarSourceId());
                map.put("carSourceImg", car.getCarSourceImg());
                map.put("carLevel", car.getCarLevel());
                map.put("carLevelName", CarLevel.getName(car.getCarLevel()));
                map.put("estimateTime", car.getEstimateTime());
                map.put("estimateDistance", car.getEstimateDistance());

                redisUtil.set(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE + car.getDynamicCode(), map,
                        CacheConsts.TEN_MINUTE);

                redisUtil.set(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE_DYNAMIC_CODE + car.getDynamicCode(),
                        JSONUtil.toJsonStr(estimateResultMap.get("estimateId")), CacheConsts.TEN_MINUTE);
                break;
            }
        }

        // 调用.net接口检查订单参数
        RequestCheckOrderDto requestCheckOrderDto = new RequestCheckOrderDto();
        BeanUtil.copyProperties(orderParam, requestCheckOrderDto, false);
        requestCheckOrderDto.setNewEstimateAmount(newMaxEstimatePrice);
        requestCheckOrderDto.setCheckType((short) 1);
        BaseResponse response = configurationService.checkOrder(requestCheckOrderDto, null);
        if (response.getCode() != 0) {
            if (response.getCode() == 1009 || response.getCode() == 1010 || response.getCode() == 1011) {
                throw new CustomException(response.getCode(), response.getMessage());
            } else {
                if (response.getCode() == 3 && ("用户未设置账户,暂时无法用车.".equals(response.getMessage())
                        || "The user has not set up an account and is temporarily unable to use the vehicle."
                                .equals(response.getMessage()))) {
                    log.info("checkOrder 无默认账户，也可以下单，结算时，传企业默认账户:{}", response.getMessage());
                    // 获取默认账户填充账户新
                    Long accountId = billService.getDefaultAccountId(orderParam.getCompanyId());
                    orderParam.setAccountId(accountId);
                    billService.insertAccountUser(orderParam.getCompanyId(), orderParam.getUserId(), accountId);
                } else {
                    throw new CustomException(response.getCode(), response.getMessage());
                }
            }
        }

        return map;
    }

    /**
     * 订单修改目的地
     */
    public int changeDest(CreateOrderParam orderParam) throws Exception {
        Long userId = orderParam.getUserId();
        Long orderId = orderParam.getOrderId();
        String coreOrderId = null;
        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
        coreOrderId = orderSource.getCoreOrderId();

        if (orderParam.getPrePayFlag() != null && orderParam.getPrePayFlag()) {
            throw new BusinessException("预付费订单不可更改目的地");
        }

        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE_DYNAMIC_CODE + orderParam.getDynamicCode())) {
            if (ObjectUtil.isEmpty(orderParam.getEstimateId())) {
                orderParam.setEstimateId(StrUtil.toStringOrNull(redisUtil
                        .get(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE_DYNAMIC_CODE + orderParam.getDynamicCode())));
            }
        }

        BaseResponse response = coreOrderService.changeDest(orderParam, coreOrderId);

        if (response.getCode() != 0) {
            throw new CustomException(1004001, response.getMessage());
        }

        // 更新缓存
        CacheOrder cacheOrder = null;

        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId))) {
            Object obj = redisUtil.hashGet(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId),
                    String.valueOf(orderId));
            try {
                cacheOrder = JSONUtil.toBean(JSONUtil.toJsonStr(obj), CacheOrder.class);
            } catch (Exception e) {
                cacheOrder = (CacheOrder) obj;
            }
        }

        if (cacheOrder != null) {
            cacheOrder.setDestLocationName(orderParam.getDestLocationName());
            redisUtil.hashPut(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId), String.valueOf(orderId),
                    JSONUtil.toJsonStr(cacheOrder));
        }

        // add by guoxin 修改目的地通知report-service
        RequestReportNotifyChangeDest requestReportNotifyChangeDest = new RequestReportNotifyChangeDest();
        if (null != orderParam.getOrderId()) {
            requestReportNotifyChangeDest.setId(orderParam.getOrderId());
        }
        if (null != orderParam.getDestLat()) {
            requestReportNotifyChangeDest.setActualDestLat(orderParam.getDestLat());
        }
        if (null != orderParam.getDestLng()) {
            requestReportNotifyChangeDest.setActualDestLng(orderParam.getDestLng());
        }

        if (StringUtils.isNotEmpty(orderParam.getDestLocation())) {
            requestReportNotifyChangeDest.setActualDestLocation(orderParam.getDestLocation());
        }

        if (StringUtils.isNotEmpty(orderParam.getDestLocationName())) {
            requestReportNotifyChangeDest.setActualDestLocationName(orderParam.getDestLocationName());
        }

        requestReportNotifyChangeDest.setCompanyId(orderParam.getCompanyId());

        // 加密地址
        cipherService.addressEncryptForCreateOrderParam(orderParam);

        // BeanUtil.copyProperties(orderParam, requestReportNotifyChangeDest, true);
        requestReportNotifyChangeDest.setStatus((short) 4);
        requestReportNotifyChangeDest.setSubStatus((short) 100);
        // 添加属性值，通知报表服务
        requestReportNotifyChangeDest.setIsChangeDest(true);
        requestReportNotifyChangeDest.setChangeDestTime(new Date());

        // 设置修改目的地时预估信息
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE + orderParam.getDynamicCode())) {
            Object o = redisUtil.get(CacheConsts.REDIS_KEY_CHANGE_DEST_ESTIMATE + orderParam.getDynamicCode());
            JSONObject jsonObject = JSONUtil.parseObj(o);
            BigDecimal estimatePrice = jsonObject.getBigDecimal("estimatePrice", null);
            BigDecimal estimateTime = jsonObject.getBigDecimal("estimateTime", null);
            BigDecimal estimateDistance = jsonObject.getBigDecimal("estimateDistance", null);

            if (ObjectUtil.isNotEmpty(estimatePrice)) {
                orderSource.setEstimatePrice(estimatePrice);
                requestReportNotifyChangeDest.setEstimatePrice(estimatePrice);
            }

            if (ObjectUtil.isNotEmpty(estimateTime)) {
                int tempEstimateTime = NumberUtil.mul(estimateTime, 60.0).intValue();
                orderSource.setEstimateTime(tempEstimateTime);
                requestReportNotifyChangeDest.setEstimateTime(tempEstimateTime);
            }

            if (ObjectUtil.isNotEmpty(estimateDistance)) {
                int tempEstimateDistance = NumberUtil.mul(estimateDistance, 1000.0).intValue();
                orderSource.setEstimateDistance(tempEstimateDistance);
                requestReportNotifyChangeDest.setEstimateDistance(tempEstimateDistance);
            }

        }

        orderTask.notifyReportServiceChangeDest(requestReportNotifyChangeDest);

        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        // 添加changeDestLog
        OrderChangeDestLog orderChangeDestLog = new OrderChangeDestLog();
        orderChangeDestLog.setId(snowFlakeUtil.getNextId());
        orderChangeDestLog.setCarLat(orderParam.getDepartLat());
        orderChangeDestLog.setCarLng(orderParam.getDepartLng());
        orderChangeDestLog.setCarLocation(orderParam.getPickupLocation());
        orderChangeDestLog.setCarLocationName(orderParam.getPickupLocationName());
        orderChangeDestLog.setOldCityCode(orderBase.getDestCityCode());
        orderChangeDestLog.setOldCityName(orderBase.getDestCityName());
        orderChangeDestLog.setOldLat(orderBase.getDestLat());
        orderChangeDestLog.setOldLng(orderBase.getDestLng());
        orderChangeDestLog.setOldLocation(orderBase.getDestLocation());
        orderChangeDestLog.setOldLocationName(orderBase.getDestLocationName());
        orderChangeDestLog.setOrderId(orderId);
        orderChangeDestLog.setOrderSourceId(orderSource.getId());
        orderChangeDestLog.setCreateTime(new Date());
        orderChangeDestLogMapper.insertSelective(orderChangeDestLog);

        // 修改订单表
        orderBase.setDestCityCode(orderParam.getDestCityCode());
        orderBase.setDestCityName(orderParam.getDestCityName());
        orderBase.setDestLat(orderParam.getDestLat());
        orderBase.setDestLng(orderParam.getDestLng());
        orderBase.setDestLocation(orderParam.getDestLocation());
        orderBase.setDestLocationName(orderParam.getDestLocationName());
        Boolean isUserPay = orderParam.getIsUserPay();
        if (null != isUserPay) {
            orderBase.setIsUserPay(isUserPay);
            orderBase.setIsNotUserPayReason(orderParam.getIsNotUserPayReason());
        } else {
            if (null != orderBase.getIsUserPay()) {
                orderBase.setIsUserPay(null);
                orderBase.setIsNotUserPayReason(null);
                orderBaseMapper.updateUserPay(orderBase);
            }
        }
        // BeanUtil.copyProperties(orderParam, orderBase, copyOptions);
        orderBase.setAllowChangeDest(false);// 只允许修改一次
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);

        // 更新order-source表
        orderSource.setIsChangeDest(true);
        orderSourceMapper.updateByPrimaryKeySelective(orderSource);

        return 0;
    }

    /**
     * 缓存中的order_source表信息
     *
     * @param orderId
     * @return
     */
    public OrderSource getOrderSourceFromCache(Long orderId) {
        OrderSource orderSource = null;
        String key = CacheConsts.REDIS_KEY_ORDER_SOURCE + orderId;
        if (redisUtil.hasKey(key)) {
            orderSource = (OrderSource) redisUtil.get(key);
        }
        return orderSource;
    }

    /**
     * 缓存中的order_base表信息
     *
     * @param orderId
     * @return
     */
    public OrderBase getOrderBaseFromCache(Long orderId) {
        OrderBase orderBase = null;
        String key = CacheConsts.REDIS_KEY_ORDER_BASE + orderId;
        if (redisUtil.hasKey(key)) {
            orderBase = (OrderBase) redisUtil.get(key);
        }
        return orderBase;
    }

    /**
     * 判断是否是预约管家单
     *
     * @param orderBase
     * @return
     */
    public boolean isBooking(OrderBase orderBase) {
        Boolean isBooking = false;
        if (null != orderBase.getCustomInfo()) {
            JSONObject jsonObject = new JSONObject(orderBase.getCustomInfo());
            if (jsonObject != null && jsonObject.containsKey("extraServices")) {
                JSONArray extraJSONArray = jsonObject.getJSONArray("extraServices");
                if (extraJSONArray != null && extraJSONArray.size() > 0) {
                    for (int i = 0; i < extraJSONArray.size(); i++) {
                        JSONObject eachExtraJSONObject = extraJSONArray.getJSONObject(i);
                        String extraServiceCode = eachExtraJSONObject.getStr("code");
                        if ("ES0005".equals(extraServiceCode) || "ES0006".equals(extraServiceCode)) {
                            isBooking = true;
                        }
                    }
                }
            }
        }
        return isBooking;
    }

    /**
     * 追加车型预估价格
     * 调用中台预估接口 估价结果中剔除掉本单已经选择过的车型和本单无法选择的车型(价格过高)
     */
    public Map<String, Object> appendEstimate(Long userId, Long orderId) throws Exception {
        CreateOrderParam orderParam = new CreateOrderParam();
        CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(userId, orderId);
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId)) {
            orderParam = (CreateOrderParam) redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId);
        } else {
            BeanUtil.copyProperties(cacheOrder, orderParam, false);
        }

        List<SelectedCar> excludeCars = new ArrayList<SelectedCar>();

        List<CacheEstimateResult> estimateResults = null;
        if (cacheOrder != null && cacheOrder.getCallingCars() != null) {
            estimateResults = cacheOrder.getCallingCars();
        }
        if (estimateResults != null) {
            for (CacheEstimateResult cacheEstimateResult : estimateResults) {
                for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                    SelectedCar selectedCar = BeanUtil.toBean(estimateCar, SelectedCar.class);
                    excludeCars.add(selectedCar);
                }
            }
        }

        List<CarTypeLabelEstimateVo> estimateCarTypeResults = null;
        if (cacheOrder != null && cacheOrder.getCallingCarTypeLabelCars() != null) {
            estimateCarTypeResults = cacheOrder.getCallingCarTypeLabelCars();
        }
        if (estimateCarTypeResults != null) {
            for (CarTypeLabelEstimateVo cacheEstimateResult : estimateCarTypeResults) {
                for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                    SelectedCar selectedCar = BeanUtil.toBean(estimateCar, SelectedCar.class);
                    excludeCars.add(selectedCar);
                }
            }
        }

        orderParam.setEstimateId(cacheOrder.getEstimateId());
        Map<String, Object> estimateResultMap = estimate(orderParam, true, excludeCars);

        // 更新关联订单的估价结果
        String estimateId = cacheOrder.getEstimateId();
        log.info("更新关联订单的估价结果：{}, 缓存: {}" + estimateId, JSONUtil.toJsonStr(estimateResultMap.get("cars")));
        redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId,
                JSONUtil.toJsonStr(estimateResultMap.get("cars")),
                CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);

        return estimateResultMap;
    }

    /**
     * 追加车型下单
     */
    /*
     * public BaseResponse appendPlaceOrder(Long userId, Long orderId, JSONArray
     * cars) throws Exception {
     * return BaseResponse.Builder.success();
     * }
     */
    public BaseResponse appendPlaceOrder(AppendCarTypeParam appendCarTypeParam, boolean isPrePay) throws Exception {
        String traceId = IdUtil.simpleUUID();
        long startTime = System.currentTimeMillis();

        Log iLog = logService.getLog(appendCarTypeParam.getCompanyId(), appendCarTypeParam.getUserId(),
                appendCarTypeParam.getOrderId(),
                InterfaceEnum.ORDER_APPEND_PLACEORDER, traceId);
        iLog.setBody(JSONUtil.toJsonStr(appendCarTypeParam));

        if (appendCarTypeParam == null ||
                appendCarTypeParam.getOrderId() <= 0 ||
                appendCarTypeParam.getEstimateId() == null ||
                appendCarTypeParam.getCars() == null ||
                appendCarTypeParam.getCars().size() == 0) {
            if (isPrePay) {
                notifyBillService(appendCarTypeParam.getOrderId());
            }
            throw new BusinessException("参数错误");
        }

        CreateOrderParam orderParam = (CreateOrderParam) redisUtil
                .get(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + appendCarTypeParam.getOrderId());

        CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(appendCarTypeParam.getUserId(),
                appendCarTypeParam.getOrderId());

        if (cacheOrder == null) {
            if (isPrePay) {
                notifyBillService(appendCarTypeParam.getOrderId());
            }
            throw new BusinessException("没有订单信息");
        }

        Integer maxCarLevel = CarLevel.NORMAL.getCode();
        BigDecimal oldMaxEstimatePrice = BigDecimal.ZERO;
        for (SelectedCar car : cacheOrder.getCars()) {
            if (oldMaxEstimatePrice.compareTo(car.getEstimatePrice()) < 0) {
                oldMaxEstimatePrice = car.getEstimatePrice();
            }

            if (maxCarLevel < car.getCarLevel()) {
                maxCarLevel = car.getCarLevel();
            }
        }

        BigDecimal newMaxEstimatePrice = BigDecimal.ZERO;// 本次追加车型最大预估价格
        Integer newMaxCarLevel = CarLevel.NORMAL.getCode();
        List<Long> couponIds = new ArrayList<>();
        List<SelectedCar> upgradeCars = new ArrayList<>();

        for (SelectedCar car : appendCarTypeParam.getCars()) {
            if (newMaxEstimatePrice.compareTo(car.getEstimatePrice()) == -1) {
                newMaxEstimatePrice = car.getEstimatePrice();
            }
            if (car.getCouponId() != null && car.getCouponId() != 0 && !couponIds.contains(car.getCouponId())) {
                couponIds.add(car.getCouponId());
            }

            // 获取选中最高的carLevel，免费升舱用
            if (newMaxCarLevel == CarLevel.LUXURY.getCode()) {
                upgradeCars.clear();
                continue;
            }

            if (newMaxCarLevel < car.getCarLevel()) {
                upgradeCars.clear();
                newMaxCarLevel = car.getCarLevel();
                upgradeCars.add(car);
            } else if (newMaxCarLevel == car.getCarLevel()) {
                upgradeCars.add(car);
            }
        }

        // log.info("OrderServiceImpl.appendPlaceOrder.pre===>oldEstimatePrice:{},newEstimatePrice:{}",oldMaxEstimatePrice,newMaxEstimatePrice);

        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(appendCarTypeParam.getOrderId());

        BigDecimal extraFee = BigDecimal.ZERO;// 订单包含的增值服务费用
        Boolean isUpgradeOrder = false;// 原始订单是否触发了升舱
        Integer oldUpgradeCarLevel = CarLevel.NORMAL.getCode();// 原始订单的升舱车型
        Boolean changeToNomalOrder = false;// 升舱订单是否变更到普通订单
        Boolean isGetUpgradeSetting = false;// 是否重新获取升舱设置
        Boolean isFirstUpgrade = false;// 是否是首次升舱
        List<SelectedCar> copySelectedCars = new ArrayList<>();

        // 将预估时间和预估里程补充到自定义字段中
        List<CacheEstimateResult> estimateResults = JSONUtil.toList(
                JSONUtil.toJsonStr(
                        redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + appendCarTypeParam.getEstimateId())),
                CacheEstimateResult.class);
        if (CollectionUtils.isNotEmpty(estimateResults)) {
            for (SelectedCar selectedCar : appendCarTypeParam.getCars()) {
                if (null != selectedCar.getEstimateDistance() || null != selectedCar.getEstimateTime())
                    continue;
                for (CacheEstimateResult cacheEstimateResult : estimateResults) {
                    List<EstimateCar> estimateCars = cacheEstimateResult.getList().stream()
                            .filter(t -> t.getCarLevel() == selectedCar.getCarLevel()
                                    && t.getCarSourceId().equals(selectedCar.getCarSourceId()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(estimateCars)) {
                        selectedCar.setEstimateTime(estimateCars.get(0).getEstimateTime());
                        selectedCar.setEstimateDistance(estimateCars.get(0).getEstimateDistance());
                    }
                }
            }
        }

        copySelectedCars.addAll(appendCarTypeParam.getCars());

        // 调用.net接口检查订单参数
        RequestCheckOrderDto requestCheckOrderDto = new RequestCheckOrderDto();
        BeanUtil.copyProperties(orderParam, requestCheckOrderDto);

        JSONObject customObject = null;
        if (orderBase.getCustomInfo() != null) {
            customObject = new JSONObject(orderBase.getCustomInfo());
            if (couponIds.size() > 0) {
                if (customObject.containsKey("couponIds")) {
                    List<Long> couponIdList = customObject.getBeanList("couponIds", Long.class);
                    couponIdList.addAll(couponIds);
                    customObject.set("couponIds", couponIdList);
                } else {
                    customObject.set("couponIds", couponIds);
                }
            }
            if (customObject.containsKey("upgrade")) {
                isUpgradeOrder = true;

                JSONObject upgradeObject = new JSONObject(customObject.get("upgrade"));
                oldUpgradeCarLevel = upgradeObject.getInt("upgradeCarLevel");
                oldUpgradeCarLevel--;
            }

            if (customObject.containsKey("extraFee")) {
                extraFee = customObject.getBigDecimal("extraFee");
            }

            List<SelectedCar> selectedCarList = customObject.getBeanList("selectedCars", SelectedCar.class);
            selectedCarList.addAll(copySelectedCars);
            customObject.set("selectedCars", selectedCarList);

            if (isPrePay) {
                if (customObject.containsKey("prepay")) {
                    JSONArray prepayJsonArray = customObject.getJSONArray("prepay");
                    JSONObject prepayJsonObject = new JSONObject();
                    prepayJsonObject.set("isPrepay", true);
                    prepayJsonObject.set("prepayAmount", newMaxEstimatePrice.subtract(oldMaxEstimatePrice));
                    prepayJsonArray.add(prepayJsonObject);
                    customObject.set("prepay", prepayJsonArray);
                } else {
                    JSONArray prepayJsonArray = new JSONArray();
                    JSONObject prepayJsonObject = new JSONObject();
                    prepayJsonObject.set("isPrepay", true);
                    prepayJsonObject.set("prepayAmount", newMaxEstimatePrice);
                    prepayJsonArray.add(prepayJsonObject);
                    customObject.set("prepay", prepayJsonArray);

                    orderBase.setIsPrepay(true);
                }
            }

            requestCheckOrderDto.getCars().addAll(selectedCarList);
        }

        requestCheckOrderDto.setCheckType((short) 2);

        BigDecimal frozenAmountDiff = BigDecimal.ZERO;

        // add by guoxin 临时处理
        newMaxEstimatePrice = newMaxEstimatePrice.add(extraFee);
        OrderSource orderSource = orderSourceMapper.selectByOrderId(appendCarTypeParam.getOrderId());
        if (orderSource != null && oldMaxEstimatePrice.compareTo(orderSource.getEstimatePrice()) < 0) {
            oldMaxEstimatePrice = orderSource.getEstimatePrice();
        }

        // log.info("OrderServiceImpl.appendPlaceOrder.after===>oldEstimatePrice:{},newEstimatePrice:{}",oldMaxEstimatePrice,newMaxEstimatePrice);

        requestCheckOrderDto.setNewEstimateAmount(newMaxEstimatePrice);
        requestCheckOrderDto.setOldEstimateAmount(oldMaxEstimatePrice);

        // 自定义限制条件参数拼装
        this.setOrderParamByOrderLimit(requestCheckOrderDto, orderParam);

        if (newMaxEstimatePrice.compareTo(oldMaxEstimatePrice) == 1) {
            BigDecimal additionalAmount = newMaxEstimatePrice.subtract(oldMaxEstimatePrice);
            BigDecimal newFrozenAmount = additionalAmount.multiply(FROZEN_AMOUNT_RATE);
            frozenAmountDiff = newFrozenAmount;
        }

        BaseResponse checkOrderResponse = null;
        // 如果企业没开启自定义条件限制直接返回
        if (orderLimitService.isOpenOrderLimitConfig(orderParam.getCompanyId())) {
            // 自定义限制条件参数拼装
            this.setOrderParamByOrderLimit(requestCheckOrderDto, orderParam);
            requestCheckOrderDto.setFrozenAmount(newMaxEstimatePrice);
            List<KeyValue> customLimits = requestCheckOrderDto.getCustomLimits();
            customLimits = customLimits.stream()
                    .filter(o -> !StringUtils.equals(o.getKey(), CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_5.getKey()))
                    .collect(Collectors.toList());
            requestCheckOrderDto.setCustomLimits(customLimits);
            checkOrderResponse = orderValidationFeign.checkOrder(requestCheckOrderDto);
        } else {
            if (StrUtil.isNotBlank(requestCheckOrderDto.getCustomInfo())) {
                JSONObject customJsonObject = new JSONObject(requestCheckOrderDto.getCustomInfo());
                Object customCarInfoObj = JSONUtil.getByPath(customJsonObject, "customCarInfo", null);
                if (ObjectUtil.isNotEmpty(customCarInfoObj)) {
                    String customCarInfo = JSONUtil.toJsonStr(customCarInfoObj);
                    requestCheckOrderDto.setCustomInfo(customCarInfo);
                }
            }
            requestCheckOrderDto.setFrozenAmount(frozenAmountDiff);
            checkOrderResponse = configurationService.checkOrder(requestCheckOrderDto, null);
            if (checkOrderResponse.getCode() == 1009 || checkOrderResponse.getCode() == 1010
                    || checkOrderResponse.getCode() == 1011) {
                throw new CustomException(checkOrderResponse.getCode(), checkOrderResponse.getMessage());
            }
        }

        if (checkOrderResponse.getCode() != 0) {
            if (null != requestCheckOrderDto.getAllowExcess() && requestCheckOrderDto.getAllowExcess()) {
                // 根据编码判断是否提示
                boolean isError = regulationService.isErrorByCode(orderParam, checkOrderResponse.getCode());
                if (isError) {
                    throw new CustomException(checkOrderResponse.getCode(), checkOrderResponse.getMessage());
                }
            } else {
                if (checkOrderResponse.getCode() == 3 && ("用户未设置账户,暂时无法用车.".equals(checkOrderResponse.getMessage())
                        || "The user has not set up an account and is temporarily unable to use the vehicle."
                                .equals(checkOrderResponse.getMessage()))) {
                    log.info("checkOrder 无默认账户，也可以下单，结算时，传企业默认账户:{}", checkOrderResponse.getMessage());
                    // 获取默认账户填充账户新
                    Long accountId = billService.getDefaultAccountId(orderParam.getCompanyId());
                    orderParam.setAccountId(accountId);
                    billService.insertAccountUser(orderParam.getCompanyId(), orderParam.getUserId(), accountId);
                } else {
                    if (isPrePay) {
                        notifyBillService(appendCarTypeParam.getOrderId());
                    }
                    throw new CustomException(checkOrderResponse.getCode(), checkOrderResponse.getMessage());
                }
            }
        }

        if (newMaxCarLevel == CarLevel.LUXURY.getCode()) {
            changeToNomalOrder = isUpgradeOrder ? true : false;
        } else {
            if (isUpgradeOrder) {// 免费升舱订单
                if (oldUpgradeCarLevel <= newMaxCarLevel) {
                    isGetUpgradeSetting = true;
                }
            } else {
                // 不是免费升舱订单
                // 将此次选择的车型和之前的车型件对比，如果此次车型为更高一级车型，重新判断是否符合免费升舱，符合，走升舱流程
                if (newMaxCarLevel >= maxCarLevel) {
                    isGetUpgradeSetting = true;
                    isFirstUpgrade = true;
                }
            }
        }

        Boolean isUpgradeAppend = false;

        if (isGetUpgradeSetting) {// 把强生过滤掉
            // 获取免费升舱配置信息
            List<UpgradeParam> carList = new ArrayList<>();
            UpgradeDto upgradeDto = new UpgradeDto();
            upgradeDto.setUserId(appendCarTypeParam.getUserId());
            upgradeDto.setCompanyId(appendCarTypeParam.getCompanyId());
            upgradeDto.setUpgradeCarLevel(newMaxCarLevel);
            for (SelectedCar car : appendCarTypeParam.getCars()) {
                if (car.getCarLevel() != newMaxCarLevel) {
                    continue;
                }
                UpgradeParam upgradeParam = new UpgradeParam();
                upgradeParam.setCarSourceId(car.getCarSourceId());
                upgradeParam.setEstimatePrice(car.getActualEstimatePrice());
                carList.add(upgradeParam);
            }
            upgradeDto.setCars(carList);

            // BaseResponse baseResponse = systemFeign.getUpgrade(upgradeDto);// 替换
            BaseResponse baseResponse = systemService.getUpgrade(upgradeDto);
            if (baseResponse.getCode() == 0) {
                JSONObject jsonObject = new JSONObject(baseResponse.getData());
                if (jsonObject.getBool("isUpgrade")) {
                    // 升舱前车型名称
                    String oldLevelName = CarLevel.getName(newMaxCarLevel);

                    isUpgradeAppend = true;
                    // 从缓存获取预估车型结果
                    if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + appendCarTypeParam.getEstimateId())) {
                        // List<CacheEstimateResult> estimateResultList = (List<CacheEstimateResult>)
                        // redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX +
                        // appendCarTypeParam.getEstimateId());
                        List<CacheEstimateResult> estimateResultList = JSONUtil.toList(redisUtil
                                .get(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + appendCarTypeParam.getEstimateId())
                                .toString(), CacheEstimateResult.class);
                        int upgradeCarLevel = jsonObject.getInt("upgradeCarLevel");
                        BigDecimal maxDiscountAmount = jsonObject.getBigDecimal("maxDiscountAmount");
                        List<Integer> carSources = jsonObject.getBeanList("carSources", Integer.class);

                        // 将更高一级车型加到下单选择的cars里
                        for (CacheEstimateResult cacheEstimateResult : estimateResultList) {
                            if (cacheEstimateResult.getId() == upgradeCarLevel) {
                                List<UpgradeParam> upgradeParamList = new ArrayList<>();
                                List<EstimateCar> cars = cacheEstimateResult.getList();
                                for (EstimateCar estimateCar : cars) {
                                    if (carSources.contains(estimateCar.getCarSourceId())) {
                                        SelectedCar selectedCar = new SelectedCar();
                                        BeanUtil.copyProperties(estimateCar, selectedCar, "couponId", "couponAmount");

                                        selectedCar.setUpgradeValue(1);
                                        selectedCar.setNewCarLevelName(oldLevelName);

                                        appendCarTypeParam.getCars().add(selectedCar);

                                        UpgradeParam upgradeParam = new UpgradeParam();
                                        upgradeParam.setEstimatePrice(estimateCar.getActualEstimatePrice());
                                        upgradeParam.setCarSourceId(estimateCar.getCarSourceId());
                                        upgradeParamList.add(upgradeParam);
                                    }
                                }

                                if (upgradeParamList.size() > 0) {
                                    if (isFirstUpgrade) {
                                        if (customObject == null) {
                                            customObject = new JSONObject();
                                        }

                                        LinkedHashMap upgradeMap = new LinkedHashMap();
                                        upgradeMap.put("isUpgradeSuccess", false);
                                        upgradeMap.put("upgradeCarLevel", upgradeCarLevel);
                                        upgradeMap.put("maxDiscountAmount", maxDiscountAmount);
                                        upgradeMap.put("upgradeCars", upgradeParamList);
                                        customObject.set("upgrade", upgradeMap);

                                        // 更新免费升舱占用
                                        UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                                        updateUsageCountDto.setUserId(appendCarTypeParam.getUserId());
                                        updateUsageCountDto.setCompanyId(appendCarTypeParam.getCompanyId());
                                        updateUsageCountDto.setUseUpgrade(true);
                                        updateUsageCountDto.setOrderId(appendCarTypeParam.getOrderId());
                                        updateUsageCountDto.setRecordDesc("appendPlaceOrder");
                                        updateUsageCountDto.setOrderTime(orderBase.getDepartTime());
                                        // systemFeign.updateUsageCount(updateUsageCountDto);
                                        systemService.updateUsageCount(updateUsageCountDto);
                                    } else {
                                        // 更新数据库免费升舱信息
                                        if (customObject.containsKey("upgrade")) {
                                            JSONObject upgradeObject = new JSONObject(customObject.get("upgrade"));
                                            if (upgradeObject.getInt("upgradeCarLevel") == upgradeCarLevel) {
                                                List<UpgradeParam> oldUpgradeParas = upgradeObject
                                                        .getBeanList("upgradeCars", UpgradeParam.class);
                                                oldUpgradeParas.addAll(upgradeParamList);
                                                upgradeObject.set("upgradeCars", oldUpgradeParas);
                                            } else {
                                                upgradeObject.set("upgradeCarLevel", upgradeCarLevel);
                                                upgradeObject.set("upgradeCars", upgradeParamList);
                                            }
                                            upgradeObject.set("maxDiscountAmount", maxDiscountAmount);

                                            customObject.set("upgrade", upgradeObject);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else {
                    changeToNomalOrder = isUpgradeOrder ? true : false;
                }
            }
        }

        if (isUpgradeOrder && changeToNomalOrder) {
            // 更新数据库中的免费升舱标识，取消免费升舱占用
            if (!isFirstUpgrade) {
                // 更新数据库免费升舱信息
                if (customObject != null && customObject.containsKey("upgrade")) {
                    customObject.remove("upgrade");
                }

                // 取消免费升舱占用
                UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                updateUsageCountDto.setUserId(appendCarTypeParam.getUserId());
                updateUsageCountDto.setCompanyId(appendCarTypeParam.getCompanyId());
                updateUsageCountDto.setUseUpgrade(false);
                updateUsageCountDto.setOrderId(appendCarTypeParam.getOrderId());
                updateUsageCountDto.setRecordDesc("appendPlaceOrder");
                updateUsageCountDto.setOrderTime(orderBase.getDepartTime());
                // systemFeign.updateUsageCount(updateUsageCountDto);
                systemService.updateUsageCount(updateUsageCountDto);
            }
        }

        RequestAppendPlaceOrderDto requestAppendPlaceOrderDto = new RequestAppendPlaceOrderDto();
        requestAppendPlaceOrderDto.setCoreOrderId(Long.valueOf(cacheOrder.getCoreOrderId()));
        requestAppendPlaceOrderDto.setEstimateId(appendCarTypeParam.getEstimateId());
        requestAppendPlaceOrderDto.setCars(appendCarTypeParam.getCars());

        // requestAppendPlaceOrderDto.setDepartPoi(orderBase.getDepartPoi());
        // requestAppendPlaceOrderDto.setDestPoi(orderBase.getDestPoi());

        /**
         * 途经点判断
         * 只向首汽下单，将来要是支持的途径点多的话，此逻辑就要去掉了
         *
         */
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX
                + String.valueOf(orderParam.getEstimateId()))) {
            List<SelectedCar> cacheCars = JSONUtil
                    .toList(redisUtil.get(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX
                            + String.valueOf(orderParam.getEstimateId())).toString(), SelectedCar.class);
            List<Integer> carLevels = new ArrayList<>();

            for (SelectedCar sc : requestAppendPlaceOrderDto.getCars()) {
                Long cnt = cacheCars.stream().filter(t -> t.getCarLevel() == sc.getCarLevel()).count();
                if (cnt > 0 && !carLevels.contains(sc.getCarLevel()))
                    carLevels.add(sc.getCarLevel());
            }

            if (carLevels.size() == 0) {
                requestAppendPlaceOrderDto.getCars().clear();
                // throw new Exception("当前选择的车型不支持途经点")
            } else {
                List<SelectedCar> cars = cacheCars.stream().filter(t -> carLevels.contains(t.getCarLevel()))
                        .collect(Collectors.toList());
                requestAppendPlaceOrderDto.setCars(cars);
            }

            if (null != customObject && customObject.containsKey("passingPoints")) {
                requestAppendPlaceOrderDto.setPassingPoints(customObject.getJSONArray("passingPoints"));
            }
        }

        // begin 针对南航的特殊处理逻辑

        if (IPATH_SPECIAL_CUSTOMER != null && orderParam.getCompanyId().equals(IPATH_SPECIAL_CUSTOMER)) {
            for (SelectedCar selectedCar : requestAppendPlaceOrderDto.getCars()) {
                if ("普通优选".equals(selectedCar.getCarLevelName())
                        && selectedCar.getCarLevel() == CarLevel.COMFORTABLE.getCode()) {
                    selectedCar.setCarLevel(CarLevel.NORMAL.getCode());
                }
            }
        }
        // end

        // 修复 追加车型过程中有司机接单导致，司机接单后追加车型成功导致状态错误
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_STATUS + orderBase.getId())) {
            String s = (String) redisUtil.get(CacheConsts.REDIS_KEY_ORDER_STATUS + orderBase.getId());
            String[] split = s.split("_");
            if (split.length >= 1) {
                Short orderStatus = Short.valueOf(split[0]);
                Short state = orderBase.getState();
                // 判断如果是预约管家则不更新状态，客服取消没有经过中台回调 update by Lok 20230324_fix 取消单状态显示
                if (!this.isBooking(orderBase) && null != orderStatus && orderStatus != 1) {
                    return BaseResponse.Builder.success();
                }
            }
        }

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        JSONObject paramObject = new JSONObject(requestAppendPlaceOrderDto);
        remoteCallDto.setPath("/api/v2/ordercore/appendPlaceOrder");
        remoteCallDto.setContent(paramObject.toString());
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);

        BaseResponse resp;
        // 需求:极速派单调度 下单(追加车型)
        boolean dispatchAppendPlaceOrder = dispatchService.appendPlaceOrder(requestAppendPlaceOrderDto, orderBase,
                customObject);
        if (dispatchAppendPlaceOrder) {
            resp = new BaseResponse();
            resp.setCode(0);
        } else {
            resp = remoteCallFeign.call(remoteCallDto);
        }

        // begin 针对南航的特殊处理逻辑

        if (IPATH_SPECIAL_CUSTOMER != null && orderParam.getCompanyId().equals(IPATH_SPECIAL_CUSTOMER)) {
            for (SelectedCar selectedCar : appendCarTypeParam.getCars()) {
                if ("普通优选".equals(selectedCar.getCarLevelName())
                        && selectedCar.getCarLevel() == CarLevel.NORMAL.getCode()) {
                    selectedCar.setCarLevel(CarLevel.COMFORTABLE.getCode());
                }
            }
        }
        // end

        if (resp.getCode() != 0) {
            if (isPrePay) {
                notifyBillService(appendCarTypeParam.getOrderId());
            }
            // log.info("appendPlaceOrder failed: estimateId={}, iCarDcOrderId={},
            // errorMsg={}", appendCarTypeParam.getEstimateId(),
            // cacheOrder.getCoreOrderId(), resp.getMessage());
            throw new BusinessException(resp.getMessage());
        }

        orderBase.setCustomInfo(customObject == null ? null : customObject.toString());
        orderBase.setUpdateTime(new Date());
        orderBase.setState(null);
        orderBase.setPartnerOrderId(orderParam.getPartnerOrderId());
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);

        if (newMaxEstimatePrice.compareTo(oldMaxEstimatePrice) == 1) {
            BigDecimal additionalAmount = newMaxEstimatePrice.subtract(oldMaxEstimatePrice);
            BigDecimal newFrozenAmount = additionalAmount.multiply(FROZEN_AMOUNT_RATE);
            // requestCheckOrderDto.setFrozenAmount(newFrozenAmount);
            // requestCheckOrderDto.setFrozenAmount(newMaxEstimatePrice);
            frozenAmountDiff = newFrozenAmount;

            if (orderSource != null) {
                orderSource.setEstimatePrice(newMaxEstimatePrice);
                orderSource.setIpathEstimatePrice(newMaxEstimatePrice);
                orderSource.setPlatformEstimatePrice(newMaxEstimatePrice);
                orderSource.setState(null);
                orderSourceMapper.updateByPrimaryKeySelective(orderSource);
            }
        }

        // 新增车型通知账单服务进行金额冻结
        if (newMaxEstimatePrice.compareTo(BigDecimal.ZERO) == 1) {
            RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();
            requestOrderNotifyDto.setEventType((short) 1);
            requestOrderNotifyDto.setCompanyId(appendCarTypeParam.getCompanyId());
            requestOrderNotifyDto.setOrderId(appendCarTypeParam.getOrderId());
            requestOrderNotifyDto.setAccountId(orderParam.getAccountId());
            requestOrderNotifyDto.setEstimatePrice(newMaxEstimatePrice);
            orderTask.notifyBillService(requestOrderNotifyDto);
        }

        // 通知优惠券服务
        if (couponIds.size() > 0) {
            RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();
            BeanUtil.copyProperties(orderParam, requestOrderNotifyDto, false);
            requestOrderNotifyDto.setEventType((short) 1);
            requestOrderNotifyDto.setOrderId(appendCarTypeParam.getOrderId());
            requestOrderNotifyDto.setCouponIds(couponIds);
            if (cacheOrder.getCouponIds() == null) {
                cacheOrder.setCouponIds(new ArrayList<>());
            }
            cacheOrder.getCouponIds().addAll(couponIds);

            orderTask.notifyCouponService(requestOrderNotifyDto);
        }

        List<SelectedCar> cars = new ArrayList<>();
        if (copySelectedCars.size() > 0) {
            cars.addAll(copySelectedCars);
        } else {
            cars.addAll(appendCarTypeParam.getCars());
        }

        try {
            List<CarTypeLabelEstimateVo> cacheCarTypeLavelEstimateResultList = getCallingCarTypeLabelCars(
                    appendCarTypeParam.getEstimateId(),
                    copySelectedCars);

            if (cacheCarTypeLavelEstimateResultList != null) {
                for (CarTypeLabelEstimateVo cacheEstimateResult : cacheCarTypeLavelEstimateResultList) {
                    boolean isAdded = false;
                    for (CarTypeLabelEstimateVo cacheEstimateResult1 : cacheOrder.getCallingCarTypeLabelCars()) {
                        if (Objects.equals(cacheEstimateResult1.getLabelCode(), cacheEstimateResult.getLabelCode())) {
                            cacheEstimateResult1.getList().addAll(cacheEstimateResult.getList());

                            BigDecimal max = cacheEstimateResult1.getList().stream()
                                    .map(EstimateCar::getEstimatePrice)
                                    .distinct()
                                    .max(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);

                            BigDecimal min = cacheEstimateResult1.getList().stream()
                                    .map(EstimateCar::getEstimatePrice)
                                    .distinct()
                                    .min(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);

                            cacheEstimateResult1.setPriceLab(NumberUtil.equals(max, min) ? StrUtil.toString(max)
                                    : StrUtil.format("{}-{}", min, max));

                            isAdded = true;
                            break;
                        }
                    }
                    if (!isAdded) {
                        cacheOrder.getCallingCarTypeLabelCars().add(cacheEstimateResult);
                    }
                }
            }
        } catch (Exception e) {
            log.info("标签车型异常 ==> ", e);
        }

        List<CacheEstimateResult> cacheEstimateResultList = getCallingCars(appendCarTypeParam.getEstimateId(),
                copySelectedCars);

        log.info("appendPlaceOrder: appendCars={}, cacheCars={}", cacheEstimateResultList, cacheOrder.getCallingCars());
        if (cacheEstimateResultList != null) {
            for (CacheEstimateResult cacheEstimateResult : cacheEstimateResultList) {
                boolean isAdded = false;
                for (CacheEstimateResult cacheEstimateResult1 : cacheOrder.getCallingCars()) {
                    if (cacheEstimateResult1.getId() == cacheEstimateResult.getId()) {
                        cacheEstimateResult1.getList().addAll(cacheEstimateResult.getList());
                        isAdded = true;
                        break;
                    }
                }
                if (!isAdded) {
                    cacheOrder.getCallingCars().add(cacheEstimateResult);
                }
            }

            log.info("appendPlaceOrder: newCacheCars={}", cacheOrder.getCallingCars());

            // 修复 追加车型过程中有司机接单导致，司机接单后追加车型成功导致状态错误
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_STATUS + orderBase.getId())) {
                String s = (String) redisUtil.get(CacheConsts.REDIS_KEY_ORDER_STATUS + orderBase.getId());
                String[] split = s.split("_");
                if (split.length >= 1) {
                    Short orderStatus = Short.valueOf(split[0]);
                    Short state = orderBase.getState();
                    // 判断如果是预约管家则不更新状态，客服取消没有经过中台回调 update by Lok 20230324_fix 取消单状态显示
                    if (!this.isBooking(orderBase) && null != orderStatus) {
                        cacheOrder.setState(orderStatus);
                    }
                }
            }

            long currentExpire = redisUtil
                    .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + appendCarTypeParam.getUserId().toString());
            redisUtil.hashPut(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + appendCarTypeParam.getUserId().toString(),
                    appendCarTypeParam.getOrderId().toString(), JSONUtil.toJsonStr(cacheOrder), currentExpire);
        }

        // add by guoxin 通知report-service
        RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();
        requestOrderNotifyDto.setOrderId(appendCarTypeParam.getOrderId());
        requestOrderNotifyDto.setCompanyId(appendCarTypeParam.getCompanyId());
        requestOrderNotifyDto.setUserId(appendCarTypeParam.getUserId());
        requestOrderNotifyDto.setCacheEstimateResults(cacheOrder.getCallingCars());
        requestOrderNotifyDto.setEstimateId(appendCarTypeParam.getEstimateId());
        requestOrderNotifyDto.setEstimatePrice(frozenAmountDiff);
        if (isUpgradeAppend) {
            requestOrderNotifyDto.setIsUpgrade(true);
        } else {
            requestOrderNotifyDto.setIsUpgrade(orderBase.getIsUpgrade());
        }
        if (isPrePay) {
            requestOrderNotifyDto.setIsPrePay(true);
        } else {
            requestOrderNotifyDto.setIsPrePay(orderBase.getIsPrepay());
        }
        requestOrderNotifyDto.setCustomInfo(orderBase.getCustomInfo());
        orderTask.notifyReportServiceAppendPlaceOrder(requestOrderNotifyDto);

        // 通知配置服务
        if (frozenAmountDiff.compareTo(BigDecimal.ZERO) == 1) {
            RequestOrderNotifyDto requestOrderNotifyDto1 = new RequestOrderNotifyDto();
            requestOrderNotifyDto1.setOrderId(appendCarTypeParam.getOrderId());
            requestOrderNotifyDto1.setFrozenAmount(frozenAmountDiff);
            requestOrderNotifyDto1.setSceneId(orderParam.getSceneId());
            requestOrderNotifyDto1.setEventType((short) 1);
            requestOrderNotifyDto1.setCompanyId(orderBase.getCompanyId());
            requestOrderNotifyDto1.setAccountId(orderParam.getAccountId());
            requestOrderNotifyDto1.setEstimatePrice(frozenAmountDiff);
            requestOrderNotifyDto1.setUserId(orderBase.getUserId());
            requestOrderNotifyDto1.setScenePublishId(orderBase.getScenePublishId());
            requestOrderNotifyDto1.setServiceType(orderBase.getServiceType());
            orderTask.notifyConfigureService(requestOrderNotifyDto1);
        }

        redisUtil.set(CacheConsts.REDIS_KEY_APPEND_ORDER_PREFIX + orderBase.getId(), true,
                CacheConsts.ORDER_CACHE_EXPIRE_TIME);

        Long endTime = System.currentTimeMillis();
        iLog.setResMillsecond(endTime - startTime);
        logService.saveLogAsync(iLog, JSONUtil.toJsonStr(BaseResponse.Builder.success()));

        return BaseResponse.Builder.success();
    }

    private void notifyBillService(Long orderId) throws Exception {
        RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();
        requestOrderNotifyDto.setEventType((short) -100);
        requestOrderNotifyDto.setOrderId(orderId);
        orderTask.notifyBillService(requestOrderNotifyDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setOrderComplaint(FeedbackParam param) throws Exception {
        Long orderId = param.getOrderId();
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        if (orderBase == null) {
            throw new BusinessException("订单不存在");
        }
        if (!orderBase.getUserId().equals(param.getUserId())) {
            throw new BusinessException("只能投诉自己的订单");
        }

        boolean repeatComplaint = false;
        Integer complaintType = 0;
        JSONObject customJsonObject = new JSONObject(orderBase.getCustomInfo());

        if (null == param.getFeedbackTypeId() || param.getFeedbackTypeId() != 7) { // 非自动投诉，验证是否被主动投诉过
            complaintType = customJsonObject.getInt("complaintType", 0);
            repeatComplaint = (complaintType & ComplaintTypeConst.ACTIVE) == ComplaintTypeConst.ACTIVE;
            complaintType = complaintType | ComplaintTypeConst.ACTIVE;
        }

        if (repeatComplaint) {
            UserBase ub = userBaseMapper.selectByPrimaryKey(orderBase.getUserId());
            String msg = resourceUtil.getResource(orderBase.getUserId(), orderBase.getCompanyId(),
                    null == ub ? 1 : ub.getLanguage(),
                    ResourceKeyConsts.RESOURCE_KEY_TIP_SOURCE_COMPLAINT_REPEAT);
            throw new BusinessException(msg);
        } else if (complaintType > 0) {
            customJsonObject.set("complaintType", complaintType);
            orderBase.setCustomInfo(customJsonObject.toString());
            orderBaseMapper.updateByPrimaryKey(orderBase);
        }
        if (null != param.getIsConfirmAbnormal() && param.getIsConfirmAbnormal()) {
            confirmAbnormal(orderId, param.getUserId(), param.getCompanyId(), null);
        }

        int level = 1;

        OrderComplaint orderComplaint = new OrderComplaint();
        orderComplaint.setId(snowFlakeUtil.getNextId());
        orderComplaint.setOrderId(orderId);
        orderComplaint.setFeedback(param.getFeedback());
        orderComplaint.setState(0);
        orderComplaint.setCreateTime(new Date());

        if (null != param.getComplaints() && !param.getComplaints().isEmpty()) {
            if (null == param.getFeedbackTypeId()) {
                JSONObject jsonObject = param.getComplaints().getJSONObject(0);
                if (jsonObject.containsKey("id")) {
                    String id = jsonObject.getStr("id");
                    if (StrUtil.isNotEmpty(id)) {
                        param.setFeedbackTypeId(Short.valueOf(id.substring(0, 1)));
                    }
                }
            }

            String labels = JSONUtil.toJsonStr(param.getComplaints());
            orderComplaint.setComplaintLabels(labels);

            if (null != param.getComplaints()) {
                for (int i = 0; i < param.getComplaints().size(); i++) {
                    JSONObject eachObject = param.getComplaints().getJSONObject(i);
                    if (null != eachObject) {
                        if (level < eachObject.getInt("level", 0)) {
                            level = eachObject.getInt("level");
                        }
                    }
                }
            }
        }

        orderComplaintMapper.insertSelective(orderComplaint);

        RequestBillNotifyComplaintDto complaintDto = new RequestBillNotifyComplaintDto();
        BeanUtil.copyProperties(orderComplaint, complaintDto, true);
        JSONObject complaintLabelsObject = new JSONObject();
        complaintLabelsObject.set("complaintLabels", param.getComplaints());
        complaintDto.setComplaintLabels(complaintLabelsObject);
        complaintDto.setLevel(level);
        complaintDto.setScore(param.getScore());
        complaintDto.setFeedbackTypeId(
                (null != param.getIsConfirmAbnormal() && param.getIsConfirmAbnormal()) ? 9 : param.getFeedbackTypeId());
        // 设置source值,默认3-移动端投诉
        if (complaintDto.getSource() == null) {
            complaintDto.setSource(3);
        }
        // end
        orderTask.notifyReportServiceComplaint(complaintDto, orderBase.getCompanyId());
        // end
        return;
    }

    /**
     * 确认异常规则
     */
    public void confirmAbnormal(Long orderId, Long userId, Long companyId, String comment) throws Exception {
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        if (null == orderBase)
            return;

        orderBase.setAbnormalStatus((short) 2);
        orderBase.setRemark(comment);
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);

        // 如果是微众需要通知报表确认结果，出账单用
        if (orderLimitService.isOpenAnbnormalConfirmedNotifyReport(companyId)) {
            String exceptionResult = "无异常";
            if (StrUtil.isNotEmpty(orderBase.getCustomInfo())) {
                JSONObject customJsonObject = new JSONObject(orderBase.getCustomInfo());
                if (null != customJsonObject && customJsonObject.containsKey("complaintType")) {
                    if ((customJsonObject.getInt("complaintType", 0)
                            & ComplaintTypeConst.ACTIVE) == ComplaintTypeConst.ACTIVE)
                        exceptionResult = "异常";
                }
            }

            RequestNotifyReportForCompletionDto reportForCompletionDto = new RequestNotifyReportForCompletionDto();
            // 此处不要传公司id
            reportForCompletionDto.setOrderId(orderBase.getId());
            reportForCompletionDto.setBackupField01(exceptionResult);
            reportForCompletionDto.setBackupField02(comment);
            orderTask.notifyReportServiceCompleted(reportForCompletionDto);
        }

        if (redisUtil.hashHasKey(
                CacheConsts.REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX + String.valueOf(companyId),
                String.valueOf(userId))) {
            redisUtil.hashDelete(
                    CacheConsts.REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX + String.valueOf(companyId),
                    String.valueOf(userId));
        }
    }

    /**
     * 支付中心通知支付状态变化
     */
    public void orderPayNotify(OrderPayNotifyParam param) throws Exception {
        Boolean isPrePay = false;
        String transNo = param.getTransNo();
        Long orderId = 0L;
        String estimateId = null;
        Long userId = null;

        Object orderIdObject = redisUtil.hashGet(CacheConsts.REDIS_KEY_TRANSORDERNO_USERPAY_PREFIX + transNo,
                String.valueOf(1));
        if (null != orderIdObject) {
            orderId = Long.valueOf(orderIdObject.toString());
            isPrePay = true;
        }

        Short state = param.getState();
        if (state == 1) { // 待支付 = 0,已支付 = 1,支付失败 = 2
            if (isPrePay) {// 大额预付自动下单
                CreateOrderParam estimatePara = null;
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId)) {
                    estimatePara = (CreateOrderParam) redisUtil
                            .get(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId);
                } else {
                    notifyBillService(orderId);
                    return;
                }
                log.info("大额预付自动下单,estimatePara: {}", JSONUtil.toJsonStr(estimatePara));
                estimatePara.setPayType(param.getPayWay());
                estimateId = estimatePara.getEstimateId();
                userId = estimatePara.getUserId();

                autoPlaceOrder(estimatePara, orderId);

                this.bindingPartnerOrderId(orderId);
            } else {
                RequestReportNotifyPaidDto paidDto = new RequestReportNotifyPaidDto();
                BeanUtil.copyProperties(param, paidDto, true);
                orderTask.notifyReportServicePaid(paidDto);

                if (null != param.getOrderPayInfo() && param.getOrderPayInfo().size() > 0) {
                    for (OrderPayDetail orderPayDetail : param.getOrderPayInfo()) {
                        if (orderPayDetail.getPayType() != 1)
                            continue;
                        OrderApply orderApply = orderApplyMapper.selectByOrderId(orderPayDetail.getOrderId());
                        if (orderApply != null && orderApply.getState() == 4) {
                            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderPayDetail.getOrderId());
                            RequestOrderPayEventDto payEventDto = new RequestOrderPayEventDto();
                            payEventDto.setCompanyId(param.getCompanyId());
                            payEventDto.setOrderId(orderPayDetail.getOrderId());
                            payEventDto.setIsReject(true);
                            payEventDto.setUserId(orderBase.getUserId());
                            payEventDto.setRecoveryAmount(orderPayDetail.getMoney());
                            payEventDto.setSceneId(orderBase.getSceneId());
                            orderTask.notifyConfigureServiceForReject(payEventDto);

                            this.bindingPartnerOrderId(orderId);
                        }
                    }

                    for (OrderPayDetail orderPayDetail : param.getOrderPayInfo()) {
                        RequestOrderPayEventDto payEventDto = new RequestOrderPayEventDto();
                        payEventDto.setCompanyId(param.getCompanyId());
                        payEventDto.setOrderId(orderPayDetail.getOrderId());
                        orderTask.notifyCdsMstServiceForReject(payEventDto);
                    }
                }
            }
        }
    }

    private void bindingPartnerOrderId(Long orderId) throws Exception {
        try {
            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
            if (null == orderBase)
                return;
            String partnerOrderId = orderBase.getPartnerOrderId();
            if (StringUtils.isBlank(partnerOrderId)) {
                // 判断公司是否需要获取第三方订单id
                if (orderLimitService.isOpenPartnerOrderId(orderBase.getCompanyId())) {
                    String partnerOrderIdResult = this.getPartnerOrderId(orderBase.getCompanyId(),
                            orderBase.getUserId());
                    if (StringUtils.isNotBlank(partnerOrderIdResult)) {
                        orderBase.setPartnerOrderId(partnerOrderIdResult);
                        orderBaseMapper.updateByPrimaryKeySelective(orderBase);
                        log.info("bindingPartnerOrderId ===>成功 orderId:{},partnerOrderId:{}", orderId,
                                partnerOrderIdResult);
                    } else {
                        log.info("bindingPartnerOrderId ===>失败 orderId:{},partnerOrderId:{}", orderId,
                                partnerOrderIdResult);
                    }
                }
            }
        } catch (Exception e) {
            log.error("bindingPartnerOrderId ===> 绑定三方id异常：{}", e);
        }
    }

    /**
     * 查询订单评价
     */
    public Map<String, Object> getOrderFeedback(OrderIdParam param) throws Exception {
        if (param == null || param.getOrderId() == null) {
            throw new BusinessException("没有订单参数");
        }
        // 改用通过report-core获取orderFeedback
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("orderId", param.getOrderId());
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath("/api/v2/reportcore/ReportCore/GetOrderComplaintDetail");
        remoteCallDto.setContent(jsonObject.toString());
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            throw new BusinessException(response.getMessage());
        }
        JSONObject feedback = new JSONObject(response.getData());
        DriverInfo driverInfo = new DriverInfo();
        if (feedback.containsKey("driverInfo")) {
            JSONObject jsonObject3 = new JSONObject(feedback.get("driverInfo"));
            driverInfo.setName(jsonObject3.get("name") == null ? null : jsonObject3.get("name").toString());
            driverInfo.setLevel(jsonObject3.get("level") == null ? null : jsonObject3.get("level").toString());
            driverInfo.setVehicleColor(
                    jsonObject3.get("vehicleColor") == null ? null : jsonObject3.get("vehicleColor").toString());
            driverInfo.setVehicleNo(
                    jsonObject3.get("vehicleNo") == null ? null : jsonObject3.get("vehicleNo").toString());
            driverInfo.setPhone(jsonObject3.get("phone") == null ? null : jsonObject3.get("phone").toString());
            driverInfo.setVehicleModel(
                    jsonObject3.get("vehicleModel") == null ? null : jsonObject3.get("vehicleModel").toString());
            driverInfo.setPhoneVirtual(
                    jsonObject3.get("phoneVirtual") == null ? null : jsonObject3.get("phoneVirtual").toString());
        }
        FeedbackVo feedbackVo = new FeedbackVo();
        feedbackVo.setOrderId(param.getOrderId());
        ComplaintInfo complaintInfo = new ComplaintInfo();
        JSONArray jsonArray = new JSONArray();
        if (feedback.containsKey("orderFeedback")) {
            JSONObject jsonObject1 = new JSONObject(feedback.get("orderFeedback"));
            feedbackVo.setSourceNameCn(jsonObject1.getStr("sourceNameCn"));
            feedbackVo.setScore(jsonObject1.getShort("score"));
            feedbackVo.setFeedback(jsonObject1.getStr("feedback"));
        }
        if (feedback.containsKey("complaintInfo")) {
            JSONObject jsonObject2 = new JSONObject(feedback.get("complaintInfo"));
            complaintInfo.setState(jsonObject2.getShort("state"));
            JSONObject complaintsObject = new JSONObject(jsonObject2.get("complaintLabels"));
            if (complaintsObject != null) {
                complaintInfo.setTpyes(complaintsObject.getJSONArray("complaintLabels"));
            } else {
                complaintInfo.setTpyes(jsonArray);
            }
            if (jsonObject2.containsKey("replay")) {
                feedbackVo.setReply(jsonObject2.getStr("replay"));
            }
            feedbackVo.setComplaint(complaintInfo);
        }
        // end

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("driverInfo", driverInfo);
        resultMap.put("feedbackInfo", feedbackVo);
        return resultMap;
    }

    public FeedbackVo getOrderEvaluate(OrderIdParam param) throws Exception {
        if (param == null || param.getOrderId() == null) {
            throw new BusinessException("没有订单参数");
        }

        OrderFeedback feedback = orderFeedbackMapper.selectByOrderId(param.getOrderId());

        if (null == feedback)
            return null;

        FeedbackVo feedbackVo = new FeedbackVo();
        feedbackVo.setOrderId(param.getOrderId());
        feedbackVo.setScore(feedback.getScore().shortValue());
        feedbackVo.setFeedback(feedback.getFeedback());
        if (StrUtil.isNotEmpty(feedback.getEvaluateLabels()))
            feedbackVo.setTpyes(JSONUtil.parseArray(feedback.getEvaluateLabels()));
        return feedbackVo;
    }

    public static String encodeURI(String url) throws Exception {
        String encode = URLEncoder.encode(url, "UTF-8");

        return encode.replace("%3A", ":").replace("%2F", "/");
    }

    private static Multipart getMultipart(String content, String remoteFile, String fileName) throws Exception {
        Multipart multipart = new MimeMultipart();
        // 向multipart中添加正文
        BodyPart contentBodyPart = new MimeBodyPart();
        contentBodyPart.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentBodyPart);

        // 向multipart中添加远程附件
        // multipart.addBodyPart(getBodyPart(encodeURI(remoteFile), fileName));
        multipart.addBodyPart(getBodyPart(remoteFile, fileName));

        return multipart;
    }

    public static BodyPart getBodyPart(String path, String fileName) throws Exception {
        BodyPart bodyPart = new MimeBodyPart();
        // 根据附件路径获取文件,
        DataHandler dh = null;
        dh = new DataHandler(new URLDataSource(new URL(path)));
        bodyPart.setDataHandler(dh);

        System.out.println(fileName);
        bodyPart.setFileName(fileName);
        return bodyPart;
    }

    private void checkOrderBasicParam(CreateOrderParam orderParam) throws Exception {
        if (orderParam.getSceneId() == null) {
            throw new BusinessException(OrderConstant.IS_EMPTY_SCENEID);
        }

        if (orderParam.getDestCityCode() == null || orderParam.getDestLat() == null
                || orderParam.getDestLng() == null || orderParam.getDestLocation() == null
                || orderParam.getDestLocationName() == null) {
            throw new BusinessException(OrderConstant.IS_EMPTY_DESTPARAMS);
        }

        // 1 实时, 2 预约, 6 接机 7 接站 20 包车
        if (orderParam.getServiceType() == 1 || orderParam.getServiceType() == 2 || orderParam.getServiceType() == 20) {

            if (orderParam.getDepartCityCode() == null || orderParam.getDepartLat() == null
                    || orderParam.getDepartLng() == null || orderParam.getPickupLocation() == null
                    || orderParam.getPickupLocationName() == null) {
                throw new BusinessException(OrderConstant.IS_EMPTY_PICKUPPARAMS);
            }

            if (orderParam.getServiceType() == 2 && orderParam.getDepartTime() == null) {
                throw new BusinessException("预约用车时间参数不正确");
            }
        } else if (orderParam.getServiceType() == 6) {
            if (orderParam.getFlightNumber() == null) {
                throw new BusinessException("接机用车缺少航班号参数");
            }
        } else if (orderParam.getServiceType() == 7) {
            if (orderParam.getTrainNumber() == null) {
                throw new BusinessException("接站用车缺少列车号参数");
            }

            // 列车号赋值给航班号吧
            orderParam.setFlightNumber(orderParam.getTrainNumber());
        }

        if (null != orderParam.getIsUserPay() && !orderParam.getIsUserPay()
                && StringUtils.isBlank(orderParam.getIsNotUserPayReason())) {
            throw new BusinessException("非个人支付行程原因不能为空");
        }
    }

    public void updateOrderDetails(JSONObject dataObject) throws Exception {

        Short orderStatus = dataObject.getShort("status");
        String coreOrderId = dataObject.getStr("coreOrderId");
        Long originOrderId = dataObject.getLong("partnerOrderId");
        JSONObject orderDetail = (JSONObject) dataObject.get("orderDetail");
        JSONObject driverObject = (JSONObject) orderDetail.get("driver");

        if (orderStatus == 2) {// 状态2时，如果司机信息为空，直接返回，不再处理
            if (orderDetail.isNull("driver") || ObjectUtil.isEmpty(orderDetail.getJSONObject("driver"))) {
                log.info(originOrderId + ",状态2司机信息为空，不做处理");
                return;
            }
        }

        if (ObjectUtil.equals(orderStatus, 5) || ObjectUtil.equals(orderStatus, 7)) {
            redisUtil.listLeftPush(StrUtil.format(CacheConsts.REDIS_KEY_ORDER_STATUS_HISTORY, originOrderId),
                    orderStatus, CacheConsts.ONE_HOUR);
        } else {
            redisUtil.listLeftPush(StrUtil.format(CacheConsts.REDIS_KEY_ORDER_STATUS_HISTORY, originOrderId),
                    orderStatus, CacheConsts.ONE_DAY);
        }

        if (orderStatus == 5) {
            redisUtil.set(OrderConstant.ORDER_IS_SETTLING + originOrderId, 1, 15);
            log.info(originOrderId + "结算中(行程结束，标记结算中)");
        }

        // 通知新报表
        delayProducer.sendDelayReport(dataObject);

        CacheOrder cacheOrder = null;
        CacheICarOrder cacheICarOrder = (CacheICarOrder) redisUtil
                .get(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId);
        if (cacheICarOrder != null) {
            cacheOrder = cacheUtil.getUserCacheOrder(cacheICarOrder.getUserId(), cacheICarOrder.getOrderId());
            if (orderStatus != 7) {
                // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                redisUtil.set(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString(),
                        orderStatus.toString() + "_null", CacheConsts.TEN_SECOND);
            }
        }

        if (orderStatus == 1 || orderStatus == 6) {// 不处理状态1,6
            return;
        } else if (orderStatus == 7) {// 状态为7时，如果是用户主动取消，不处理此消息
            if (cacheICarOrder == null) {
                // 处理一下包车
                OrderBase ob = orderBaseMapper.selectByPrimaryKey(originOrderId);
                if (null == ob || ob.getState() == 7) {
                    log.info("updateOrderDetails: 取消单限制成功");
                    return;
                }

                // 如果上面限制不住，确认下此订单是否被用户主动取消
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + originOrderId)) {
                    try {
                        Boolean isCanceled = Boolean
                                .valueOf(redisUtil.get(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + originOrderId)
                                        .toString());
                        if (isCanceled) {
                            log.info("订单{}已被取消，但还未处理完", originOrderId);
                            return;
                        }
                    } catch (Exception ex) {
                        log.error("处理订单回传[检验取消单]出现异常【OrderService->updateOrderDetails,orderId:{}】", originOrderId, ex);
                    }
                }
            } else {
                boolean isOne = redisUtil.hasKey(OrderConstant.ORDER_IS_RE_PLACE_ORDER + cacheICarOrder.getOrderId());
                boolean isRe = redisUtil
                        .hasKey(OrderConstant.ORDER_IS_OPEN_RE_PLACE_ORDER + cacheICarOrder.getOrderId());
                if (isOne) {

                    // Short state = ob.getState();
                    // add by kakarotto 20230427 平台/司机取消重新派单(单个选单个平台取消司机后下单)
                    boolean isSuccess = this.rePlaceOrderOneSource(dataObject, cacheICarOrder.getOrderId());

                    log.info("触发取消同一司机接单重新派单===>[orderId:{},isSucces:{}]", originOrderId, isSuccess);
                    if (isSuccess) {
                        orderStatus = 1;
                        // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                        redisUtil.set(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString(),
                                orderStatus.toString() + "_null", CacheConsts.TEN_SECOND);

                        // 更新order缓存
                        cacheOrder.setState(orderStatus);

                        long currentExpire = redisUtil
                                .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX
                                        + cacheICarOrder.getUserId().toString());
                        redisUtil.hashPut(
                                CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + cacheICarOrder.getUserId().toString(),
                                cacheICarOrder.getOrderId().toString(), JSONUtil.toJsonStr(cacheOrder), currentExpire);

                        // 更新icarorder缓存
                        cacheICarOrder.setState(orderStatus);
                        currentExpire = redisUtil.getExpire(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId);
                        redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId, cacheICarOrder,
                                currentExpire);
                        return;
                    }
                }

                if (!isOne && rePlaceOrderAfterCoreCancelSwitch && isRe) {

                    // add by kakarotto 20230427 平台/司机取消重新派单
                    // 说明：【cacheICarOrder】用户取消会删除这个缓存，中台回调会保存这个缓存
                    boolean isSuccess = this.rePlaceOrderAfterCoreCancelAfreshPlaceOrder(dataObject,
                            cacheICarOrder.getOrderId());

                    log.info("触发取消重新派单===>[orderId:{},isSucces:{}]", originOrderId, isSuccess);
                    if (isSuccess) {
                        orderStatus = 1;
                        // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                        redisUtil.set(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString(),
                                orderStatus.toString() + "_null", CacheConsts.TEN_SECOND);

                        // 更新order缓存
                        cacheOrder.setState(orderStatus);

                        long currentExpire = redisUtil
                                .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX
                                        + cacheICarOrder.getUserId().toString());
                        redisUtil.hashPut(
                                CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + cacheICarOrder.getUserId().toString(),
                                cacheICarOrder.getOrderId().toString(), JSONUtil.toJsonStr(cacheOrder), currentExpire);

                        // 更新icarorder缓存
                        cacheICarOrder.setState(orderStatus);
                        currentExpire = redisUtil.getExpire(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId);
                        redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId, cacheICarOrder,
                                currentExpire);

                        return;
                    }
                }
            }
        }

        OrderBase orderBase = null;

        if (cacheICarOrder != null) {
            orderBase = orderBaseMapper.selectByPrimaryKey(cacheICarOrder.getOrderId());
            if (orderBase == null)
                return;

            Boolean isBooking = this.isBooking(orderBase);
            if (isBooking) {
                bookingService.changeStatus(orderStatus, originOrderId);
            }

            if (isBooking
                    && ((orderStatus == 2 && originOrderId.compareTo(orderBase.getId()) != 0) || orderStatus == 7)) {
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString())) {
                    // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                    redisUtil.delete(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString());
                }

                // 修复状态7的预约管家接站产生取消费后结算 （用户取消不会走进来）
                if (orderStatus == 7) {
                    this.notifySettleCance(orderBase);
                    if (dataObject.containsKey("orderDetail")) {
                        JSONObject orderDetailJsonObject = dataObject.getJSONObject("orderDetail");
                        if (null != orderDetailJsonObject && orderDetailJsonObject.containsKey("fee")) {
                            JSONObject feeJsonObject = orderDetailJsonObject.getJSONObject("fee");
                            if (null != feeJsonObject && feeJsonObject.containsKey("totalPrice")) {
                                BigDecimal cancelFee = feeJsonObject.getBigDecimal("totalPrice", BigDecimal.ZERO);
                                if (cancelFee.compareTo(BigDecimal.ZERO) == 1) {
                                    this.notifyBookingFee(orderBase.getCompanyId(), orderBase.getId(),
                                            orderBase.getUserId(), originOrderId, Long.valueOf(coreOrderId), cancelFee);
                                }
                            }
                        }
                    }
                }

                return;
            }
        }

        JSONObject orderInfo = (JSONObject) orderDetail.get("order");
        Integer sourceId = orderInfo.getInt("carSource");

        if (cacheICarOrder != null) {
            if (orderStatus == 3 && orderInfo.get("driverArrivedTime") != null) {
                // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                redisUtil.set(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString(),
                        orderStatus.toString() + "_" + orderInfo.get("driverArrivedTime"), CacheConsts.TEN_SECOND);
            } else {
                // 中台同步过来的状态先存到缓存中，防止查询订单出现状态不一致 add by Lok 20230316_fix_订单状态更新延迟
                redisUtil.set(CacheConsts.REDIS_KEY_ORDER_STATUS + cacheICarOrder.getOrderId().toString(),
                        orderStatus.toString() + "_null", CacheConsts.TEN_SECOND);
            }
        }
        DriverInfo driver = null;
        if (driverObject != null) {
            driver = new DriverInfo();
            driver.setName(driverObject.getStr("driverName"));
            driver.setPhone(driverObject.getStr("driverPhone"));
            driver.setVehicleModel(driverObject.getStr("vehicleModel"));
            driver.setVehicleColor(driverObject.getStr("vehicleColor"));
            driver.setVehicleNo(driverObject.getStr("vehicleNo"));
            driver.setLevel(driverObject.getStr("driverLevel"));
            if (!driverObject.isNull("driverPhoneVirtual")) {
                driver.setPhoneVirtual(driverObject.getStr("driverPhoneVirtual"));
            }
            if (!driverObject.isNull("driverAvatar")) {
                driver.setAvatar(driverObject.getStr("driverAvatar"));
            }
            if (cacheOrder != null) {
                cacheOrder.setDriverInfo(driver);
            }
        }

        // 状态变化，更新缓存和数据库
        if (cacheOrder != null) {
            // 更新order缓存
            cacheOrder.setState(orderStatus);
            if (sourceId != null) {
                cacheOrder.setSourceNameCn(cacheService.getSourceName(sourceId, 1));
                cacheOrder.setSourceNameEn(cacheService.getSourceName(sourceId, 2));
            }
            long currentExpire = redisUtil
                    .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + cacheICarOrder.getUserId().toString());
            redisUtil.hashPut(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + cacheICarOrder.getUserId().toString(),
                    cacheICarOrder.getOrderId().toString(), JSONUtil.toJsonStr(cacheOrder), currentExpire);

            // 更新icarorder缓存
            cacheICarOrder.setState(orderStatus);
            currentExpire = redisUtil.getExpire(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId);
            redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId, cacheICarOrder, currentExpire);

            processOrderStatus(dataObject, Long.valueOf(cacheICarOrder.getOrderId()), orderStatus,
                    cacheOrder.getCars());
        } else {
            log.info("updateOrderDetail ==> order cache expired, process order detail");
            OrderSource orderSource = orderSourceMapper.selectByICarDcOrderId(coreOrderId);
            if (orderSource == null) {
                // 订单找不到，直接通知预约服务
                bookingService.changeStatus(orderStatus, originOrderId);
                log.info("updateOrderDetail ==> 未找到订单，可能是预约管家的订单");
                return;
            }
            processOrderStatus(dataObject, orderSource.getOrderId(), orderStatus, null);
        }
    }

    /**
     * 【推荐上车点埋点 add by kakarotto 20240523】2判断是否使用推荐上车点方法
     *
     * @return
     */
    private boolean recommendedLocation(Long companyId, Long userId, String departLat, String departLng,
            String pickupLocationName, String pickupLocation) {
        try {
            List<RecommendedLocationInfoVo> recommendedLocation = userService.getRecommendedLocation(companyId, userId,
                    departLat, departLng, null, null);
            if (CollectionUtils.isNotEmpty(recommendedLocation)) {
                List<String> collect = recommendedLocation.stream().map(o -> o.getTitle()).collect(Collectors.toList());
                if (null != pickupLocationName && collect.contains(pickupLocationName)) {
                    return true;
                }
                if (null != pickupLocation && collect.contains(pickupLocation)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("recommendedLocation ===> 异常:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 静默支付
     *
     * @param orderBase
     * @param regulationConfig
     * @return
     */
    private boolean getPayState(OrderBase orderBase, RegulationBo regulationConfig) {
        Long orderId = orderBase.getId();
        try {
            PartnerRegulationCompanyConfig config = new PartnerRegulationCompanyConfig();
            List<PartnerRegulationCompanyConfig> collect = regulationConfig.getPartnerRegulationCompanyCofigList()
                    .stream()
                    .filter(o -> o.getCompanyId().equals(orderBase.getCompanyId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                config = collect.get(0);
            }

            CdsMstDto cdsMstDto = new CdsMstDto();
            cdsMstDto.setOrderId(orderBase.getId());
            cdsMstDto.setCustomer(config.getCustomer());
            CreateOrderParam createOrderParam = new CreateOrderParam();
            createOrderParam.setCompanyId(orderBase.getCompanyId());
            createOrderParam.setUserId(orderBase.getUserId());
            cdsMstDto.setOrderParam(createOrderParam);
            cdsMstDto.setAutoPay(true);

            // 美团结算中(行程结束静默支付，调用支付)
            redisUtil.set(OrderConstant.ORDER_IS_SETTLING + orderId, 1, 15);
            cdsMstSerice.ordePay(cdsMstDto);
            return true;
        } catch (Exception e) {
            // 美团结算中(删除)(静默支付，异常)
            redisUtil.set(OrderConstant.ORDER_IS_SETTLING + orderId, 1, 2);
            log.error("静默支付失败:" + orderBase.getId());
            return false;
        }
    }

    /**
     * 校验场景自动审批
     *
     * @param departTime 订单时间
     * @param itemValue  场景规则
     * @return boolean
     */
    public boolean checkSceneAutoApproval(Date departTime, SceneItemValue itemValue, Long sceneId) {
        boolean result = false;
        List<ItemValue> rules = itemValue.getRules();
        if (StringUtils.equals(itemValue.getMode(), "weekday")) {
            // 按 时间段类型 验证
            if (ObjectUtil.isNotEmpty(rules)) {
                // 比较 订单预约时间 是否在场景规定的时间段内
                int weekDay = DateUtil.dayOfWeek(departTime);
                if (weekDay == 1) {
                    weekDay = 7;
                } else {
                    weekDay = weekDay - 1;
                }
                int weekDayKua = 0;
                if (weekDay == 1) {
                    weekDayKua = 7;
                } else {
                    weekDayKua = weekDay - 1;
                }

                for (ItemValue rule : rules) {
                    String b = rule.getB();
                    String e = rule.getE();
                    String time = DateFormatUtils.format(departTime, "HH:mm:ss");

                    if (weekDayKua == rule.getDay()) {
                        if (this.compTimeWithEqual(b, e)) {
                            if (this.compTimeWithEqual(e, time) && this.compTimeWithEqual(time, "00:00:00")) {
                                result = true;
                            }
                        }
                    }
                    if (weekDay == rule.getDay()) {
                        if (this.compTimeWithEqual(time, b) && this.compTimeWithEqual("23:59:59", time)) {
                            result = true;
                        }
                    }
                }
            }
        } else if (StringUtils.equals(itemValue.getMode(), "holiday")) {
            // 按 工作日/节假日 判断
            // {"mode":"holiday","action":1,"rules":[{"day":1,"b":"08:00","e":"17:00"},{"day":0,"b":"08:00","e":"17:00"}]}
            if (ObjectUtil.isNotEmpty(rules)) {

                boolean holiday = comSceneService.isHoliday(sceneId, departTime);
                // 跨天问题
                Date yesterday = DateUtils.addDays(departTime, -1);
                boolean yesterdayHoliday = comSceneService.isHoliday(sceneId, yesterday);

                for (ItemValue rule : rules) {
                    try {
                        String b = rule.getB() + ":00";
                        String e = rule.getE() + ":59";

                        Integer day = rule.getDay();
                        if (null != day && !yesterdayHoliday && day == 1) {
                            String time = DateFormatUtils.format(yesterday, "HH:mm:ss");
                            if (this.compTimeWithEqual(b, e)) {
                                if (this.compTimeWithEqual(e, time) && this.compTimeWithEqual(time, "00:00:00")) {
                                    result = true;
                                }
                            }
                        }

                        if (null != day && yesterdayHoliday && day == 0) {
                            String time = DateFormatUtils.format(yesterday, "HH:mm:ss");
                            if (this.compTimeWithEqual(b, e)) {
                                if (this.compTimeWithEqual(e, time) && this.compTimeWithEqual(time, "00:00:00")) {
                                    result = true;
                                }
                            }
                        }

                        if (null != day && !holiday && day == 1) {
                            String time = DateFormatUtils.format(departTime, "HH:mm:ss");
                            if (this.compTimeWithEqual(time, b) && this.compTimeWithEqual("23:59:59", time)) {
                                result = true;
                            }
                        }

                        if (null != day && holiday && day == 0) {
                            String time = DateFormatUtils.format(departTime, "HH:mm:ss");
                            if (this.compTimeWithEqual(time, b) && this.compTimeWithEqual("23:59:59", time)) {
                                result = true;
                            }
                        }
                    } catch (Exception e) {
                        log.error(
                                "checkSceneAutoApproval.isHolidayError ==> errorMsg: {}, sceneId: {}, departTime: {}",
                                e.getMessage(), sceneId, departTime);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 查询司机位置并下发
     */
    public JSONObject getDriverLocation(String iCarDcOrderId) throws Exception {
        // 调用中台接口查询司机位置
        RequestOrderQueryDto requestOrderQueryDto = new RequestOrderQueryDto();
        requestOrderQueryDto.setCoreOrderId(iCarDcOrderId);
        JSONObject paramObject = new JSONObject(requestOrderQueryDto);
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath("/api/v2/ordercore/getDriverLocation");
        remoteCallDto.setContent(paramObject.toString());
        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);
        BaseResponse response = remoteCallFeign.call(remoteCallDto);

        if (response.getCode() != 0) {
            log.info("getDriverLocation failed: iCarDcOrderId={}, errorMsg={}", iCarDcOrderId, response.getMessage());
            return null;
        }

        JSONObject dataObject = new JSONObject(response.getData());

        return dataObject;
    }

    /**
     * 大额预付后后重新下单
     *
     * @param estimateParam
     * @param orderId
     * @throws Exception
     */
    @Override
    public void autoPlaceOrder(CreateOrderParam estimateParam, Long orderId) throws Exception {
        if (estimateParam.getAppendPrePayFlag() != null && estimateParam.getAppendPrePayFlag()) {
            CreateOrderParam orderParam = (CreateOrderParam) redisUtil
                    .get(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId);
            List<SelectedCar> selectedCarList = new ArrayList<SelectedCar>();

            int callCount = 0;
            String estimateId = null;
            JSONArray estimateCarArray = null;

            CacheOrder cacheOrder = (CacheOrder) redisUtil.hashGet(
                    CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(orderParam.getUserId()),
                    String.valueOf(orderId));
            List<SelectedCar> excludeCars = new ArrayList<SelectedCar>();

            List<CacheEstimateResult> estimateResults = cacheOrder.getCallingCars();
            if (estimateResults != null) {
                for (CacheEstimateResult cacheEstimateResult : estimateResults) {
                    for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                        SelectedCar selectedCar = BeanUtil.toBean(estimateCar, SelectedCar.class);
                        excludeCars.add(selectedCar);
                    }
                }
            }

            do {
                callCount++;

                Map<String, Object> estimateMap = estimate(estimateParam, true, excludeCars);
                estimateId = estimateMap.get("estimateId").toString();
                estimateCarArray = JSONUtil.parseArray(estimateMap.get("cars"));

                log.info("orderPayNotify ==> appendEstimate reEstimateCount={}，estimateCarArray={}", callCount,
                        estimateCarArray);
            } while ((estimateCarArray == null || estimateCarArray.isEmpty()) && callCount <= 3);

            // 获取追加车型下单时选择的车型
            List<SelectedCar> cars = null;
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_APPEND_PLACEORDER_SELECTED_CARS_PREPAY + orderId)) {
                cars = (List<SelectedCar>) redisUtil
                        .get(CacheConsts.REDIS_KEY_APPEND_PLACEORDER_SELECTED_CARS_PREPAY + orderId);
                log.info("orderPayNotify ==> appendEstimate selectedCars={}", cars);
                for (int selectedCarIndex = 0; selectedCarIndex < cars.size(); selectedCarIndex++) {
                    for (int i = 0; i < estimateCarArray.size(); i++) {
                        JSONObject carDataObject = (JSONObject) estimateCarArray.get(i);
                        JSONArray carArray = JSONUtil.parseArray(carDataObject.get("list"));
                        if (carArray != null) {
                            for (int j = 0; j < carArray.size(); j++) {
                                EstimateCar carObject = carArray.get(j, EstimateCar.class);

                                // log.info("orderPayNotify ==> appendEstimate {}_{}_{}==>
                                // carLevel:{}={},carSource:{}={},carLevelName:{}={}",
                                // carObject.getCarLevel() == cars.get(selectedCarIndex).getCarLevel(),
                                // carObject.getCarSourceId().compareTo(cars.get(selectedCarIndex).getCarSourceId())==0,
                                // carObject.getCarLevelName().equals(cars.get(selectedCarIndex).getCarLevelName()),
                                // carObject.getCarLevel(),cars.get(selectedCarIndex).getCarLevel(),
                                // carObject.getCarSourceId(),cars.get(selectedCarIndex).getCarSourceId(),
                                // carObject.getCarLevelName(),cars.get(selectedCarIndex).getCarLevelName());

                                if (carObject.getCarLevel() == cars.get(selectedCarIndex).getCarLevel() &&
                                        carObject.getCarSourceId()
                                                .compareTo(cars.get(selectedCarIndex).getCarSourceId()) == 0
                                        &&
                                        carObject.getCarLevelName()
                                                .equals(cars.get(selectedCarIndex).getCarLevelName())) {
                                    // log.info("orderPayNotify ==> appendEstimate {}_{}_{}==>
                                    // carLevel:{}={},carSource:{}={},carLevelName:{}={}",
                                    // carObject.getCarLevel() == cars.get(selectedCarIndex).getCarLevel(),
                                    // carObject.getCarSourceId() == cars.get(selectedCarIndex).getCarSourceId(),
                                    // carObject.getCarLevelName().equals(cars.get(selectedCarIndex).getCarLevelName()),
                                    // carObject.getCarLevel(),cars.get(selectedCarIndex).getCarLevel(),
                                    // carObject.getCarSourceId(),cars.get(selectedCarIndex).getCarSourceId(),
                                    // carObject.getCarLevelName(),cars.get(selectedCarIndex).getCarLevelName());
                                    SelectedCar selectedCar = new SelectedCar();
                                    BeanUtil.copyProperties(carObject, selectedCar);
                                    selectedCarList.add(selectedCar);
                                }
                            }
                        }

                    }
                }
            } else {
                log.info("orderPayNotify ==> prepay=true,orderId={},大额预付后，重新下单失败；缓存车型失效", orderId);
            }

            if (selectedCarList.size() == 0) {
                log.info("orderPayNotify ==> prepay=true,orderId={},大额预付后，重新下单失败；message：no cars", orderId);
                notifyBillService(orderId);
            } else {
                log.info("orderPayNotify ==> prepay=true,orderId={},oldSelectedCars={},newSelectedCars={}", orderId,
                        cars, selectedCarList);
                AppendCarTypeParam appendCarTypeParam = new AppendCarTypeParam();
                appendCarTypeParam.setCompanyId(orderParam.getCompanyId());
                appendCarTypeParam.setUserId(orderParam.getUserId());
                appendCarTypeParam.setOrderId(orderId);
                appendCarTypeParam.setEstimateId(estimateId);
                appendCarTypeParam.setCars(selectedCarList);
                log.info("orderPayNotify ==> autoAppendPlaceOrder={}", appendCarTypeParam);
                appendPlaceOrder(appendCarTypeParam, true);
            }

            // 通知websocket，下发订单支付成功消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("orderId", orderId.toString());
            jsonObject.set("orderState", (short) 1);
            jsonObject.set("prePayFlag", true);

            String key = orderId + "_" + 1;
            String msg = jsonObject.toString();
            orderTask.notifyWebsocketService(msg, key, orderParam.getCompanyId(), null);
        } else {
            // 重新预估价、下单 手动 try catch，遇到异常 通知结算中心
            CreateOrderParam orderParam = (CreateOrderParam) redisUtil
                    .get(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId);

            List<SelectedCar> selectedCarList = new ArrayList<SelectedCar>();
            if (DateUtil.compare(new Date(), orderParam.getEstimateExpireTime()) < 0 &&
                    DateUtil.between(new Date(), orderParam.getEstimateExpireTime(), DateUnit.SECOND) > 60) {
                orderParam.setPayType(estimateParam.getPayType());
                placeOrder(orderParam, false, Long.valueOf(orderId));
            } else {
                int callCount = 0;
                String estimateId = null;
                JSONArray estimateCarArray = null;

                do {
                    callCount++;

                    Map<String, Object> estimateMap = estimate(estimateParam, false, null);
                    estimateId = estimateMap.get("estimateId").toString();
                    estimateCarArray = JSONUtil.parseArray(estimateMap.get("cars"));

                } while ((estimateCarArray == null || estimateCarArray.isEmpty()) && callCount <= 3);

                for (int selectedCarIndex = 0; selectedCarIndex < orderParam.getCars().size(); selectedCarIndex++) {
                    for (int i = 0; i < estimateCarArray.size(); i++) {
                        JSONObject carDataObject = (JSONObject) estimateCarArray.get(i);
                        JSONArray carArray = (JSONArray) carDataObject.get("list");
                        if (carArray != null) {
                            for (int j = 0; j < carArray.size(); j++) {
                                SelectedCar carObject = carArray.get(j, SelectedCar.class);
                                if (carObject.getCarLevel() == orderParam.getCars().get(selectedCarIndex).getCarLevel()
                                        &&
                                        carObject.getCarSourceId().compareTo(
                                                orderParam.getCars().get(selectedCarIndex).getCarSourceId()) == 0
                                        &&
                                        carObject.getCarLevelName()
                                                .equals(orderParam.getCars().get(selectedCarIndex).getCarLevelName())) {
                                    selectedCarList.add(carObject);
                                }
                            }
                        }

                    }
                }

                orderParam.setCars(selectedCarList);
                // log.info("写入缓存_02,orderParam:{}", orderParam);
                if (orderParam.getCars().size() == 0) {
                    log.info("orderPayNotify ==> prepay=true,orderId={},大额预付后，重新下单失败；message：no cars", orderId);
                    notifyBillService(orderId);
                } else {
                    log.info("orderPayNotify ==> prePay orderParam={}", orderParam);
                    orderParam.setEstimateId(estimateId);
                    orderParam.setPayType(estimateParam.getPayType());
                    placeOrder(orderParam, false, orderId);
                }
            }
        }
    }

    // 订单取消后自动下单
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rePlaceOrder(ReorderParam param) throws Exception {

        // OrderBase ob = orderBaseMapper.selectByPrimaryKey(1628696836594171904L);
        // OrderSource os = orderSourceMapper.selectByOrderId(1628696836594171904L);
        // UserBase ub = userBaseMapper.selectByPrimaryKey(198791621400068293L);
        // List<UserBase> as = new ArrayList<>();
        // as.add(ub);
        // orderApplyService.sendMsg(ob,os,as,(short)1,false,BigDecimal.ZERO,"");

        Map<String, Object> map = new HashMap<>();
        if (param.getOrderId() == null) {
            throw new BusinessException("orderId参数不能为空");
        }
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(parseLong(param.getOrderId()));
        if (orderBase == null) {
            throw new BusinessException("订单信息不存在，请重新下单");
        }
        CreateOrderParam createOrderParam = new CreateOrderParam();
        // CacheOrder cacheOrder = (CacheOrder)
        // redisUtil.hashGet(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX +
        // String.valueOf(orderBase.getUserId()), String.valueOf(param.getOrderId()));
        BeanUtil.copyProperties(orderBase, createOrderParam, true);

        // 原订单为预约单
        if (orderBase.getServiceType() == 2) {
            Date oldDepartTime = orderBase.getDepartTime();
            Date cancelTime = orderBase.getCancelTime();
            Long minute = DateUtil.between(oldDepartTime, cancelTime, DateUnit.MINUTE, false);
            Long minute_now = DateUtil.between(oldDepartTime, new Date(), DateUnit.MINUTE, false);
            if (minute.compareTo(0L) <= 0) {
                throw new BusinessException("取消时间已超过预约时间,请重新下单");
            } else if (minute_now.compareTo(30L) >= 0) {
                createOrderParam.setDepartTime(oldDepartTime);
            } else {
                createOrderParam.setServiceType((short) 1);
                createOrderParam.setDepartTime(new Date());
            }
        }

        Map<String, Object> estimateResultMap = estimate(createOrderParam, false, null);
        List<CacheEstimateResult> cacheEstimateResults = (List<CacheEstimateResult>) estimateResultMap.get("cars");
        List<SelectedCar> selectedCars = new ArrayList<>();
        OrderExtraParameter orderExtraParameter = new OrderExtraParameter();
        Integer upgradeCarLevel = 0;
        Integer upgradeCarSource = 0;
        Boolean isUpgrade = false;
        if (orderBase.getCustomInfo() != null) {
            JSONObject jsonObject = new JSONObject(orderBase.getCustomInfo());
            if (jsonObject.containsKey("upgrade") && jsonObject.get("upgrade") != null) {
                JSONObject upgradeObject = new JSONObject(jsonObject.get("upgrade"));
                if (upgradeObject.containsKey("upgradeCarLevel")) {
                    upgradeCarLevel = upgradeObject.getInt("upgradeCarLevel");
                }
                if (upgradeObject.containsKey("isUpgradeSuccess")) {
                    isUpgrade = upgradeObject.getBool("isUpgradeSuccess");
                }
                if (upgradeObject.containsKey("upgradeCars")) {
                    JSONObject upgradeCarObject = new JSONObject(upgradeObject.get("upgradeCars"));
                    if (upgradeCarObject.containsKey("carSourceId")) {
                        upgradeCarSource = upgradeCarObject.getInt("carSourceId");
                    }
                }
            }
            if (jsonObject.containsKey("selectedCars") && jsonObject.get("selectedCars") != null) {
                JSONArray jsonArray = jsonObject.getJSONArray("selectedCars");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = new JSONObject(jsonArray.get(i));
                        if (isUpgrade && jsonObject1.getInt("carSourceId").equals(upgradeCarSource)
                                && jsonObject1.getInt("carLevel").equals(upgradeCarLevel)) {
                            continue;
                        }
                        for (int j = 0; j < cacheEstimateResults.size(); j++) {
                            if (jsonObject1.getInt("carLevel").equals(cacheEstimateResults.get(j).getId())) {
                                List<EstimateCar> estimateCars = cacheEstimateResults.get(j).getList();
                                for (EstimateCar estimateCar : estimateCars) {
                                    if (jsonObject1.getInt("carSourceId").equals(estimateCar.getCarSourceId())) {
                                        SelectedCar selectedCar = new SelectedCar();
                                        BeanUtil.copyProperties(estimateCar, selectedCar, true);
                                        selectedCars.add(selectedCar);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // jsonObject.set("selectedCars", selectedCars);
            // orderExtraParameter.setCustomInfo(jsonObject);
        }
        if (selectedCars.size() == 0) {
            throw new BusinessException("自动下单失败，请重新下单");
        }
        createOrderParam.setCars(selectedCars);
        createOrderParam.setEstimateId(estimateResultMap.get("estimateId").toString());

        orderExtraParameter.setUserCarReason(orderBase.getUseCarReason());
        createOrderParam.setExtraParameter(orderExtraParameter);

        Map<String, Object> placeOrderResultMap = orderServiceImpl.placeOrder(createOrderParam, true, 0L);

        if (placeOrderResultMap.containsKey("orderId")) {
            map.put("orderId", placeOrderResultMap.get("orderId").toString());
        }

        if (placeOrderResultMap.containsKey("coreOrderId")) {
            map.put("coreOrderId", placeOrderResultMap.get("coreOrderId").toString());
        }

        if (placeOrderResultMap.containsKey("preDepartApplyId")) {
            map.put("preDepartApplyId", placeOrderResultMap.get("preDepartApplyId").toString());
        }

        if (placeOrderResultMap.containsKey("prePayFlag")) {
            map.put("prePayFlag", placeOrderResultMap.get("prePayFlag"));
        }

        if (placeOrderResultMap.containsKey("amount")) {
            map.put("amount", placeOrderResultMap.get("amount"));
        }

        if (placeOrderResultMap.containsKey("time")) {
            map.put("time", placeOrderResultMap.get("time"));
        }

        return map;
    }

    /**
     * 检查异常订单
     * 返回字符串暂未在数据库中进行配置
     * 增加carLevelName入参，用于判断超出预估价的规则 modify by huzhen 2024/06/20
     *
     * @param orderBase
     * @param orderSourceOld
     * @param subCarTypeCode 运力实际的车型code
     * @return
     */
    public Boolean checkAbnormalOrder(OrderBase orderBase, OrderSource orderSourceOld, String subCarTypeCode) {
        try {
            // 需求:极速派单调度 中台状态5通知 （合规预警处理）
            OrderSource orderSource = BeanUtil.copyProperties(orderSourceOld, OrderSource.class);
            dispatchService.notifyOrderStatus5AbnormalOrder(orderBase, orderSource);

            if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ABNORMAL_PREFIX + orderBase.getCompanyId())) {
                dataUtil.getCompanyAbnormalRules(orderBase.getCompanyId());
            }

            if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ABNORMAL_PREFIX + orderBase.getCompanyId())) {
                return false;
            }

            List<CompanyAbnormalRules> abnormalRules = JSONUtil.toList(
                    JSONUtil.toJsonStr(redisUtil.get(CacheConsts.REDIS_KEY_ABNORMAL_PREFIX + orderBase.getCompanyId())),
                    CompanyAbnormalRules.class);
            if (null == abnormalRules || abnormalRules.size() == 0)
                return false;

            // log.info("orderId={},sceneId={}, abnormalRules={}", orderBase.getId(),
            // orderBase.getSceneId(), abnormalRules);

            JSONArray abnormalArray = new JSONArray();

            JSONArray complaintArray = new JSONArray();// 自动投诉 超出规则列表
            Boolean isAbnornalOrder = false;
            for (CompanyAbnormalRules abnormalParam : abnormalRules) {
                if (!abnormalParam.getSceneIds().contains(orderBase.getSceneId())
                        || null == abnormalParam.getAbnoramlRuleItems()
                        || abnormalParam.getAbnoramlRuleItems().size() == 0) {
                    continue;
                }

                List<String> abnormalList = new ArrayList<>();
                List<Map<String, Object>> riskParamList = new ArrayList<>();
                List<CompanyAbnormalItems> abnormalItemList = abnormalParam.getAbnoramlRuleItems();

                for (CompanyAbnormalItems abnormalItem : abnormalItemList) {
                    if (null == abnormalItem.getItemValue())
                        continue;

                    BigDecimal actualDistance = (null == orderSource.getTravelDistance()) ? BigDecimal.ZERO
                            : orderSource.getTravelDistance().multiply(new BigDecimal(1000)); // 此单里程，米>

                    Map<String, Object> riskParamMap = new HashMap<>();

                    switch (abnormalItem.getItemCode()) {
                        case "7001": // 行驶里程规则，T3、风韵、高德等平台，不返回实际上车和下车地点。如果，无实际上下车地址，忽略此判断
                        {
                            if (actualDistance.compareTo(BigDecimal.ZERO) == 0 &&
                                    orderSource.getActualPickupLat() != null &&
                                    orderSource.getActualPickupLng() != null &&
                                    orderSource.getActualDestLat() != null &&
                                    orderSource.getActualDestLng() != null) {
                                try {
                                    actualDistance = getDistance(orderBase.getCompanyId(),
                                            orderSource.getActualPickupLat(),
                                            orderSource.getActualDestLat(), orderSource.getActualPickupLng(),
                                            orderSource.getActualDestLng());
                                } catch (Exception ex) {
                                    log.error(
                                            "处理订单回传[检查异常订单-行驶里程规则-7001]出现异常【OrderService->checkAbnormalOrder,orderId:{}】",
                                            orderBase.getId(), ex);
                                }
                            }

                            if (actualDistance.compareTo(BigDecimal.ZERO) == 0) {
                                continue;
                            }

                            BigDecimal settingValue = new BigDecimal(abnormalItem.getItemValue());
                            if (settingValue.compareTo(BigDecimal.ZERO) == 0) {
                                continue;
                            }

                            // BigDecimal estimateDistance = getDistance(orderBase.getCompanyId(),
                            // orderBase.getDepartLat(),
                            // orderBase.getDestLat(), orderBase.getDepartLng(), orderBase.getDestLng());

                            // 20241219 预估里程使用运力返回的预估里程
                            BigDecimal estimateDistance = BigDecimal.ZERO;
                            if (ObjectUtil.isNotEmpty(orderSource.getEstimateDistance())) {
                                estimateDistance = new BigDecimal(orderSource.getEstimateDistance());
                            }
                            if (NumberUtil.equals(estimateDistance, BigDecimal.ZERO)) {
                                // 修改目的地会修改 order_base的dest相关信息,所以直接使用即可
                                estimateDistance = getDistance(orderBase.getCompanyId(), orderBase.getDepartLat(),
                                        orderBase.getDestLat(), orderBase.getDepartLng(), orderBase.getDestLng());
                            }

                            if ("2".equals(abnormalItem.getItemType())) {// 按比例
                                if (actualDistance.compareTo(estimateDistance) == -1) {
                                    continue;
                                }

                                BigDecimal limitDistance = estimateDistance
                                        .add(estimateDistance.multiply(settingValue).divide(new BigDecimal(100)));
                                if (actualDistance.compareTo(limitDistance) == 1) {
                                    abnormalList.add(StrUtil.format("实际行驶里程超过预估里程 {} %", settingValue));
                                    riskParamMap.put("riskType", 1);
                                    riskParamMap.put("riskTips", StrUtil.format("实际行驶里程超过预估里程 {} %", settingValue));
                                    riskParamList.add(riskParamMap);
                                }
                            } else if ("4".equals(abnormalItem.getItemType())) {// 按里程

                                if (actualDistance.compareTo(estimateDistance) == -1) {
                                    continue;
                                }

                                BigDecimal diffDistance = actualDistance.subtract(estimateDistance);
                                if (diffDistance.compareTo(settingValue) == 1) {
                                    // returnMap.put("7001_02", String.valueOf(settingValue));
                                    abnormalList.add(StrUtil.format("实际行驶里程超过预估里程 {} 米", settingValue));
                                    riskParamMap.put("riskType", 1);
                                    riskParamMap.put("riskTips", StrUtil.format("实际行驶里程超过预估里程 {} 米", settingValue));
                                    riskParamList.add(riskParamMap);
                                }
                            } else if ("5".equals(abnormalItem.getItemType())) {// 按差值
                                Integer diffDistance = actualDistance.subtract(estimateDistance)
                                        .multiply(new BigDecimal(1000)).intValue();
                                BigDecimal absDistance = new BigDecimal(Math.abs(diffDistance));
                                if (absDistance.compareTo(settingValue) == 1) {
                                    // returnMap.put("7001_03", String.valueOf(settingValue));
                                    abnormalList.add(StrUtil.format("实际行驶里程与预估里程的差超出 {} 米", settingValue));
                                    riskParamMap.put("riskType", 1);
                                    riskParamMap.put("riskTips", StrUtil.format("实际行驶里程与预估里程的差超出 {} 米", settingValue));
                                    riskParamList.add(riskParamMap);
                                }
                            }

                            break;
                        }
                        case "7002": // 订单附加费
                        {
                            BigDecimal actualExtralFee = getAdditionalFee(orderSource.getFeeDetail().toString());// 实际的附加费
                            if ("3".equals(abnormalItem.getItemType())) { // 按金额
                                BigDecimal settingExtralFee = new BigDecimal(abnormalItem.getItemValue());
                                if (settingExtralFee.compareTo(BigDecimal.ZERO) == 1
                                        && actualExtralFee.compareTo(settingExtralFee) == 1) {
                                    // returnMap.put("7002_01", String.valueOf(settingExtralFee));
                                    abnormalList.add(StrUtil.format("订单附加费超出 {} 元", settingExtralFee));
                                    riskParamMap.put("riskType", 3);
                                    riskParamMap.put("riskTips", StrUtil.format("订单附加费超出 {} 元", settingExtralFee));
                                    riskParamList.add(riskParamMap);
                                }
                            } else {// 按里程
                                BigDecimal actualDistanceLiCheng = null == orderSource.getTravelDistance()
                                        ? BigDecimal.ZERO
                                        : orderSource.getTravelDistance(); // 此单里程，公里

                                JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                                if ("1".equals(valueObject.getStr("type"))) { // 以上
                                    BigDecimal settingDistance = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                    BigDecimal settingExtralFee = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                                    if (actualDistanceLiCheng.compareTo(BigDecimal.ZERO) == 0) {
                                        if (orderSource.getActualPickupLat() != null
                                                && orderSource.getActualPickupLng() != null
                                                && orderSource.getActualDestLat() != null
                                                && orderSource.getActualDestLng() != null) {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderSource.getActualPickupLat(), orderSource.getActualDestLat(),
                                                    orderSource.getActualPickupLng(), orderSource.getActualDestLng());
                                        } else {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderBase.getDepartLat(), orderBase.getDestLat(),
                                                    orderBase.getDepartLng(), orderBase.getDestLng());
                                        }
                                    }

                                    if (actualDistanceLiCheng.compareTo(settingDistance) == 1
                                            && actualExtralFee.compareTo(settingExtralFee) == 1) {
                                        // returnMap.put("7002_02", String.valueOf(settingDistance) + "," +
                                        // String.valueOf(settingExtralFee));

                                        abnormalList.add(StrUtil.format("订单里程 {} 公里以上附加费超出 {} 元", settingDistance,
                                                settingExtralFee));
                                        riskParamMap.put("riskType", 3);
                                        riskParamMap.put("riskTips",
                                                StrUtil.format("订单里程 {} 公里以上附加费超出 {} 元", settingDistance,
                                                        settingExtralFee));
                                        riskParamList.add(riskParamMap);
                                    }
                                } else if ("2".equals(valueObject.getStr("type"))) { // 以下
                                    BigDecimal settingDistance = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                    BigDecimal settingExtralFee = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                                    if (actualDistanceLiCheng.compareTo(BigDecimal.ZERO) == 0) {
                                        if (orderSource.getActualPickupLat() != null
                                                && orderSource.getActualPickupLng() != null
                                                && orderSource.getActualDestLat() != null
                                                && orderSource.getActualDestLng() != null) {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderSource.getActualPickupLat(), orderSource.getActualDestLat(),
                                                    orderSource.getActualPickupLng(), orderSource.getActualDestLng());
                                        } else {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderBase.getDepartLat(), orderBase.getDestLat(),
                                                    orderBase.getDepartLng(), orderBase.getDestLng());
                                        }
                                    }

                                    if (actualDistanceLiCheng.compareTo(settingDistance) == -1
                                            && actualExtralFee.compareTo(settingExtralFee) == 1) {
                                        // returnMap.put("7002_03", String.valueOf(settingDistance) + "," +
                                        // String.valueOf(settingExtralFee));
                                        abnormalList.add(StrUtil.format("订单里程 {} 公里以下附加费超出 {} 元", settingDistance,
                                                settingExtralFee));
                                        riskParamMap.put("riskType", 3);
                                        riskParamMap.put("riskTips",
                                                StrUtil.format("订单里程 {} 公里以下附加费超出 {} 元", settingDistance,
                                                        settingExtralFee));
                                        riskParamList.add(riskParamMap);
                                    }
                                } else { // 区间
                                    BigDecimal settingMinDistance = valueObject.getBigDecimal("value1",
                                            BigDecimal.ZERO);
                                    BigDecimal settingMaxDistance = valueObject.getBigDecimal("value2",
                                            BigDecimal.ZERO);
                                    BigDecimal settingExtralFee = valueObject.getBigDecimal("value3", BigDecimal.ZERO);
                                    if (actualDistanceLiCheng.compareTo(BigDecimal.ZERO) == 0) {
                                        if (orderSource.getActualPickupLat() != null
                                                && orderSource.getActualPickupLng() != null
                                                && orderSource.getActualDestLat() != null
                                                && orderSource.getActualDestLng() != null) {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderSource.getActualPickupLat(), orderSource.getActualDestLat(),
                                                    orderSource.getActualPickupLng(), orderSource.getActualDestLng());
                                        } else {
                                            actualDistanceLiCheng = getDistance(orderBase.getCompanyId(),
                                                    orderBase.getDepartLat(), orderBase.getDestLat(),
                                                    orderBase.getDepartLng(), orderBase.getDestLng());
                                        }
                                    }

                                    if (actualDistanceLiCheng.compareTo(settingMinDistance) == 1
                                            && actualDistanceLiCheng.compareTo(settingMaxDistance) == -1
                                            && actualExtralFee.compareTo(settingExtralFee) == 1) {
                                        // returnMap.put("7002_04", String.valueOf(settingMinDistance) + ","+
                                        // String.valueOf(settingMaxDistance) + "," + String.valueOf(settingExtralFee));
                                        abnormalList.add(StrUtil.format("订单里程 {} 至 {} 公里附加费超出 {} 元", settingMinDistance,
                                                settingMaxDistance, settingExtralFee));
                                        riskParamMap.put("riskType", 3);
                                        riskParamMap.put("riskTips",
                                                StrUtil.format("订单里程 {} 至 {} 公里附加费超出 {} 元", settingMinDistance,
                                                        settingMaxDistance, settingExtralFee));
                                        riskParamList.add(riskParamMap);
                                    }
                                }
                            }
                            break;
                        }
                        case "7003": // 订单实际金额
                        {
                            // 通过 orderBase.CustomInfo 中 selectedCars 的carSourceId 对应 orderSource 中的
                            // SourceCode 获取CustomInfo的实际预估价格
                            // 获取订单预估价格
                            BigDecimal estimatePrice = orderSource.getEstimatePrice();
                            if (estimatePrice == null || estimatePrice.compareTo(BigDecimal.ZERO) == 0) {
                                try {
                                    String customInfo = orderBase.getCustomInfo();
                                    Long sourceCode = Long.valueOf(orderSource.getSourceCode());
                                    Integer carLevel = orderSource.getCarLevel();
                                    JSONArray selectedCars = JSONUtil.parseObj(customInfo).getJSONArray("selectedCars");
                                    Optional<JSONObject> carSourceId = selectedCars.stream().map(JSONUtil::parseObj)
                                            .filter(item -> NumberUtil.compare(sourceCode,
                                                    item.getLong("carSourceId")) == 0
                                                    && NumberUtil.compare(carLevel, item.getInt("carLevel")) == 0
                                                    && (StrUtil.isBlank(subCarTypeCode)
                                                            || StrUtil.equals(subCarTypeCode,
                                                                    item.getStr("subCarType"))))
                                            .findAny();
                                    if (carSourceId.isPresent()) {
                                        estimatePrice = carSourceId.get().getBigDecimal("estimatePrice", estimatePrice);
                                    }
                                } catch (Exception ex) {
                                    log.error(
                                            "处理订单回传[检查异常订单-解析预估价格-7003]出现异常【OrderService->checkAbnormalOrder,orderId:{}】",
                                            orderBase.getId(), ex);
                                }
                            }

                            // 获取实际金额
                            BigDecimal amount = orderSource.getAmount();

                            // BigDecimal actualExtralFee =
                            // getAdditionalFee(orderSource.getFeeDetail().toString());// 实际的附加费
                            // amount = NumberUtil.sub(amount, actualExtralFee);

                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                            if ("2".equals(abnormalItem.getItemType())) {// 按比例

                                if (null == amount || amount.compareTo(BigDecimal.ZERO) == 0) {
                                    log.info("订单金额验证规则：orderSource表中amount字段为0。");
                                    continue;
                                }

                                if (amount.compareTo(estimatePrice) <= 0)
                                    continue;

                                BigDecimal value1 = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                if (value1.compareTo(BigDecimal.ZERO) == 1) {
                                    BigDecimal limitAmount = estimatePrice.add(
                                            estimatePrice.multiply(value1).divide(new BigDecimal(100)));
                                    if (amount.compareTo(limitAmount) == 1) {
                                        abnormalList.add(StrUtil.format("订单实际金额超出预估价格 {} %", value1));
                                        riskParamMap.put("riskType", 2);
                                        riskParamMap.put("riskTips", StrUtil.format("订单实际金额超出预估价格 {} %", value1));
                                        riskParamList.add(riskParamMap);
                                    }
                                }
                            } else if ("4".equals(abnormalItem.getItemType())) { // 按金额
                                if (valueObject.containsKey("value1")) {
                                    BigDecimal value1 = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                    if (value1.compareTo(BigDecimal.ZERO) == 1) {
                                        // 订单实际结算金额
                                        BigDecimal actualAmount = orderSource.getCompanyBearAmount()
                                                .add(orderSource.getUserBearAmount());
                                        if (actualAmount.compareTo(value1) == 1) {
                                            abnormalList.add(StrUtil.format("订单实际金额超出 {} 元", value1));
                                            riskParamMap.put("riskType", 2);
                                            riskParamMap.put("riskTips", StrUtil.format("订单实际金额超出 {} 元", value1));
                                            riskParamList.add(riskParamMap);
                                        }
                                    }
                                }

                                if (valueObject.containsKey("value2")) {
                                    BigDecimal value2 = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                                    if (value2.compareTo(BigDecimal.ZERO) == 1) {
                                        // 订单单次额度限制
                                        JSONObject customObject = new JSONObject(orderBase.getCustomInfo());
                                        if (customObject != null && customObject.containsKey("onceAmount")) {

                                            BigDecimal actualAmount = NumberUtil.add(orderSource.getCompanyBearAmount(),
                                                    orderSource.getUserBearAmount());
                                            BigDecimal onceAmount = customObject.getBigDecimal("onceAmount");
                                            BigDecimal tempAmount = NumberUtil.add(onceAmount, value2);

                                            if (actualAmount.compareTo(tempAmount) == 1) {
                                                abnormalList.add(StrUtil.format("订单实际金额超出单次额度 {} 元", value2));
                                                riskParamMap.put("riskType", 2);
                                                riskParamMap.put("riskTips",
                                                        StrUtil.format("订单实际金额超出单次额度 {} 元", value2));
                                                riskParamList.add(riskParamMap);
                                            }
                                        }
                                    }
                                }

                                if (valueObject.containsKey("value3")) {
                                    if (null == amount || amount.compareTo(BigDecimal.ZERO) == 0) {
                                        log.info("订单金额验证规则：orderSource表中amount字段为0。");
                                        continue;
                                    }
                                    if (amount.compareTo(estimatePrice) <= 0)
                                        continue;
                                    BigDecimal value3 = valueObject.getBigDecimal("value3", BigDecimal.ZERO);
                                    if (value3.compareTo(BigDecimal.ZERO) == 1) {
                                        BigDecimal exceedingAmount = amount.subtract(estimatePrice);
                                        if (exceedingAmount.compareTo(value3) == 1) {
                                            abnormalList.add(StrUtil.format("订单实际金额超出预估价格 {} 元", value3));
                                            riskParamMap.put("riskType", 2);
                                            riskParamMap.put("riskTips", StrUtil.format("订单实际金额超出预估价格 {} 元", value3));
                                            riskParamList.add(riskParamMap);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case "7004": // 两个订单发起时间间隔
                        {
                            BigDecimal settlingMinute = new BigDecimal(abnormalItem.getItemValue());
                            OrderBase preOrderBase = orderBaseMapper.selectPreOrderCreateTime(orderBase);
                            if (preOrderBase != null) {
                                Long offsetMinute = DateUtil.between(preOrderBase.getCreateTime(),
                                        orderBase.getCreateTime(), DateUnit.MINUTE, true);
                                if (offsetMinute.compareTo(settlingMinute.longValue()) == -1) {
                                    // returnMap.put("7004", String.valueOf(settingOffsetMinute));
                                    abnormalList.add(StrUtil.format("两个订单发起时间间隔小于 {} 分钟", settlingMinute));
                                }
                            }
                            break;
                        }
                        case "7005": // 实际上下车地址
                        {
                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                            if (valueObject.containsKey("value1")) {
                                BigDecimal settingPickupDistance = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                if (settingPickupDistance.compareTo(BigDecimal.ZERO) == 1
                                        && orderSource.getActualPickupLat() != null
                                        && orderSource.getActualPickupLng() != null) {
                                    Integer distance = com.ipath.orderflowservice.order.util.CoordinateUtil.getDistance(
                                            Double.valueOf(orderBase.getDepartLat()),
                                            Double.valueOf(orderBase.getDepartLng()),
                                            Double.valueOf(orderSource.getActualPickupLat()),
                                            Double.valueOf(orderSource.getActualPickupLng()));
                                    if (distance.compareTo(settingPickupDistance.intValue()) == 1) {
                                        // returnMap.put("7005_01", String.valueOf(settingDistance));
                                        abnormalList.add(
                                                StrUtil.format("实际上车地点超出下单时填写的起始地 {} 米的直线距离范围", settingPickupDistance));
                                    }
                                }
                            }

                            if (valueObject.containsKey("value2")) {
                                BigDecimal settingDestDistance = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                                if (settingDestDistance.compareTo(BigDecimal.ZERO) == 1
                                        && orderSource.getActualDestLat() != null
                                        && orderSource.getActualDestLng() != null) {
                                    Integer distance = com.ipath.orderflowservice.order.util.CoordinateUtil.getDistance(
                                            Double.valueOf(orderBase.getDestLat()),
                                            Double.valueOf(orderBase.getDestLng()),
                                            Double.valueOf(orderSource.getActualDestLat()),
                                            Double.valueOf(orderSource.getActualDestLng()));
                                    if (distance.compareTo(settingDestDistance.intValue()) == 1) {
                                        // returnMap.put("7005_02", String.valueOf(actualDestDistance));
                                        abnormalList
                                                .add(StrUtil.format("实际下车地点超出下单时填写的目的地 {} 米的直线距离范围",
                                                        settingDestDistance));
                                    }
                                }
                            }
                            break;
                        }
                        case "7006": // 地点关键词
                        {
                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                            if (valueObject.containsKey("value1")) {
                                String startLocationKeyword = valueObject.getStr("value1", null);
                                String pickupLocationName = cipherService.addressDecrypt(orderBase.getCompanyId(),
                                        orderBase.getPickupLocationName());
                                if (!StringUtils.isEmpty(startLocationKeyword)
                                        && pickupLocationName.contains(startLocationKeyword)) {
                                    abnormalList.add(StrUtil.format("下单时填写起始地存在关键词 \"{}\"", startLocationKeyword));
                                }
                            }

                            if (valueObject.containsKey("value2")) {
                                String endLocationKeyword = valueObject.getStr("value2", null);
                                String destLocationName = cipherService.addressDecrypt(orderBase.getCompanyId(),
                                        orderBase.getDestLocationName());
                                if (!StringUtils.isEmpty(endLocationKeyword)
                                        && destLocationName.contains(endLocationKeyword)) {
                                    // returnMap.put("7006_02", endLocationKeyword);
                                    abnormalList.add(StrUtil.format("下单时填写目的地存在关键词 \"{}\"", endLocationKeyword));
                                }
                            }

                            if (valueObject.containsKey("value3")) {
                                String startLocationKeyword = valueObject.getStr("value3", null);
                                String pickupLocationName = cipherService.addressDecrypt(orderBase.getCompanyId(),
                                        orderBase.getPickupLocationName());
                                if (!StringUtils.isEmpty(startLocationKeyword)
                                        && !pickupLocationName.contains(startLocationKeyword)) {
                                    abnormalList.add(StrUtil.format("下单时填写起始地不包含关键词 \"{}\"", startLocationKeyword));
                                }
                            }

                            if (valueObject.containsKey("value4")) {
                                String endLocationKeyword = valueObject.getStr("value4", null);
                                String destLocationName = cipherService.addressDecrypt(orderBase.getCompanyId(),
                                        orderBase.getDestLocationName());
                                if (!StringUtils.isEmpty(endLocationKeyword)
                                        && !destLocationName.contains(endLocationKeyword)) {
                                    abnormalList.add(StrUtil.format("下单时填写目的地不包含关键词 \"{}\"", endLocationKeyword));
                                }
                            }
                            break;
                        }
                        case "7007": // 时间段
                        {
                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                            if (valueObject.containsKey("mode")) {
                                String mode = valueObject.getStr("mode", null);
                                if (StringUtils.equals(mode, "weekday")) {
                                    if (valueObject.containsKey("rules")) {

                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(orderBase.getDepartTime());
                                        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                                        if (weekDay == 1) {
                                            weekDay = 7;
                                        } else {
                                            weekDay = weekDay - 1;
                                        }

                                        JSONArray rules = valueObject.getJSONArray("rules");
                                        for (int i = 0; i < rules.size(); i++) {
                                            JSONObject job = rules.getJSONObject(i);
                                            int weekDayKua = 0;
                                            if (weekDay == 1) {
                                                weekDayKua = 7;
                                            } else {
                                                weekDayKua = weekDay - 1;
                                            }
                                            if (job.getInt("day") == weekDayKua) {
                                                String b = job.getStr("b");
                                                String e = job.getStr("e");
                                                String time = DateFormatUtils.format(orderBase.getDepartTime(),
                                                        "HH:mm:ss");
                                                if (this.compTime(b, e)) {
                                                    if (this.compTime(e, time)) {
                                                        abnormalList.add("订单用车时间在限制时间段范围内");
                                                    }
                                                }
                                            }
                                            if (job.getInt("day") == weekDay) {
                                                String b = job.getStr("b");
                                                String e = job.getStr("e");
                                                String time = DateFormatUtils.format(orderBase.getDepartTime(),
                                                        "HH:mm:ss");

                                                if (this.compTime(time, b) && this.compTime(e, time)) {
                                                    abnormalList.add("订单用车时间在限制时间段范围内");
                                                }
                                            }
                                        }
                                    }
                                }

                                if (StringUtils.equals(mode, "holiday")) {
                                    if (valueObject.containsKey("rules")) {

                                        boolean holiday = companyService.isHoliday(orderBase.getCompanyId(),
                                                orderBase.getDepartTime());
                                        Date yesterday = DateUtils.addDays(orderBase.getDepartTime(), -1);
                                        boolean yesterdayHoliday = companyService.isHoliday(orderBase.getCompanyId(),
                                                yesterday);

                                        JSONArray rules = valueObject.getJSONArray("rules");
                                        for (int i = 0; i < rules.size(); i++) {
                                            JSONObject job = rules.getJSONObject(i);
                                            Object day = job.get("day");

                                            boolean hua = false;
                                            if (null != day && !yesterdayHoliday && job.getInt("day") == 1) {
                                                String b = job.getStr("b") + ":00";
                                                String e = job.getStr("e") + ":00";
                                                String time = DateFormatUtils.format(yesterday, "HH:mm:ss");
                                                if (this.compTime(b, e)) {
                                                    if (this.compTime(e, time)) {
                                                        abnormalList.add("订单用车时间在限制时间段范围内");
                                                    }
                                                }
                                            }

                                            if (null != day && yesterdayHoliday && job.getInt("day") == 0) {
                                                String b = job.getStr("b") + ":00";
                                                String e = job.getStr("e") + ":00";
                                                String time = DateFormatUtils.format(yesterday, "HH:mm:ss");
                                                if (this.compTime(b, e)) {
                                                    if (this.compTime(e, time)) {
                                                        abnormalList.add("订单用车时间在限制时间段范围内");
                                                    }
                                                }
                                            }

                                            if (null != day && !holiday && job.getInt("day") == 1) {
                                                String b = job.getStr("b") + ":00";
                                                String e = job.getStr("e") + ":00";
                                                String time = DateFormatUtils.format(orderBase.getDepartTime(),
                                                        "HH:mm:ss");
                                                if (this.compTime(time, b) && this.compTime(e, time)) {
                                                    abnormalList.add("订单用车时间在限制时间段范围内");
                                                }
                                            }

                                            if (null != day && holiday && job.getInt("day") == 0) {
                                                String b = job.getStr("b") + ":00";
                                                String e = job.getStr("e") + ":00";
                                                String time = DateFormatUtils.format(orderBase.getDepartTime(),
                                                        "HH:mm:ss");
                                                if (this.compTime(time, b) && this.compTime(e, time)) {
                                                    abnormalList.add("订单用车时间在限制时间段范围内");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            break;
                        }
                        case "7008": // 同城/异地
                        {
                            if (null == orderSource.getAmount()) {
                                break;
                            }

                            BigDecimal amount = orderSource.getAmount();
                            BigDecimal actualExtralFee = getAdditionalFee(orderSource.getFeeDetail().toString());// 实际的附加费
                            amount = NumberUtil.sub(amount, actualExtralFee);

                            if (null == orderSource.getTravelDistance()
                                    || orderSource.getTravelDistance() == BigDecimal.ZERO || null == amount
                                    || amount == BigDecimal.ZERO)
                                break;

                            boolean isCrossCity = !orderBase.getDepartCityCode().equals(orderBase.getDestCityCode());
                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());

                            BigDecimal settingAmount = BigDecimal.ZERO;
                            BigDecimal settingPrice = BigDecimal.ZERO;

                            if (isCrossCity) {
                                settingAmount = valueObject.getBigDecimal("value3",
                                        BigDecimal.ZERO);
                                settingPrice = valueObject.getBigDecimal("value4", BigDecimal.ZERO);
                            } else {
                                settingAmount = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                                settingPrice = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                            }

                            if (settingAmount.compareTo(BigDecimal.ZERO) <= 0
                                    || settingPrice.compareTo(BigDecimal.ZERO) <= 0)
                                break;

                            if (amount.compareTo(settingAmount) > 0) {
                                BigDecimal actualPrice = amount.divide(orderSource.getTravelDistance(), 2,
                                        RoundingMode.HALF_UP);
                                if (actualPrice.compareTo(settingPrice) > 0) {
                                    abnormalList.add(StrUtil.format("单笔{}订单实际金额超出 {}元且每公里单价大于 {} 元",
                                            isCrossCity ? "异地" : "同城", settingAmount,
                                            settingPrice));
                                    riskParamMap.put("riskType", 2);
                                    riskParamMap.put("riskTips", StrUtil.format("单笔{}订单实际金额超出 {}元且每公里单价大于 {} 元",
                                            isCrossCity ? "异地" : "同城", settingAmount,
                                            settingPrice));
                                    riskParamList.add(riskParamMap);
                                }
                            }
                            break;
                        }
                        case "7009": // 司乘成单异常规则
                        {
                            // 获取规则参数
                            JSONObject valueObject = new JSONObject(abnormalItem.getItemValue());
                            // 获取指定天数
                            BigDecimal days = valueObject.getBigDecimal("value1", BigDecimal.ZERO);
                            // 获取规定值
                            BigDecimal values = valueObject.getBigDecimal("value2", BigDecimal.ZERO);
                            log.info("司乘规则: {}", values);

                            // 获取 同一下单人下单同一车牌号在指定天数内的成单数量
                            int countTemp = orderBaseMapper.getSameDriverAndPassengerCount(orderBase.getCompanyId(),
                                    orderBase.getUserId(), orderSource.getVehicleNo(), days.intValue());
                            // +1 是把当前单作为已完单订单加上
                            BigDecimal count = BigDecimal.valueOf(countTemp + 1);
                            log.info("同一下单人下单同一车牌号在指定天数内的成单参数: companyId: {} ,userPhone: {} ,VehicleNo: {},days: {}",
                                    orderBase.getCompanyId(), orderBase.getUserPhone(), orderSource.getVehicleNo(),
                                    days.intValue());
                            log.info("同一下单人下单同一车牌号在指定天数内的成单数量: {} , {}", countTemp, count);

                            if ("1".equals(abnormalItem.getItemType())) {
                                // 按比例: 一个月内，同一乘客和同一司机成单率大于__％时，将判定为异常订单 总数量

                                // 下单人手机号的所有完单订单数量
                                int totalCountTemp = orderBaseMapper.getPassengerCount(orderBase.getCompanyId(),
                                        orderBase.getUserPhone(), days.intValue());
                                // 总单数加1, 当前单也加入到总数中
                                BigDecimal totalCount = BigDecimal.valueOf(totalCountTemp + 1);

                                log.info("下单人手机号的所有完单订单数量参数: companyId: {} ,userPhone: {} ,days: {}",
                                        orderBase.getCompanyId(), orderBase.getUserPhone(), days.intValue());
                                log.info("下单人手机号的所有完单订单数量数量: {} , {}", totalCountTemp, totalCount);

                                // 总数要>0
                                if (totalCount.compareTo(BigDecimal.ONE) > 0) {
                                    // 成单率的计算为：一个月内同一下单人下单同一车牌号的完单订单/一个月内该下单人手机号的所有完单订单 * 100%
                                    BigDecimal tempValues = NumberUtil.mul(NumberUtil.div(count, totalCount),
                                            BigDecimal.valueOf(100));
                                    // 成单率大于指定比例,为异常
                                    if (tempValues.compareTo(values) > 0) {
                                        abnormalList.add(StrUtil.format("{}天内,同一乘客和同一司机成单率大于{}%",
                                                days, values));
                                    }

                                }
                            } else if ("2".equals(abnormalItem.getItemType())) {
                                // 按数量: 一个月内，同一乘客和同一司机成单率大于__单时，将判定为异常订单

                                // 同一下单人下单同一车牌号在指定天数内的成单数量 大于指定数量,为异常
                                if (count.compareTo(values) > 0) {
                                    abnormalList.add(StrUtil.format("{}天内,同一乘客和同一司机成单数量大于{}单",
                                            days, values));
                                }
                            }
                            break;
                        }
                        case "7010": // 目的地偏移异常规则
                        {
                            // 获取规则限制的距离(公里)
                            BigDecimal distanceRule = new BigDecimal(abnormalItem.getItemValue());

                            // 获取实际下车经纬度
                            String actualDestLatStr = orderSource.getActualDestLat(); // 纬度
                            String actualDestLngStr = orderSource.getActualDestLng(); // 经度

                            // 获取订单目的地
                            String destLat = orderBase.getDestLat();
                            String destLng = orderBase.getDestLng();

                            if (NumberUtil.isGreater(distanceRule, BigDecimal.ZERO) // 规则距离 > 0
                                    && StrUtil.isNotEmpty(actualDestLatStr)
                                    && StrUtil.isNotEmpty(actualDestLngStr) // 获取实际下车经纬度 不为空
                                    && StrUtil.isNotEmpty(destLat)
                                    && StrUtil.isNotEmpty(destLng) // 订单目的地经纬度 不为空
                            ) {
                                // 获取两地 实际差距公里数
                                Integer distance = com.ipath.orderflowservice.order.util.CoordinateUtil
                                        .getDistanceMeter(
                                                Double.parseDouble(destLat),
                                                Double.parseDouble(destLng),
                                                Double.parseDouble(actualDestLatStr),
                                                Double.parseDouble(actualDestLngStr));
                                if (NumberUtil.isGreater(BigDecimal.valueOf(distance),
                                        NumberUtil.mul(distanceRule, 1000))) { // 实际距离(m) > 规定距离(m) 则为异常
                                    abnormalList.add(StrUtil.format("目的地偏移{}公里以上", distanceRule));
                                    riskParamMap.put("riskType", 1);
                                    riskParamMap.put("riskTips", StrUtil.format("目的地偏移{}公里以上", distanceRule));
                                    riskParamList.add(riskParamMap);
                                }
                            }
                            break;
                        }
                    }
                }

                if (abnormalList.size() > 0) {
                    JSONObject abnormalObject = new JSONObject();
                    abnormalObject.set("abnormalRuleName", abnormalParam.getNameCn());
                    abnormalObject.set("notifyUser", abnormalParam.getNotifyUser());
                    abnormalObject.set("remindApproverInfo", abnormalParam.getRemindApproverInfo());
                    abnormalObject.set("abnormalReason", abnormalList);
                    abnormalObject.set("riskParam", riskParamList);
                    abnormalArray.add(abnormalObject);

                    isAbnornalOrder = true;
                }
            }

            JSONObject customeInfo = new JSONObject(orderBase.getCustomInfo());
            if (customeInfo != null) {
                customeInfo.set("abnormal", abnormalArray);
                orderBase.setCustomInfo(customeInfo.toString());
            }

            // 订单投诉
            List<String> repeatReason = new ArrayList<>();
            int id = 0;
            JSONObject jsonObject = new JSONObject(orderBase.getCustomInfo());
            if (jsonObject.containsKey("abnormal") && jsonObject.get("abnormal") != null) {
                JSONArray existingAbnormalArray = new JSONArray(jsonObject.get("abnormal"));

                if (existingAbnormalArray != null && existingAbnormalArray.size() > 0) {
                    for (int index = 0; index < existingAbnormalArray.size(); index++) {
                        JSONObject existingAbnormalObject = existingAbnormalArray.getJSONObject(index);
                        if (existingAbnormalObject.containsKey("abnormalReason")
                                && existingAbnormalObject.get("abnormalReason") != null) {
                            JSONArray rulesArray = existingAbnormalObject.getJSONArray("abnormalReason");
                            List<String> stringList = JSONUtil.toList(rulesArray, String.class);
                            if (stringList.size() > 0) {
                                for (int i = 0; i < stringList.size(); i++) {
                                    if (existingAbnormalObject.containsKey("remindApproverInfo")
                                            && existingAbnormalObject.get("remindApproverInfo") != null) {
                                        JSONObject jsonObject1 = new JSONObject(
                                                existingAbnormalObject.get("remindApproverInfo"));
                                        if (jsonObject1.containsKey("autoComplaint")
                                                && jsonObject1.getBool("autoComplaint")) {
                                            if (!repeatReason.contains(stringList.get(i))) {
                                                id++;
                                                repeatReason.add(stringList.get(i));
                                                JSONObject jsonObject2 = new JSONObject();
                                                jsonObject2.set("label", stringList.get(i));
                                                jsonObject2.set("isCheck", true);
                                                jsonObject2.set("id", String.valueOf(id));
                                                complaintArray.add(jsonObject2);
                                            }
                                        }
                                    }

                                    if (existingAbnormalObject.containsKey("notifyUser")
                                            && existingAbnormalObject.getShort("notifyUser") == 2) {
                                        if (!redisUtil.hashHasKey(
                                                CacheConsts.REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX
                                                        + String.valueOf(orderBase.getCompanyId()),
                                                String.valueOf(orderBase.getUserId()))) {
                                            redisUtil.hashPut(
                                                    CacheConsts.REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX
                                                            + String.valueOf(orderBase.getCompanyId()),
                                                    String.valueOf(orderBase.getUserId()), orderBase.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (complaintArray.size() > 0) {
                StringBuffer reason = new StringBuffer();
                for (int index = 0; index < complaintArray.size(); index++) {
                    JSONObject complaintObject = complaintArray.getJSONObject(index);
                    reason.append(complaintObject.get("label"));
                    if (index != complaintArray.size() - 1) {
                        reason.append("\n");
                    }
                }

                FeedbackParam param = new FeedbackParam();
                param.setOrderId(orderBase.getId());
                param.setUserId(orderBase.getUserId());
                param.setScore((short) 1);
                param.setComplaints(complaintArray);
                param.setFeedback(reason.toString());
                param.setFeedbackTypeId((short) 7);
                setOrderComplaint(param);

                JSONObject originCustomeInfo = new JSONObject(orderBase.getCustomInfo());
                if (null != originCustomeInfo) {
                    originCustomeInfo.set("complaintType", ComplaintTypeConst.ABNORMAL);// 投诉类型 自动投诉1（001） 手动投诉2（010）
                    // 两者都有1&2（011）
                    orderBase.setCustomInfo(originCustomeInfo.toString());
                }
            }

            return isAbnornalOrder;
        } catch (Exception ex) {
            Log iLog = logService.getLog(orderBase.getCompanyId(), orderBase.getUserId(), orderBase.getId(),
                    InterfaceEnum.ORDER_ERROR_COMMON_MESSAGE);
            iLog.setBody(StrUtil.format("orderBase:[{}],orderSource:[{}],subCarTypeCode:[{}]",
                    JSONUtil.toJsonStr(orderBase), JSONUtil.toJsonStr(orderSourceOld), subCarTypeCode));
            logService.saveErrorLogAsync(iLog, ex);

            log.error("处理订单回传[检查异常订单]出现异常【OrderService->checkAbnormalOrder,orderId:{}】", orderBase.getId(), ex);
        }
        return false;
    }

    /**
     * 比较两个时间 时分秒 大小
     *
     * @param s1
     * @param s2
     * @return
     */
    public boolean compTime(String s1, String s2) {
        try {
            if (s1.indexOf(":") < 0 || s2.indexOf(":") < 0) {
                System.out.println("格式不正确");
            } else {
                String[] array1 = s1.split(":");
                int total1 = Integer.valueOf(array1[0]) * 3600 + Integer.valueOf(array1[1]) * 60
                        + Integer.valueOf(array1[2]);
                String[] array2 = s2.split(":");
                int total2 = Integer.valueOf(array2[0]) * 3600 + Integer.valueOf(array2[1]) * 60
                        + Integer.valueOf(array2[2]);
                return total1 - total2 > 0 ? true : false;
            }
        } catch (NumberFormatException e) {
            log.error("compTime ===> 时间比较异常s1{},s2{},", s1, s2);
            return false;
        }
        return false;
    }

    public boolean compTimeWithEqual(String s1, String s2) {
        try {
            if (s1.indexOf(":") < 0 || s2.indexOf(":") < 0) {
                System.out.println("格式不正确");
            } else {
                String[] array1 = s1.split(":");
                int total1 = Integer.valueOf(array1[0]) * 3600 + Integer.valueOf(array1[1]) * 60
                        + Integer.valueOf(array1[2]);
                String[] array2 = s2.split(":");
                int total2 = Integer.valueOf(array2[0]) * 3600 + Integer.valueOf(array2[1]) * 60
                        + Integer.valueOf(array2[2]);
                return total1 - total2 >= 0 ? true : false;
            }
        } catch (NumberFormatException e) {
            log.error("compTime ===> 时间比较异常s1: {},s2: {},", s1, s2);
            return false;
        }
        return false;
    }

    /**
     * 获取两点间的路线的距离
     *
     * @param companyId
     * @param startLat
     * @param destLat
     * @param startLng
     * @param destLng
     * @return
     */
    private BigDecimal getDistance(Long companyId, String startLat, String destLat, String startLng, String destLng) {
        JSONObject resObj = getOriginalDistance(companyId, startLat, destLat, startLng, destLng);
        if (null == resObj)
            return BigDecimal.ZERO;

        BigDecimal distance = resObj.getBigDecimal("distance");
        return distance;
    }

    private JSONObject getOriginalDistance(Long companyId, String startLat, String destLat, String startLng,
            String destLng) {
        JSONObject object = new JSONObject();
        object.set("companyId", companyId.toString());
        object.set("origins", startLng + "," + startLat);
        object.set("destination", destLng + "," + destLat);
        object.set("strategy", 0);

        RemoteCallDto remoteCall = new RemoteCallDto();
        remoteCall.setPath("/api/v1/map/getdistance");
        remoteCall.setContent(object.toString());
        BaseResponse response = remoteCallFeign.call(remoteCall);
        if (response.getCode() != 0) {
            return null;
        }

        JSONObject resObj = new JSONObject(response.getData());
        return resObj;
    }

    /**
     * 获取附加费
     *
     * @return
     */
    private BigDecimal getAdditionalFee(String feeInfo) {
        // {"fees":[{"amount":0.17,"nameCn":"时长费","type":"time_fee"},{"amount":6.2,"nameCn":"起步费","type":"start_fee"}]}
        if (StringUtil.isNullOrEmpty(feeInfo)) {
            return BigDecimal.ZERO;
        }

        JSONObject feeObject = new JSONObject(feeInfo);
        if (!feeObject.containsKey("fees")) {
            return BigDecimal.ZERO;
        }

        BigDecimal extralFee = BigDecimal.ZERO;
        JSONArray feeArray = feeObject.getJSONArray("fees");
        if (ObjectUtil.isNotEmpty(feeArray)) {
            Object obj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_EXTRAL_FEE_CODES,
                    null, null, null);

            List<String> tempCodes = new ArrayList<>(EXTRAL_FEE_CODES);
            if (ObjectUtil.isNotEmpty(obj)) {
                tempCodes = JSONUtil.toList(StrUtil.toString(obj), String.class);
            } else {
                log.info("未获取到缓存中的附加费code,使用系统默认值");
            }

            for (int index = 0; index < feeArray.size(); index++) {
                JSONObject eachFeeObject = new JSONObject(feeArray.get(index));
                if (eachFeeObject != null) {
                    if (tempCodes.contains(eachFeeObject.getStr("type"))) {
                        extralFee = extralFee.add(eachFeeObject.getBigDecimal("amount", BigDecimal.ZERO));
                    }
                }
            }
        }

        return extralFee;
    }

    /**
     * 获取增值服务费
     *
     * @param companyId
     * @param selectedExtraServices
     * @return
     */
    private BigDecimal getExtralFee(Long companyId, List<ExtraService> selectedExtraServices) {
        if (selectedExtraServices == null || selectedExtraServices.size() == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal extralFee = BigDecimal.ZERO;

        Boolean reloadCache = false;
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX + companyId)) {
            JSONArray extraArray = new JSONArray(
                    redisUtil.get(CacheConsts.REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX + companyId));
            if (extraArray == null || extraArray.size() == 0) {
                reloadCache = true;
            } else {
                for (ExtraService eachSelectedExtraService : selectedExtraServices) {
                    reloadCache = true;
                    for (int index = 0; index < extraArray.size(); index++) {
                        JSONObject eachExtraObject = new JSONObject(extraArray.get(index));
                        if (eachExtraObject.getLong("id").equals(eachSelectedExtraService.getId())) {
                            extralFee = extralFee.add(eachExtraObject.getBigDecimal("price"));
                            reloadCache = false;
                            break;
                        }
                    }
                    if (reloadCache == true) {
                        break;
                    }
                }
            }
        } else {
            reloadCache = true;
        }

        if (reloadCache == true) {
            RequestCompanyDto requestCompanyDto = new RequestCompanyDto();
            requestCompanyDto.setCompanyId(companyId);
            BaseResponse baseResponse = couponConsumeFeign.getCompanyExtraList(requestCompanyDto);
            if (baseResponse.getCode() == 0) {
                if (redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX + companyId)) {
                    redisUtil.delete(CacheConsts.REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX + companyId);
                }
                redisUtil.set(CacheConsts.REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX + companyId, baseResponse.getData(),
                        CacheConsts.STABLE_CACHE_EXPIRE_TIME);

                JSONArray extraArray = new JSONArray(baseResponse.getData());
                for (ExtraService eachSelectedExtraService : selectedExtraServices) {
                    reloadCache = true;
                    for (int index = 0; index < extraArray.size(); index++) {
                        JSONObject eachExtraObject = new JSONObject(extraArray.get(index));
                        if (eachExtraObject.getLong("id").equals(eachSelectedExtraService.getId())) {
                            extralFee = extralFee.add(eachExtraObject.getBigDecimal("price"));
                            reloadCache = false;
                            break;
                        }
                    }
                    if (reloadCache == true) {
                        break;
                    }
                }
            }
        }

        return extralFee;
    }

    /**
     * 接收预约管家回调通知
     */
    @Async
    public void receiveBookingResult(JSONObject dataObject) throws Exception {
        String traceId = IdUtil.simpleUUID();
        Log activityLog = logService.getLog(null, null,
                null == dataObject ? 12345L : dataObject.getLong("orderIdByOrderService"),
                InterfaceEnum.BOOKING_CALLBACK, traceId);
        activityLog.setBody(JSONUtil.toJsonStr(dataObject));
        if (dataObject == null || false == dataObject.getBool("isBookingOrder")) {
            logService.saveErrorLogAsync(activityLog,
                    new Exception("isBookingOrder=" + dataObject.getBool("isBookingOrder")));
            return;
        }
        Long orderId = dataObject.getLong("orderIdByOrderService");
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        OrderSource orderSource = orderSourceMapper.selectLatestByOrderId(orderId);
        if (null == orderBase || null == orderSource) {
            logService.saveErrorLogAsync(activityLog, "无订单信息");
            throw new BusinessException("无订单信息");
        }

        activityLog.setCompanyId(orderBase.getCompanyId());
        activityLog.setUserId(orderBase.getUserId());

        logService.saveLogAsync(activityLog, "");

        int orderStatus = dataObject.getInt("orderState");// 7预约失败 2司机接单/替换司机 1预约中

        String oldCoreOrderId = dataObject.getStr("coreOrderId");
        String newCoreOrderId = dataObject.getStr("newCoreOrderId");
        String proxyPhone = dataObject.getStr("proxyPhone");
        int carLevel = dataObject.getInt("carLevel");
        Integer carSource = dataObject.getInt("carSource");

        orderBase.setState((short) orderStatus);
        orderBase.setUserPhone(proxyPhone);
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);

        switch (orderStatus) {
            case 7: {
                cancelOrder(orderSource.getUserId(), orderSource.getOrderId(), 2, "预约失败");
                break;
            }
            case 2: {
                if (!dataObject.containsKey("driverInfo")) {
                    logService.saveErrorLogAsync(activityLog, "司机信息为空");
                    throw new BusinessException("司机信息为空");
                }

                String carLevelName = CarLevel.getName(carLevel);
                String carSourceName = cacheService.getSourceName(carSource, 1);

                JSONObject driverJSONObject = dataObject.getJSONObject("driverInfo");

                orderSource.setState((short) 2);
                if (StrUtil.isNotEmpty(newCoreOrderId)) {
                    orderSource.setCoreOrderId(newCoreOrderId);
                }

                orderSource.setDriverName(driverJSONObject.containsKey("name") ? driverJSONObject.getStr("name") : "");
                orderSource
                        .setDriverPhone(driverJSONObject.containsKey("phone") ? driverJSONObject.getStr("phone") : "");
                orderSource.setDriverPhoneVirtual(
                        driverJSONObject.containsKey("virtualPhone") ? driverJSONObject.getStr("virtualPhone") : "");
                orderSource
                        .setDriverLevel(driverJSONObject.containsKey("level") ? driverJSONObject.getStr("level") : "");
                orderSource.setDriverAvatar(
                        driverJSONObject.containsKey("avatar") ? driverJSONObject.getStr("avatar") : "");
                orderSource.setVehicleModel(
                        driverJSONObject.containsKey("vehicleModel") ? driverJSONObject.getStr("vehicleModel") : "");
                orderSource.setVehicleColor(
                        driverJSONObject.containsKey("vehicleColor") ? driverJSONObject.getStr("vehicleColor") : "");
                orderSource.setVehicleNo(
                        driverJSONObject.containsKey("vehicleNo") ? driverJSONObject.getStr("vehicleNo") : "");
                orderSource.setCarLevel(carLevel);
                orderSource.setSourceCode(carSource.toString());
                orderSource.setSourceNameCn(carSourceName);
                orderSource.setUpdateTime(new Date());
                orderSourceMapper.updateByPrimaryKeySelective(orderSource);

                String newCarSources = carLevelName + "|" + carSourceName;
                if (false == newCarSources.equals(orderBase.getCarSources())) {
                    orderBase.setCarSources(newCarSources);
                }

                orderBase.setState((short) 2);

                orderBaseMapper.updateByPrimaryKeySelective(orderBase);

                moidfyRunningOrderCache(oldCoreOrderId, newCoreOrderId, (short) 2, orderSource);

                // 通知报表
                notifyReport(dataObject, orderBase, orderSource, (short) 2, activityLog);
                break;
            }
            case 1: {
                if (StringUtils.isNotEmpty(newCoreOrderId)) {
                    orderSource.setCoreOrderId(newCoreOrderId);
                }

                orderSource.setState((short) 1);
                orderSource.setVehicleNo(null);
                orderSource.setVehicleColor(null);
                orderSource.setVehicleModel(null);
                orderSource.setDriverAvatar(null);
                orderSource.setDriverLevel(null);
                orderSource.setDriverPhone(null);
                orderSource.setUpdateTime(new Date());
                orderSourceMapper.updateByPrimaryKeySelective(orderSource);

                // 优惠券
                String customerInfo = orderBase.getCustomInfo();
                if (customerInfo != null) {
                    JSONObject customJSONObject = new JSONObject(customerInfo);
                    if (null != customJSONObject && customJSONObject.containsKey("couponIds")) {
                        List<Long> couponIdList = customJSONObject.getBeanList("couponIds", Long.class);
                        if (couponIdList != null && couponIdList.size() > 0) {
                            couponService.changeCouponState(couponIdList, 1, orderBase.getCompanyId(),
                                    orderBase.getUserId(), orderBase.getId());
                        }
                    }
                }

                orderBase.setState((short) 1);
                orderBaseMapper.updateByPrimaryKeySelective(orderBase);

                moidfyRunningOrderCache(oldCoreOrderId, newCoreOrderId, (short) 1, orderSource);

                // 通知报表
                notifyReport(dataObject, orderBase, orderSource, (short) 1, activityLog);
                break;
            }
        }
    }

    /**
     * 通知报表
     *
     * @param dataObject
     * @param orderBase
     * @param orderSource
     * @param status
     * @param activityLog
     * @throws Exception
     */
    private void notifyReport(JSONObject dataObject, OrderBase orderBase, OrderSource orderSource,
            Short status, Log activityLog) throws Exception {
        JSONObject orderObject = new JSONObject();
        orderObject.set("orderId", orderBase.getId());
        orderObject.set("coreOrderId", orderSource.getCoreOrderId());
        orderObject.set("status", status);
        orderObject.set("platformOrderId", dataObject.getStr("platformOrderId"));
        orderObject.set("vehicleColor", orderSource.getVehicleColor());
        orderObject.set("vehicleNo", orderSource.getVehicleNo());
        orderObject.set("vehicleModel", orderSource.getVehicleModel());
        orderObject.set("driverPhoneVirtual", orderSource.getDriverPhoneVirtual());
        orderObject.set("driverPhone", orderSource.getDriverPhone());
        orderObject.set("driverName", orderSource.getDriverName());
        orderObject.set("driverLevel", orderSource.getDriverLevel());

        ObjectMapper objectMapper = new ObjectMapper();
        String msgStr = objectMapper.writeValueAsString(orderObject);
        String key = orderBase.getId() + "_" + orderBase.getState();

        String destination = TOPIC_ORDER_CHG_TO_REPORT + ":" + TAG_ORDER_CHG_TO_REPORT;
        Message message = MessageBuilder.withPayload(msgStr).setHeader("KEYS", key).build();
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            public void onSuccess(SendResult sendResult) {
                log.info("notifyReportService succeed");
            }

            public void onException(Throwable throwable) {
                logService.saveErrorLogAsync(activityLog, "notify ka report fail.error msg:" + throwable.getMessage());
                log.error("booking notifyReportService fail; {}", throwable.getMessage());
            }
        });

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/report/compensation/handleBookingStatusChg");
            remoteCallDto.setContent(msgStr);
            BaseResponse response = remoteCallFeign.call(remoteCallDto);
            if (response.getCode() != 0) {
                logService.saveErrorLogAsync(activityLog, "notify core report fail");
                log.error("booking notifyReportCoreService fail; ", response);
            }
        } catch (Exception ex) {
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("通知core端报表出现异常【OrderService->notifyReport,orderId:{}】", orderBase.getId(), ex);
        }
    }

    /**
     * 修改进行中订单的缓存信息
     */
    private void moidfyRunningOrderCache(String oldCoreOrderId, String newCoreOrderId, Short orderStatus,
            OrderSource orderSource) {
        if (StringUtils.isEmpty(newCoreOrderId)) {
            newCoreOrderId = oldCoreOrderId;
        }

        CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(orderSource.getUserId(), orderSource.getOrderId());

        if (cacheOrder == null) {
            return;
        }

        CacheICarOrder cacheICarOrder = (CacheICarOrder) redisUtil
                .get(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + oldCoreOrderId);
        if (cacheICarOrder != null) {
            redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + newCoreOrderId, cacheICarOrder);
            cacheICarOrder.setState(orderStatus);
            Long currentExpire = redisUtil.getExpire(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + oldCoreOrderId);
            redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + newCoreOrderId, cacheICarOrder, currentExpire);
        }

        if (null != orderSource && null != cacheOrder) {
            if (null == cacheOrder.getDriverInfo()) {
                DriverInfo driverInfo = new DriverInfo();
                driverInfo.setName(orderSource.getDriverName());
                driverInfo.setPhone(orderSource.getDriverPhone());
                driverInfo.setVehicleModel(orderSource.getVehicleModel());
                driverInfo.setVehicleColor(orderSource.getVehicleColor());
                driverInfo.setVehicleNo(orderSource.getVehicleNo());
                driverInfo.setLevel(orderSource.getDriverLevel());
                driverInfo.setPhoneVirtual(orderSource.getDriverPhoneVirtual());
                driverInfo.setAvatar(orderSource.getDriverAvatar());
                cacheOrder.setDriverInfo(driverInfo);
            } else {
                cacheOrder.getDriverInfo().setName(orderSource.getDriverName());
                cacheOrder.getDriverInfo().setPhone(orderSource.getDriverPhone());
                cacheOrder.getDriverInfo().setVehicleModel(orderSource.getVehicleModel());
                cacheOrder.getDriverInfo().setVehicleColor(orderSource.getVehicleColor());
                cacheOrder.getDriverInfo().setVehicleNo(orderSource.getVehicleNo());
                cacheOrder.getDriverInfo().setLevel(orderSource.getDriverLevel());
                cacheOrder.getDriverInfo().setPhoneVirtual(orderSource.getDriverPhoneVirtual());
                cacheOrder.getDriverInfo().setAvatar(orderSource.getDriverAvatar());
            }

            // 更新order缓存
            cacheOrder.setState(orderStatus);
            cacheOrder.setSourceNameCn(orderSource.getSourceNameCn());
            cacheOrder.setSourceNameEn(orderSource.getSourceNameCn());
            if (StringUtils.isNotEmpty(newCoreOrderId)) {
                cacheOrder.setCoreOrderId(newCoreOrderId);
            }
            log.info("moidfyRunningOrderCache3:state:" + cacheOrder.getState());

            long currentExpire = redisUtil
                    .getExpire(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + orderSource.getUserId().toString());
            redisUtil.hashPut(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + orderSource.getUserId().toString(),
                    orderSource.getOrderId().toString(), JSONUtil.toJsonStr(cacheOrder), currentExpire);
        }
    }

    /**
     * 自定义参数拼装
     */
    public void setOrderParamByCustomInfo(Long companyId, Long userId, CreateOrderParam orderParam)
            throws Exception {
        // List<Long> companyIds = orderLimitService.getOrderLimitOneH5Companys();
        // if (CollectionUtils.isEmpty(companyIds))
        // return;
        // if (companyIds.size() == 0)
        // return;
        // if (!companyIds.contains(companyId))
        // return;

        // if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_USER_CUSTOM_INFO +
        // userId.toString()))
        // throw new BusinessException("下单限制已失效，不可再次下单，请重新登录！");

        // 获取用户登录缓存中的信息
        RedisCustomInfoH5 redisCustomInfo = null;
        try {
            redisCustomInfo = JSONUtil.toBean(
                    JSONUtil.toJsonStr(redisUtil.get(CacheConsts.REDIS_KEY_USER_CUSTOM_INFO + userId.toString())),
                    RedisCustomInfoH5.class);
        } catch (Exception e) {
            log.error("获取redis 存储的自定义信息失败：{}", e.getMessage());

            // throw new BusinessException("获取用户登录信息失败，请重新登录!");
        }

        if (null != redisCustomInfo) {
            OrderExtraParameter extraParameter = orderParam.getExtraParameter();
            if (null == extraParameter) {
                extraParameter = new OrderExtraParameter();
            }
            JSONObject customInfo = extraParameter.getCustomInfo();
            if (null == customInfo) {
                customInfo = new JSONObject();
            }

            // 合作方单号
            if (StrUtil.isNotBlank(redisCustomInfo.getPartnerOrderId())) {
                orderParam.setPartnerOrderId(redisCustomInfo.getPartnerOrderId());
            }

            if (StrUtil.isNotBlank(redisCustomInfo.getCostCenter())) {
                customInfo.set("costCenter", redisCustomInfo.getCostCenter());
                customInfo.set("costCenterCode", redisCustomInfo.getCostCenter());
            }

            if (StrUtil.isNotBlank(redisCustomInfo.getCostCenterCode())) {
                customInfo.set("costCenterCode", redisCustomInfo.getCostCenterCode());
            }

            if (StrUtil.isNotBlank(redisCustomInfo.getSceneCode())) {
                customInfo.set("sceneCode", redisCustomInfo.getSceneCode());
            }

            if (StrUtil.isNotBlank(redisCustomInfo.getTaxiLocationLimits())) {
                customInfo.set("taxiLocationLimits", redisCustomInfo.getTaxiLocationLimits());
            }

            extraParameter.setCustomInfo(customInfo);
            orderParam.setExtraParameter(extraParameter);
        }
    }

    /**
     * 自定义限制条件参数拼装
     *
     * @param requestCheckOrderDto
     * @param orderParam
     * @throws Exception
     */
    public void setOrderParamByOrderLimit(RequestCheckOrderDto requestCheckOrderDto, CreateOrderParam orderParam)
            throws Exception {
        // 获取用户登录缓存中的信息
        RedisCpol redisCpol = null;
        try {
            redisCpol = orderLimitService.getRedisCpol(orderParam.getCompanyId(), orderParam.getUserId());
        } catch (Exception e) {
            log.error("获取redis 存储的下单限制失败：{}", e.getMessage());

            if (regulationService.isInvalidationCompany(orderParam.getCompanyId())) {
                RegulationConfigBo regulationConfigBo = regulationService
                        .invalidationCompanyConfig(orderParam.getCompanyId());
                if (null != regulationConfigBo) {
                    throw new BusinessException(regulationConfigBo.getInvalidation().getPrompt());
                }
            }

            throw new BusinessException("获取用户登录信息失败，请重新登录!");
        }

        OrderExtraParameter extraParameter = orderParam.getExtraParameter();
        if (null == extraParameter) {
            extraParameter = new OrderExtraParameter();
        }
        JSONObject customInfo = extraParameter.getCustomInfo();
        if (null == customInfo) {
            customInfo = new JSONObject();
        }

        if (null != redisCpol && null != redisCpol.getRegulationInfo()) {
            // 申请单号
            if (null != redisCpol.getRegulationInfo().getCode()) {
                orderParam.setPreDepartApplyCode(redisCpol.getRegulationInfo().getCode());
            }
            // 合作方单号
            if (null != redisCpol.getRegulationInfo().getPartnerOrderId()) {
                orderParam.setPartnerOrderId(redisCpol.getRegulationInfo().getPartnerOrderId());
            }
            // 本单服务费(单位为分)!
            if (null != redisCpol.getRegulationInfo().getServiceFee()) {
                customInfo.set("serviceFee", redisCpol.getRegulationInfo().getServiceFee());
            }

            // 本单 加价规则
            if (null != redisCpol.getRegulationInfo().getIncreaseAmountRule()) {
                customInfo.set(IncreaseAmountConstant.INCREASE_AMOUNT_RULE,
                        redisCpol.getRegulationInfo().getIncreaseAmountRule());
            }

            // 自定义参数
            if (null != redisCpol.getCustomStr()) {
                customInfo.set("customStr", redisCpol.getCustomStr());
            }

            if (null != redisCpol.getCustomInfo()) {

                JSONObject jsonObject = redisCpol.getCustomInfo();
                if (null != jsonObject.getStr("costCenter")) {
                    customInfo.set("costCenter", jsonObject.getStr("costCenter"));
                }
                if (null != jsonObject.getStr("sceneCode")) {
                    customInfo.set("sceneCode", jsonObject.getStr("sceneCode"));
                }

            }

            extraParameter.setCustomInfo(customInfo);
            orderParam.setExtraParameter(extraParameter);

            // 超出管控额度是否允许下单
            if (null != redisCpol.getRegulationInfo().getAllowExcess()) {
                boolean allowExcess = redisCpol.getRegulationInfo().getAllowExcess();
                requestCheckOrderDto.setAllowExcess(allowExcess);
            }
        }

        // 如果企业没开启自定义条件限制直接返回
        if (!orderLimitService.isOpenOrderLimitConfig(orderParam.getCompanyId())) {
            log.info("未开启下单限制CompanyId：{}", orderParam.getCompanyId());
            return;
        }

        // TODO 兼容老版本 发布中电建后删除else
        if (null != orderLimitService.getCompanyLimitList(orderParam.getCompanyId(), orderParam.getUserId(),
                orderParam.getDepartCityCode(), orderParam, null)) {
            // begin 获取企业自定义限制信息 //修改自定义配置缓存由下单服务缓存update by Lok 20230323
            List<KeyValue> customLimitMapping = orderLimitService.getCustomLimitMapping(
                    orderParam.getCompanyId(),
                    orderParam.getUserId(),
                    orderParam.getDepartCityCode(),
                    orderParam);

            log.info("orderParam=>>" + JSONUtil.toJsonStr(orderParam));
            // if (null == redisCpol &&
            // orderLimitService.isOpenOrderLimitConfig(orderParam.getCompanyId())) {
            // throw new Exception("下单限制已失效，不可再次下单，请重新登录！");
            // }
            log.info("customLimitMapping=>>" + JSONUtil.toJsonStr(customLimitMapping));
            requestCheckOrderDto.setCustomLimits(customLimitMapping);
            RedisCpolRegulationInfo regulationInfo = redisCpol.getRegulationInfo();
            if (null != regulationInfo) {
                requestCheckOrderDto.setPreDepartApplyCode(regulationInfo.getCode());
                if (null != regulationInfo.getValidDate()) {
                    requestCheckOrderDto.setValidDateFrom(
                            DateUtil.format(regulationInfo.getValidDate().getFrom(), "yyyy-MM-dd HH:mm:ss"));
                    requestCheckOrderDto.setValidDateTo(
                            DateUtil.format(regulationInfo.getValidDate().getTo(), "yyyy-MM-dd HH:mm:ss"));
                }
            }

            if (CollectionUtils.isNotEmpty(customLimitMapping)) {
                // 3005 limitedAmount
                List<KeyValue> collect = customLimitMapping.stream().filter(o -> StringUtils.equals(o.getKey(), "3005"))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)) {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject jsonObject1 = JSONUtil.parseObj(collect.get(0).getValue());
                    // 限制金额(单位为分)
                    customInfo.set("onceAmount", jsonObject1.get("value"));
                }
                extraParameter.setCustomInfo(customInfo);
                orderParam.setExtraParameter(extraParameter);
            }
            // end
        } else {
            // begin 获取企业自定义限制信息
            List<CompanyLimitVo> companyLimitList = new cn.hutool.json.JSONArray(
                    redisUtil.hashGet(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderParam.getCompanyId(),
                            orderParam.getUserId().toString()))
                    .toList(CompanyLimitVo.class);

            if (companyLimitList != null && companyLimitList.size() > 0) {
                if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + orderParam.getCompanyId())) {
                    List<CompanyLimitMappingVo> mappingList = orderBaseMapper
                            .selectLimitMappingByCompanyId(orderParam.getCompanyId());
                    if (mappingList != null && mappingList.size() > 0) {
                        redisUtil.set(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + orderParam.getCompanyId(),
                                mappingList);
                    }
                }

                List<CompanyLimitMappingVo> mappingList = (List<CompanyLimitMappingVo>) redisUtil
                        .get(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + orderParam.getCompanyId());
                List<KeyValue> customLimitMapping = new ArrayList<>();
                for (CompanyLimitMappingVo vo : mappingList) {
                    for (CompanyLimitVo companyLimitVo : companyLimitList) {
                        if (vo.getType() == companyLimitVo.getType()) {
                            KeyValue keyValue = new KeyValue();
                            keyValue.setKey(vo.getParaCode());
                            keyValue.setValue(companyLimitVo.getValue().toString());
                            customLimitMapping.add(keyValue);
                            break;
                        }
                    }
                }

                requestCheckOrderDto.setCustomLimits(customLimitMapping);

            } else {
                if (regulationService.isInvalidationCompany(orderParam.getCompanyId())) {
                    RegulationConfigBo regulationConfigBo = regulationService
                            .invalidationCompanyConfig(orderParam.getCompanyId());
                    if (null != regulationConfigBo) {
                        throw new BusinessException(regulationConfigBo.getInvalidation().getPrompt());
                    }
                }

                if (orderLimitService.isOpenOrderLimitConfig(orderParam.getCompanyId())) {
                    log.error("setOrderParamByOrderLimit===>下单限制已失效，CompanyId：{},UserId:{}", orderParam.getCompanyId(),
                            orderParam.getUserId());
                    throw new BusinessException("下单限制已失效，不可再次下单，请重新登录！");
                }
            }
            // end
        }
    }

    /**
     * 自动重新派单（平台/司机取消）
     *
     * @param dataObject
     * @return true 重新派单成功
     *         false 重新派单失败，取消订单
     * @throws Exception
     */
    @Override
    public boolean rePlaceOrderAfterCoreCancelAfreshPlaceOrder(JSONObject dataObject, Long orderId) throws Exception {
        try {
            String coreOrderId = dataObject.getStr("coreOrderId");
            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);

            // 如果使用预约管家不重新派单
            if (this.isBooking(orderBase)) {
                coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                OrderBase orderBaseRePlaceOrderOneSource = new OrderBase();
                orderBaseRePlaceOrderOneSource.setId(orderBase.getId());
                orderBaseRePlaceOrderOneSource.setState((short) 1);
                orderBaseMapper.updateByPrimaryKeySelective(orderBaseRePlaceOrderOneSource);
                OrderSource orderSourceRecordRePlaceOrderOneSource = orderSourceMapper.selectLatestByOrderId(orderId);
                OrderSource recordRePlaceOrderOneSource = new OrderSource();
                recordRePlaceOrderOneSource.setId(orderSourceRecordRePlaceOrderOneSource.getId());
                recordRePlaceOrderOneSource.setState((short) 1);
                orderSourceMapper.updateByPrimaryKeySelective(recordRePlaceOrderOneSource);
                return false;
            }

            if (null != dataObject.get("orderDetail")
                    && null != ((JSONObject) dataObject.get("orderDetail")).get("fee")) {
                JSONObject fee = (JSONObject) ((JSONObject) dataObject.get("orderDetail")).get("fee");
                BigDecimal totalPrice = fee.getBigDecimal("totalPrice");
                if (totalPrice.compareTo(new BigDecimal("0")) == 1) {
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            }

            // 校验
            boolean isRePlaceOrder = this.rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck(orderBase);
            if (!isRePlaceOrder) {
                coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                return false;
            }

            // 预估
            Map<String, Object> estimate = this.rePlaceOrderAfterCoreCancelAfreshPlaceOrderEstimate(orderBase);
            List<SelectedCar> cars = new ArrayList<>();
            if (orderBase.getCustomInfo() != null) {
                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
                if (customJSONObject != null) {
                    if (customJSONObject.containsKey("selectedCars")) {
                        cars = customJSONObject.getBeanList("selectedCars", SelectedCar.class);
                    }
                }
            }
            String estimateId = (String) estimate.get("estimateId");
            cars = this.getEstimateCarList(estimate, cars);
            Date date = (Date) estimate.get("date");

            // 重新下单
            RePlaceOrderReq rePlaceOrderReq = new RePlaceOrderReq();
            rePlaceOrderReq.setEstimateId(estimateId);
            rePlaceOrderReq.setCoreOrderId(coreOrderId);
            rePlaceOrderReq.setDepartTime(date);
            rePlaceOrderReq.setCars(cars);

            // rePlaceOrderReq.setDestPoi(orderBase.getDestPoi());
            // rePlaceOrderReq.setDepartPoi(orderBase.getDepartPoi());

            Boolean rePlaceOrder = coreOrderService.afterCoreCancelAfreshPlaceOrder(rePlaceOrderReq, orderId);

            // 失败通知中台取消订单
            if (!rePlaceOrder) {
                coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaRceOrder===>重新派单失败orderId：{},coreOrderId:{}", orderId,
                        coreOrderId);
                return false;
            } else {
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrder===>重新派单成功orderId：{},coreOrderId:{}", orderId,
                        coreOrderId);
                OrderBase orderBaseRePlaceOrderOneSource = new OrderBase();
                orderBaseRePlaceOrderOneSource.setId(orderBase.getId());
                orderBaseRePlaceOrderOneSource.setState((short) 1);
                log.info("rePlaceOrderOneSource===>待修改order_base信息: {}, 修改参数: {}", JSONUtil.toJsonStr(orderBase),
                        JSONUtil.toJsonStr(orderBaseRePlaceOrderOneSource));
                int updateCount1 = orderBaseMapper.rePlaceUpdateStateByOrderId(orderBaseRePlaceOrderOneSource);
                OrderSource orderSourceRecordRePlaceOrderOneSource = orderSourceMapper.selectLatestByOrderId(orderId);
                OrderSource recordRePlaceOrderOneSource = new OrderSource();
                recordRePlaceOrderOneSource.setId(orderSourceRecordRePlaceOrderOneSource.getId());
                recordRePlaceOrderOneSource.setState((short) 1);
                log.info("rePlaceOrderOneSource===>待修改order_source信息: {}, 修改参数: {}",
                        JSONUtil.toJsonStr(orderSourceRecordRePlaceOrderOneSource),
                        JSONUtil.toJsonStr(recordRePlaceOrderOneSource));
                int updateCount2 = orderSourceMapper.rePlaceUpdateStateByOrderId(recordRePlaceOrderOneSource);
                if (updateCount1 > 0
                        && !redisUtil.hasKey(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderBase.getId())) {
                    log.info("rePlaceOrderOneSource===>重新派单成功orderId：{},coreOrderId:{}", orderId, coreOrderId);
                    return true;
                } else {
                    log.info(
                            "rePlaceOrderOneSource===>重新派单失败orderId：{},coreOrderId:{} ===> 原因：订单已经取消 [updateCount1:{},updateCount2:{}]",
                            orderId, coreOrderId, updateCount1, updateCount2);
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("rePlaceOrderAfterCoreCancelAfreshPlaceOrder===>异常：{}", e);
            return false;
        }
    }

    /**
     * 自动重新派单预估
     */
    private Map<String, Object> rePlaceOrderAfterCoreCancelAfreshPlaceOrderEstimate(OrderBase orderBase)
            throws Exception {
        Date date = new Date();
        CreateOrderParam orderParam = new CreateOrderParam();
        if (orderBase.getServiceType() == (short) 1) {
            orderParam.setDepartTime(date);
        } else if (orderBase.getServiceType() == (short) 2) {
            OrderLimitConfig redisCacheCompanyConfig = orderLimitService.getRedisCacheCompanyConfig(
                    CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL,
                    CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL_TYPE,
                    0L);
            String value = redisCacheCompanyConfig.getValue();
            Long max = (long) 41;
            Long min = (long) 40;
            if (StringUtils.isNotBlank(value)) {
                JSONObject jsonObject = JSONUtil.parseObj(value);
                JSONObject serviceType2Time = jsonObject.getJSONObject("serviceType2Time");
                max = serviceType2Time.getLong("max");
                min = serviceType2Time.getLong("min");
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaRceOrder===>重新派单 预约单 orderId：{}，预约单配置{}",
                        orderBase.getId(), redisCacheCompanyConfig.getValue());

            } else {
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaRceOrder===>重新派单 预约单 orderId：{}，无配置，使用默认配置",
                        orderBase.getId());

            }
            long time = ((orderBase.getDepartTime()).getTime() - (new Date()).getTime()) / (1000 * 60);
            if (time > max) {
                orderParam.setDepartTime(orderBase.getDepartTime());
                date = orderBase.getDepartTime();
            } else {
                orderParam.setDepartTime(date);
            }
        } else {
            orderParam.setDepartTime(orderBase.getDepartTime());
        }

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
     * 校验是否需要自动重新派单
     * <p>
     * 检查项
     * 1.检查是否存在订单
     * 2.检查是否预约管家订单
     * 3.检查是否重新派单校验次数在规定内
     * 4.检查是否设置了途径点
     * 5.检查预约服务类型出发时间
     *
     * @param orderBase
     * @return true 需要重新派单
     *         false 不需要重新派单，取消订单
     */
    private boolean rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck(OrderBase orderBase) throws Exception {

        // 检查是否存在订单。订单不存在，通知中台取消订单
        if (null == orderBase) {
            log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：订单不存在",
                    orderBase.getId());
            return false;
        }

        // 检查是否预约管家订单。是预约管家订单，通知中台取消订单
        if (this.isBooking(orderBase)) {
            log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：是预约管家订单",
                    orderBase.getId());
            return false;
        }

        // 检查是否重新派单校验次数在规定内。重新派单校验次数不在规定内，通知中台取消订单
        Boolean check = coreOrderService.afterCoreCancelAfreshPlaceOrderCheck(orderBase.getId());
        if (!check) {
            log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：重新派单校验次数不在规定内",
                    orderBase.getId());
            return false;
        }

        // 检查是否重新派单校验次数在规定内。最大重派次数，通知中台取消订单
        Boolean checkMax = coreOrderService.afterCoreCancelAfreshPlaceOrderCheckMax(orderBase.getId());
        if (!checkMax) {
            log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：重新派单校验次数已经超过2次",
                    orderBase.getId());
            return false;
        }

        // 检查是否设置了途径点。设置了途径点，通知中台接取消订单
        boolean isPassingPoint = false;
        if (orderBase.getCustomInfo() != null) {
            JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
            if (customJSONObject != null) {
                if (customJSONObject.containsKey("isPassingPoint")) {
                    isPassingPoint = customJSONObject.getBool("isPassingPoint", false);
                }
            }
        }
        if (isPassingPoint) {
            log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：设置了途径点",
                    orderBase.getId());
            return false;
        }

        // 检查预约服务类型出发时间
        if (orderBase.getServiceType() == (short) 2) {
            OrderLimitConfig redisCacheCompanyConfig = orderLimitService.getRedisCacheCompanyConfig(
                    CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL,
                    CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL_TYPE,
                    0L);
            String value = redisCacheCompanyConfig.getValue();
            Long max = (long) 41;
            Long min = (long) 40;
            if (StringUtils.isBlank(value)) {
                log.error(
                        "rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验警告，orderId:{}，原因：预约单，未获取到预约单配置，使用默认配置max：41，min：40",
                        orderBase.getId());
            } else {
                JSONObject jsonObject = JSONUtil.parseObj(value);
                JSONObject serviceType2Time = jsonObject.getJSONObject("serviceType2Time");
                max = serviceType2Time.getLong("max");
                min = serviceType2Time.getLong("min");
            }
            long time = ((orderBase.getDepartTime()).getTime() - (new Date()).getTime()) / (1000 * 60);
            if (time < min) {
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：预约单，已经超过规定出发时间",
                        orderBase.getId());
                return false;
            }
        }

        // 检查用户是否主动取消。
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderBase.getId())) {
            try {
                log.info("rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck===>重新派单校验不通过，orderId:{}，原因：用户正在主动取消",
                        orderBase.getId());
                return false;
            } catch (Exception ex) {
                log.error(
                        "从缓存获取订单是否被取消时出现异常【OrderService->rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck,orderId:{}】",
                        orderBase.getId(), ex);
            }
        }
        return true;
    }

    /**
     * 自动重新派单 （屏蔽同一个司机，单个平台下单 平台取消 ）
     *
     * @param dataObject
     * @return
     * @throws Exception
     */
    @Override
    public boolean rePlaceOrderOneSource(JSONObject dataObject, Long orderId) throws Exception {
        String coreOrderId = dataObject.getStr("coreOrderId");
        String estimateId = null;
        try {

            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
            if (null == orderBase) {
                throw new BusinessException("rePlaceOrderOneSource ===> 订单不存在");
            }
            // 如果使用预约管家不重新派单
            if (this.isBooking(orderBase)) {
                coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                OrderBase orderBaseRePlaceOrderOneSource = new OrderBase();
                orderBaseRePlaceOrderOneSource.setId(orderBase.getId());
                orderBaseRePlaceOrderOneSource.setState((short) 1);
                orderBaseMapper.updateByPrimaryKeySelective(orderBaseRePlaceOrderOneSource);
                OrderSource orderSourceRecordRePlaceOrderOneSource = orderSourceMapper.selectLatestByOrderId(orderId);
                OrderSource recordRePlaceOrderOneSource = new OrderSource();
                recordRePlaceOrderOneSource.setId(orderSourceRecordRePlaceOrderOneSource.getId());
                recordRePlaceOrderOneSource.setState((short) 1);
                orderSourceMapper.updateByPrimaryKeySelective(recordRePlaceOrderOneSource);
                return false;
            }

            if (null != dataObject.get("orderDetail")
                    && null != ((JSONObject) dataObject.get("orderDetail")).get("fee")) {
                JSONObject fee = (JSONObject) ((JSONObject) dataObject.get("orderDetail")).get("fee");
                BigDecimal totalPrice = fee.getBigDecimal("totalPrice");
                if (totalPrice.compareTo(new BigDecimal("0")) == 1) {
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            }

            List<SelectedCar> cars = new ArrayList<>();
            if (orderBase.getCustomInfo() != null) {
                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
                if (customJSONObject != null) {
                    if (customJSONObject.containsKey("selectedCars")) {
                        cars = customJSONObject.getBeanList("selectedCars", SelectedCar.class);
                    }
                }
            }

            if (cars.size() > 1) {
                if (rePlaceOrderAfterCoreCancelSwitch) {
                    log.info("屏蔽同一个司机多车型触发取消重新派单===>[orderId:{}]", orderId);
                    boolean isSuccess = this.rePlaceOrderAfterCoreCancelAfreshPlaceOrder(dataObject, orderId);
                    log.info("屏蔽同一个司机多车型触发取消重新派单===>[orderId:{},isSucces:{}]", orderId, isSuccess);
                    return isSuccess;
                } else {
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            }

            Date date = new Date();

            CreateOrderParam orderParam = new CreateOrderParam();
            if (orderBase.getServiceType() == (short) 1) {
                orderParam.setDepartTime(date);
            } else if (orderBase.getServiceType() == (short) 2) {
                OrderLimitConfig redisCacheCompanyConfig = orderLimitService.getRedisCacheCompanyConfig(
                        CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL,
                        CompanyConstant.COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL_TYPE,
                        0L);
                String value = redisCacheCompanyConfig.getValue();
                if (StringUtils.isBlank(value)) {
                    log.error("rePlaceOrderOneSource===>重新派单失败orderId：{},coreOrderId:{}，原因：预约单，未获取到预约单配置", orderId,
                            coreOrderId);
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
                log.info("rePlaceOrderOneSource===>重新派单 预约单 orderId：{},coreOrderId:{}，预约单配置{}", orderId, coreOrderId,
                        redisCacheCompanyConfig.getValue());
                JSONObject jsonObject = JSONUtil.parseObj(value);
                JSONObject serviceType2Time = jsonObject.getJSONObject("serviceType2Time");
                Long max = serviceType2Time.getLong("max");
                Long min = serviceType2Time.getLong("min");
                long time = ((orderBase.getDepartTime()).getTime() - (new Date()).getTime()) / (1000 * 60);
                if (time > max) {
                    orderParam.setDepartTime(orderBase.getDepartTime());
                    date = orderBase.getDepartTime();
                } else if (min <= time && time <= max) {
                    orderParam.setDepartTime(date);
                } else {
                    log.info("rePlaceOrderOneSource===>重新派单失败orderId：{},coreOrderId:{}，原因：预约单，已经超过出发时间", orderId,
                            coreOrderId);
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            } else {
                orderParam.setDepartTime(orderBase.getDepartTime());
            }

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
            orderParam.setDestPoi(orderBase.getDestPoi());
            orderParam.setDepartPoi(orderBase.getDepartPoi());

            Map<String, Object> estimate = coreOrderService.estimate(orderParam, cars);
            estimateId = (String) estimate.get("estimateId");
            cars = this.getEstimateCarList(estimate, cars);

            RePlaceOrderReq rePlaceOrderReq = new RePlaceOrderReq();
            // 次数校验是否通过
            Boolean check = rePlaceOrderAfterCoreCancelAfreshPlaceOrderCheck(orderBase);
            // 重新派单是否成功
            Boolean rePlaceOrder = false;
            if (check) {
                rePlaceOrderReq.setEstimateId(estimateId);
                rePlaceOrderReq.setCoreOrderId(coreOrderId);
                rePlaceOrderReq.setDepartTime(date);
                rePlaceOrderReq.setCars(cars);
                List<RequestPlaceOrderExtraParamsDto> extraParams = new ArrayList<>();
                extraParams.add(this.getExtraParams(CoreOrderConstant.EXCLUDE_VEHICLE_NO, ""));
                rePlaceOrderReq.setExtraParams(extraParams);

                // rePlaceOrderReq.setDestPoi(orderBase.getDestPoi());
                // rePlaceOrderReq.setDepartPoi(orderBase.getDepartPoi());

                rePlaceOrder = coreOrderService.afterCoreCancelAfreshPlaceOrder(rePlaceOrderReq, orderId);
            }
            if (!check || !rePlaceOrder) {
                log.info("rePlaceOrderOneSource===>重新派单失败orderId：{},coreOrderId:{}", orderId, coreOrderId);
                coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                return false;
            } else {
                log.info("rePlaceOrderOneSource===>重新派单成功orderId：{},coreOrderId:{}", orderId, coreOrderId);
                OrderBase orderBaseRePlaceOrderOneSource = new OrderBase();
                orderBaseRePlaceOrderOneSource.setId(orderBase.getId());
                orderBaseRePlaceOrderOneSource.setState((short) 1);
                int updateCount1 = orderBaseMapper.rePlaceUpdateStateByOrderId(orderBaseRePlaceOrderOneSource);
                OrderSource orderSourceRecordRePlaceOrderOneSource = orderSourceMapper.selectLatestByOrderId(orderId);
                OrderSource recordRePlaceOrderOneSource = new OrderSource();
                recordRePlaceOrderOneSource.setId(orderSourceRecordRePlaceOrderOneSource.getId());
                recordRePlaceOrderOneSource.setState((short) 1);
                int updateCount2 = orderSourceMapper.rePlaceUpdateStateByOrderId(recordRePlaceOrderOneSource);

                if (updateCount1 > 0
                        && !redisUtil.hasKey(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderBase.getId())) {
                    log.info("rePlaceOrderOneSource===>重新派单成功orderId：{},coreOrderId:{}", orderId, coreOrderId);
                    return true;
                } else {
                    log.info(
                            "rePlaceOrderOneSource===>重新派单失败orderId：{},coreOrderId:{} ===> 原因：订单已经取消 [updateCount1:{},updateCount2{}]",
                            orderId, coreOrderId, updateCount1, updateCount2);
                    coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("rePlaceOrderOneSource===>异常：{}", e);
            coreOrderService.afterCoreCancelClose(coreOrderId, orderId);
            return false;
        }
    }

    /**
     * 本次预估 取值 上一次预估车型
     */
    public List<SelectedCar> getEstimateCarList(Map<String, Object> estimateNew, List<SelectedCar> carsOld) {
        List<SelectedCar> selectedCarList = new ArrayList<SelectedCar>();
        JSONArray estimateCarArray = JSONUtil.parseArray(estimateNew.get("rows"));
        log.info("本次预估 取值 上一次预估车型 预估{}", estimateCarArray.toString());
        for (int i = 0; i < estimateCarArray.size(); i++) {
            JSONObject carObject = estimateCarArray.get(i, JSONObject.class);
            int carType = carObject.getInt("carType", -1);
            int carSource = carObject.getInt("carSource", -1);
            BigDecimal estimatePrice = carObject.getBigDecimal("estimatePrice");
            BigDecimal actualEstimatePrice = carObject.getBigDecimal("platformEstimatePrice");
            String dynamicCode = carObject.getStr("dynamicCode");
            String carTypeName = carObject.getStr("carTypeName");
            String subCarType = carObject.getStr("subCarType");

            for (int j = 0; j < carsOld.size(); j++) {
                SelectedCar selectedCar = carsOld.get(j);
                int carTypeSelectedCar = selectedCar.getCarLevel();
                int carSourceSelectedCar = selectedCar.getCarSourceId();
                String carTypeNameSelectedCar = selectedCar.getCarLevelName();
                if (carType == carTypeSelectedCar &&
                        carSource == carSourceSelectedCar &&
                        StringUtils.equals(carTypeName, carTypeNameSelectedCar) &&
                        StrUtil.equals(subCarType, selectedCar.getSubCarType())) {
                    SelectedCar selectedCar1 = new SelectedCar();
                    selectedCar1.setCarLevel(carType);
                    selectedCar1.setCarSourceId(carSource);
                    selectedCar1.setEstimatePrice(estimatePrice);
                    selectedCar1.setActualEstimatePrice(actualEstimatePrice);
                    selectedCar1.setDynamicCode(dynamicCode);
                    selectedCar1.setCarLevelName(carTypeName);
                    selectedCarList.add(selectedCar1);
                }

            }
        }
        return selectedCarList;
    }

    /**
     * 包车下单
     * 不具有通用性，走单独方法
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> placeOrderForRental(CreateOrderParam orderParam) throws Exception {
        try {
            checkOrderBasicParam(orderParam);

            // 判断重复叫车
            if (orderParam.getConfirm() == null || !orderParam.getConfirm()) {
                // todo 重复叫车
            }

            if (orderParam.getAccountId() == null) {
                throw new BusinessException("accountId参数不能为空");
            }

            if (orderParam.getCars() == null) {
                throw new BusinessException("cars参数不能为空");
            }
            if (orderParam.getPassengerPhone() == null || !Validator.isMobile(orderParam.getPassengerPhone())) {
                throw new BusinessException("手机号不正确");
            }
            // 默认立即用车
            if (orderParam.getDepartTime() == null) {
                orderParam.setDepartTime(new Date());
            }

            List<Long> couponIds = new ArrayList<>();// 预估使用的优惠券
            BigDecimal maxEstimatePrice = BigDecimal.ZERO;

            for (SelectedCar car : orderParam.getCars()) {
                if (car.getCouponId() != null && !couponIds.contains(car.getCouponId())) {
                    couponIds.add(car.getCouponId());
                }

                if (maxEstimatePrice.compareTo(car.getEstimatePrice()) == -1) {
                    maxEstimatePrice = car.getEstimatePrice();
                }
            }

            if (couponIds.size() > 0) {
                if (orderParam.getExtraParameter() == null) {
                    orderParam.setExtraParameter(new OrderExtraParameter());
                }

                JSONObject couponIdObject = new JSONObject();
                couponIdObject.set("couponIds", couponIds);

                if (orderParam.getExtraParameter().getCustomInfo() == null) {
                    orderParam.getExtraParameter().setCustomInfo(couponIdObject);
                } else {
                    orderParam.getExtraParameter().getCustomInfo().set("couponIds", couponIds);
                }
            }

            BigDecimal extraFee = BigDecimal.ZERO;
            if (orderParam.getExtraServices() != null && orderParam.getExtraServices().size() != 0) {
                extraFee = getExtralFee(orderParam.getCompanyId(), orderParam.getExtraServices());
            }

            // 调用.net接口检查订单参数
            maxEstimatePrice = maxEstimatePrice.add(extraFee);
            BigDecimal frozenAmount = maxEstimatePrice.multiply(FROZEN_AMOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
            RequestCheckOrderDto requestCheckOrderDto = new RequestCheckOrderDto();
            BeanUtil.copyProperties(orderParam, requestCheckOrderDto, false);
            requestCheckOrderDto.setFrozenAmount(frozenAmount);
            requestCheckOrderDto.setCheckType((short) 2);

            BaseResponse response = configurationFeign.checkOrder(requestCheckOrderDto);

            if (response.getCode() != 0) {
                if (response.getCode() == 1009 || response.getCode() == 1010 || response.getCode() == 1011) {
                    throw new CustomException(response.getCode(), response.getMessage());
                } else {
                    if (response.getCode() == 3 && ("用户未设置账户,暂时无法用车.".equals(response.getMessage())
                            || "The user has not set up an account and is temporarily unable to use the vehicle."
                                    .equals(response.getMessage()))) {
                        log.info("checkOrder 无默认账户，也可以下单，结算时，传企业默认账户:{}", response.getMessage());
                        // 获取默认账户填充账户新
                        Long accountId = billService.getDefaultAccountId(orderParam.getCompanyId());
                        orderParam.setAccountId(accountId);
                        billService.insertAccountUser(orderParam.getCompanyId(), orderParam.getUserId(), accountId);
                    } else {
                        throw new BusinessException(response.getMessage());
                    }
                }
            }

            if (orderParam.getExtraParameter() == null) {
                orderParam.setExtraParameter(new OrderExtraParameter());
            }

            // 将选择的车型加到customInfo中
            if (orderParam.getExtraParameter().getCustomInfo() == null) {
                JSONObject selectedCarObject = new JSONObject();
                selectedCarObject.set("selectedCars", orderParam.getCars());
                orderParam.getExtraParameter().setCustomInfo(selectedCarObject);
            } else {
                orderParam.getExtraParameter().getCustomInfo().set("selectedCars", orderParam.getCars());
            }

            if (orderParam.getExtraServices() != null && orderParam.getExtraServices().size() > 0) {
                orderParam.getExtraParameter().getCustomInfo().set("extraServices", orderParam.getExtraServices());
            }

            if (null != orderParam.getCharteredBusType()) {
                orderParam.getExtraParameter().getCustomInfo().set("charteredBusType",
                        orderParam.getCharteredBusType());
            }

            orderParam.getExtraParameter().getCustomInfo().set("subCarLevelName",
                    orderParam.getCars().size() > 1 ? "多车型" : orderParam.getCars().get(0).getCarLevelName());

            orderParam.getExtraParameter().getCustomInfo().set("isGray", true);

            // base表
            Long orderId = orderServiceImpl.makeOrder(orderParam, extraFee, null); // 调用启用事务的内部函数，不能用this调用

            orderParam.setOrderId(orderId);

            // source表
            OrderSource orderSource = new OrderSource();
            orderSource.setId(snowFlakeUtil.getNextId());
            orderSource.setUserId(orderParam.getUserId());
            orderSource.setOrderId(orderId);
            orderSource.setCoreOrderId(snowFlakeUtil.getNextId().toString());
            orderSource.setState((short) 1);
            orderSource.setEstimatePrice(maxEstimatePrice);
            orderSource.setCreateTime(new Date());
            orderSource.setUpdateTime(new Date());
            orderSource.setEstimateDistance(orderParam.getDistance());
            orderSource.setEstimateTime(orderParam.getEstimateTime());
            // 缓存order_source信息 add by kakarotto 20230331 fix_下单后查询不到订单信息
            redisUtil.set(CacheConsts.REDIS_KEY_ORDER_SOURCE + orderId, orderSource,
                    CacheConsts.TEMP_CACHE_EXPIRE_TIME.longValue());
            orderSourceMapper.insertSelective(orderSource);

            // 调用system-service更新行前申请单占用状态
            if (orderParam.getPreDepartApplyId() != null) {
                RequestUsageStateDto requestUsageStateDto = new RequestUsageStateDto();
                requestUsageStateDto.setState(1);
                requestUsageStateDto.setOrderId(orderId);
                requestUsageStateDto.setCompanyId(orderParam.getCompanyId());
                requestUsageStateDto.setUsageTime(new Date());
                requestUsageStateDto.setUsageMoney(frozenAmount);
                requestUsageStateDto.setPreDepartApplyId(orderParam.getPreDepartApplyId());
                // systemFeign.updatePreDepartApplyState(requestUsageStateDto);
                systemService.updatePreDepartApplyState(requestUsageStateDto);
            }

            // 存缓存
            addOrderCache(orderId, orderSource.getCoreOrderId(), orderParam);

            // 异步处理：保存最近订单、历史地址、保存用户手机号、通知其他服务
            orderTask.asyncPostProcessPlaceOrder(orderParam, frozenAmount, orderSource);

            orderTask.sendToReportForRental(orderParam, orderSource);

            // // 会议相关
            // if (ObjectUtil.isNotEmpty(orderParam.getMeetingId())) {
            // // 添加会议订单对应关系
            // OrderMeeting orderMeeting = new OrderMeeting(snowFlakeUtil.getNextId(),
            // orderParam.getOrderId(),
            // orderParam.getMeetingId(), new Date());
            // orderMeetingService.insertSelective(orderMeeting);
            // }

            Map<String, Object> map = new HashMap();
            map.put("orderId", String.valueOf(orderId));
            map.put("coreOrderId", orderSource.getCoreOrderId());

            return map;
        } catch (CustomException e) {
            throw e;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * 包车预估
     * 不具有通用性，走单独方法
     *
     * @param orderParam
     * @return
     * @throws Exception
     */
    public Map<String, Object> estimateForRental(CreateOrderParam orderParam) throws Exception {
        // 用sceneId从.net接口查询用户场景金额
        RequestSceneInfoDto requestSceneInfoDto = new RequestSceneInfoDto();
        BeanUtil.copyProperties(orderParam, requestSceneInfoDto, false);
        requestSceneInfoDto.setOrderTime(orderParam.getDepartTime());
        BaseResponse sceneResponse = configurationFeign.getSceneInfo(requestSceneInfoDto);
        if (sceneResponse.getCode() != 0) {
            throw new BusinessException("调用getSceneInfo接口失败:" + sceneResponse.getMessage());
        }

        RequestEstimateDto requestEstimateDto = new RequestEstimateDto();
        requestEstimateDto.setDestPoi(orderParam.getDestPoi());
        requestEstimateDto.setDestPoi(orderParam.getDestPoi());
        if (StrUtil.isEmpty(orderParam.getPassengerPhone())) {
            orderParam.setPassengerPhone("13800138000");
        }

        BeanUtil.copyProperties(orderParam, requestEstimateDto);

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        JSONObject paramObject = new JSONObject(requestEstimateDto);
        remoteCallDto.setPath("/api/v2/ordercore/estimate");
        remoteCallDto.setContent(paramObject.toString());

        dataUtil.setRemoteCallDtoHeaders(remoteCallDto);

        BaseResponse resp = remoteCallFeign.call(remoteCallDto);

        if (resp.getCode() != 0) {
            throw new BusinessException(resp.getMessage());
        }

        // 获取用户可用优惠券 add by guoxin
        List<CouponResult> availableCoupons = new ArrayList<>();
        RequestUserCouponDto requestUserCouponDto = new RequestUserCouponDto();
        requestUserCouponDto.setUserId(orderParam.getUserId());
        BaseResponse baseResponse = couponConsumeFeign.getUserCoupons(requestUserCouponDto);
        if (baseResponse.getCode() == 0) {
            JSONArray couponArray = JSONUtil.parseArray(baseResponse.getData());
            for (int i = 0; i < couponArray.size(); i++) {
                Date validFrom = null;
                Date validTo = null;
                Date orderTime = null;
                JSONObject couponObject = (JSONObject) couponArray.get(i);

                if (!couponObject.getBool("isValid")) {
                    continue;
                }

                String effectivePeriod = String.valueOf(couponObject.get("effectivePeriod"));// 判断有效期
                if (!effectivePeriod.isBlank()) {
                    String[] effectiveArray = effectivePeriod.split("至");
                    if (effectiveArray.length == 2) {
                        validFrom = DateUtil.parse(effectiveArray[0]);
                        validTo = DateUtil.parse(effectiveArray[1] + " 23:59:59");
                    }

                    if (orderParam.getDepartTime() == null) {
                        orderTime = new Date();
                    } else {
                        orderTime = orderParam.getDepartTime();
                    }

                    if (validFrom != null && validTo != null) {
                        if (DateUtil.compare(validFrom, orderTime) > 0 || DateUtil.compare(validTo, orderTime) < 0) {
                            continue;
                        }
                    }
                }

                JSONObject allowCityObject = new JSONObject(couponObject.get("allowCities"));// 判断城市
                String cityMode = null;
                List<String> cityCodes = new ArrayList<>();
                if (allowCityObject != null) {
                    if (allowCityObject.get("codeList") != null) {
                        cityMode = allowCityObject.getStr("mode");
                        JSONArray cityCodeList = JSONUtil.parseArray(allowCityObject.get("codeList"));
                        for (int cityIndex = 0; cityIndex < cityCodeList.size(); cityIndex++) {
                            cityCodes.add(String.valueOf(cityCodeList.get(cityIndex)));
                        }
                    }
                }

                if (cityCodes.size() > 0) {
                    if ("contain".equals(cityMode)) {
                        if (!cityCodes.contains(orderParam.getDepartCityCode())) {
                            continue;
                        }
                    } else if ("exclude".equals(cityMode)) {
                        if (cityCodes.contains(orderParam.getDepartCityCode())) {
                            continue;
                        }
                    }
                }

                CouponResult couponResult = new CouponResult();
                couponResult.setId(String.valueOf(couponObject.get("id")));
                if (couponObject.get("threshold") != null) {
                    couponResult.setThreshold(couponObject.getBigDecimal("threshold"));
                }
                if (couponObject.get("parValue") != null) {
                    couponResult.setParValue(couponObject.getBigDecimal("parValue"));
                }
                if (couponObject.get("source") != null) {
                    couponResult.setSource(couponObject.getShort("source"));
                }

                if (validFrom != null) {
                    couponResult.setValidFrom(validFrom);
                }

                if (validTo != null) {
                    couponResult.setValidTo(validTo);
                }

                if (couponObject.get("isValid") != null) {
                    couponResult.setValid(couponObject.getBool("isValid"));
                }

                JSONObject allowCarObject = new JSONObject(couponObject.get("allowCars"));// 车型
                if (allowCarObject != null) {
                    couponResult.setCarMode(allowCarObject.getStr("mode"));
                    couponResult.setCarLevelList(allowCarObject.getBeanList("listCarLevelCode", String.class));
                    couponResult.setCarSourceList(allowCarObject.getBeanList("listCarSourceCode", String.class));
                }

                Date currentDate = new Date();
                Long seconds = DateUtil.between(currentDate, couponResult.getValidTo(), DateUnit.SECOND);
                couponResult.setValidSeconds(seconds);
                availableCoupons.add(couponResult);
            }

            if (availableCoupons != null && availableCoupons.size() > 0) {
                availableCoupons.sort(Comparator.comparing(CouponResult::getParValue).reversed()
                        .thenComparing(CouponResult::getValidSeconds));// 排序
            }
        }

        JSONObject result = new JSONObject(resp.getData());
        String estimateId = result.getStr("estimateId");

        JSONObject priceRule = getPriceRule(orderParam.getCharteredBusType(), false);

        JSONObject dataObject = JSONUtil.parseObj(sceneResponse.getData());
        SceneInfo sceneInfo = JSONUtil.toBean(dataObject, SceneInfo.class); // 无报销信息表示全公司承担，报销比例为0表示全个人承担

        JSONArray carArray = new JSONArray();

        for (int i = 1; i < 6; i++) {
            if (null != sceneInfo.getAvailableCarLevels() &&
                    sceneInfo.getAvailableCarLevels().size() > 0 &&
                    !sceneInfo.getAvailableCarLevels().contains(i == 5 ? 10 : i))
                continue;

            JSONObject carObject = new JSONObject();
            String name = i == 1
                    ? resourceUtil.getResource(orderParam.getUserId(), orderParam.getCompanyId(), 0,
                            ResourceKeyConsts.RESOURCE_KEY_SERVICE_TYPE_ECONOMY)
                    : (i == 2
                            ? resourceUtil.getResource(orderParam.getUserId(), orderParam.getCompanyId(), 0,
                                    ResourceKeyConsts.RESOURCE_KEY_SERVICE_TYPE_COMFORT)
                            : (i == 3
                                    ? resourceUtil.getResource(orderParam.getUserId(), orderParam.getCompanyId(), 0,
                                            ResourceKeyConsts.RESOURCE_KEY_SERVICE_TYPE_BUSINESS)
                                    : (i == 4
                                            ? resourceUtil.getResource(orderParam.getUserId(),
                                                    orderParam.getCompanyId(), 0,
                                                    ResourceKeyConsts.RESOURCE_KEY_SERVICE_TYPE_LUXURY)
                                            : resourceUtil.getResource(orderParam.getUserId(),
                                                    orderParam.getCompanyId(), 0,
                                                    ResourceKeyConsts.RESOURCE_KEY_SERVICE_TYPE_BUS))));

            String seats = (i == 1 || i == 2 || i == 4) ? "适用人数1-4人" : (i == 3 ? "适用人数1-6人" : "适用人数7人以上");
            BigDecimal price = new BigDecimal(0);
            if (1 == 1) {
                price = i == 1 ? new BigDecimal(288)
                        : (i == 2 ? new BigDecimal(358)
                                : (i == 3 ? new BigDecimal(438) : (i == 4 ? new BigDecimal(598) : new BigDecimal(0))));
            } else {
                price = i == 1 ? new BigDecimal(568)
                        : (i == 2 ? new BigDecimal(668)
                                : (i == 3 ? new BigDecimal(878) : (i == 4 ? new BigDecimal(1188) : new BigDecimal(0))));
            }

            carObject.set("id", i);
            carObject.set("nameCn", name);
            carObject.set("nameEn", name);
            carObject.set("priceLab", i == 6 ? "960 ~ 1200" : price.toString());
            carObject.set("reimbursemenCn", "报销全部");
            carObject.set("reimbursemenEn", "rembursement all");

            JSONArray sourceArray = new JSONArray();
            if (i == 5) {
                for (int j = 1; j < 6; j++) {
                    String busName = (j == 1) ? "豪华巴士19座"
                            : (j == 2 ? "巴士23座" : (j == 3 ? "巴士35座" : (j == 4 ? "新能源巴士45座" : "巴士49座")));
                    BigDecimal busPrice = (j == 1)
                            ? (orderParam.getCharteredBusType() == 1 ? new BigDecimal(960) : new BigDecimal(1300))
                            : (j == 2
                                    ? (orderParam.getCharteredBusType() == 1 ? new BigDecimal(960)
                                            : new BigDecimal(1300))
                                    : (j == 3
                                            ? (orderParam.getCharteredBusType() == 1 ? new BigDecimal(1100)
                                                    : new BigDecimal(1450))
                                            : (j == 4
                                                    ? (orderParam.getCharteredBusType() == 1 ? new BigDecimal(910)
                                                            : new BigDecimal(1226))
                                                    : (orderParam.getCharteredBusType() == 1 ? new BigDecimal(1200)
                                                            : new BigDecimal(1650)))));
                    JSONObject sourceObject = new JSONObject();
                    sourceObject.set("actualEstimatePrice", busPrice);
                    sourceObject.set("dynamicCode", j);
                    sourceObject.set("carSource", busName);
                    sourceObject.set("carSourceImg",
                            "https://ipathepkernelimgs-1257314668.cos.ap-shanghai.myqcloud.com/logo_shouqi.png");
                    sourceObject.set("carSourceId", 5);
                    sourceObject.set("carLevel", 10);
                    sourceObject.set("carLevelName", busName);
                    BigDecimal couponAmount = BigDecimal.ZERO;
                    String couponId = "0";
                    JSONObject jsonObject = getCoupon(availableCoupons, "10", "5", busPrice);
                    if (jsonObject.containsKey("couponAmount")) {
                        couponAmount = jsonObject.getBigDecimal("couponAmount");
                        busPrice = busPrice.subtract(couponAmount);
                    }
                    if (jsonObject.containsKey("couponId")) {
                        couponId = jsonObject.getStr("couponId");
                    }
                    sourceObject.set("couponId", couponId);
                    sourceObject.set("couponAmount", couponAmount);
                    sourceObject.set("estimatePrice", busPrice);
                    sourceArray.add(sourceObject);
                }
            } else {
                JSONObject sourceObject = new JSONObject();
                sourceObject.set("actualEstimatePrice", price);
                sourceObject.set("dynamicCode", i);
                sourceObject.set("carSource", resourceUtil.getResource(orderParam.getUserId(),
                        orderParam.getCompanyId(), 0, ResourceKeyConsts.RESOURCE_KEY_CAR_SOURCE + "5"));
                sourceObject.set("carSourceImg",
                        "https://ipathepkernelimgs-1257314668.cos.ap-shanghai.myqcloud.com/logo_shouqi.png");
                sourceObject.set("carSourceId", 5);
                sourceObject.set("carLevel", i);
                sourceObject.set("carLevelName", name);
                BigDecimal couponAmount = BigDecimal.ZERO;
                String couponId = "0";
                JSONObject jsonObject = getCoupon(availableCoupons, String.valueOf(i), "5", price);
                if (jsonObject.containsKey("couponAmount")) {
                    couponAmount = jsonObject.getBigDecimal("couponAmount");
                    price = price.subtract(couponAmount);
                }
                if (jsonObject.containsKey("couponId")) {
                    couponId = jsonObject.getStr("couponId");
                }
                sourceObject.set("couponId", couponId);
                sourceObject.set("couponAmount", couponAmount);
                sourceObject.set("estimatePrice", price);

                sourceArray.add(sourceObject);
            }

            carObject.set("list", sourceArray);

            if (i == 5) {
                JSONObject busPriceRule = getPriceRule(orderParam.getCharteredBusType(), true);
                carObject.set("price", busPriceRule);
            }

            carObject.set("number", seats);

            JSONObject reimObject = new JSONObject();
            reimObject.set("carLevel", i);
            reimObject.set("reimModel", 1);
            reimObject.set("value", 0);

            carObject.set("reimModel", reimObject);

            // CacheEstimateResult cacheEstimateResult =
            // JSONUtil.toBean(carObject.toString(),CacheEstimateResult.class);
            redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PREFIX + estimateId, carObject.toString(),
                    CacheConsts.ORDER_ESTIMATE_CACHE_EXPIRE_TIME);

            carArray.add(carObject);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("scene", sceneInfo.getScene());
        map.put("cars", carArray);
        map.put("price", priceRule);
        map.put("estimateId", estimateId);

        return map;
    }

    private JSONObject getCoupon(List<CouponResult> availableCoupons, String carLevel, String carSourceId,
            BigDecimal estimatePrice) {
        JSONObject couponJsonObject = new JSONObject();
        if (availableCoupons != null && availableCoupons.size() > 0) {
            for (int index = 0; index < availableCoupons.size(); index++) {
                CouponResult couponResult = availableCoupons.get(index);
                if (couponResult.getCarLevelList() != null
                        && couponResult.getCarLevelList().size() > 0
                        && !couponResult.getCarLevelList().contains(carLevel)) {
                    continue;
                }

                if (couponResult.getCarSourceList() != null && couponResult.getCarSourceList().size() > 0) {
                    if ("contain".equals(couponResult.getCarMode())) {
                        if (!couponResult.getCarSourceList().contains(carSourceId)) {
                            continue;
                        }
                    } else if ("exclude".equals(couponResult.getCarMode())) {
                        if (couponResult.getCarSourceList().contains(carSourceId)) {
                            continue;
                        }
                    }
                }

                if (couponResult.getThreshold() == null ||
                        couponResult.getThreshold().compareTo(BigDecimal.ZERO) == 0 ||
                        estimatePrice.compareTo(couponResult.getThreshold()) >= 0) {
                    couponJsonObject.set("couponId", couponResult.getId());
                    couponJsonObject.set("couponAmount", couponResult.getParValue());
                    break;
                }
            }
        } else {// 如果没有，给默认值
            couponJsonObject.set("couponId", "0");
            couponJsonObject.set("couponAmount", BigDecimal.ZERO);
        }

        return couponJsonObject;
    }

    private JSONObject getPriceRule(int charteredBusType, Boolean isBus) {

        JSONObject priceObject = new JSONObject();

        priceObject.set("description", charteredBusType == 1 ? "(含50公里,4小时)" : "(含100公里,8小时)");
        priceObject.set("charteredBusType", charteredBusType);

        JSONArray priceArray = new JSONArray();
        if (isBus) {
            for (int i = 1; i < 6; i++) {
                String name = i == 1 ? "豪华巴士19座"
                        : (i == 2 ? "巴士23座" : (i == 3 ? "巴士35座" : (i == 4 ? "新能源巴士45座" : "巴士49座")));
                String price = i == 1 ? (charteredBusType == 1 ? "¥960" : "¥1300")
                        : (i == 2 ? (charteredBusType == 1 ? "¥960" : "¥1300")
                                : (i == 3 ? (charteredBusType == 1 ? "¥1100" : "¥1450")
                                        : (i == 4 ? (charteredBusType == 1 ? "¥910" : "¥1226")
                                                : (charteredBusType == 1 ? "¥1200" : "¥1650"))));
                String mileage = i == 1 ? "8.0元/公里"
                        : (i == 2 ? "8.0元/公里" : (i == 3 ? "10.0元/公里" : (i == 4 ? "13.0元/公里" : "13.0元/公里")));
                JSONObject priceEachObject = new JSONObject();
                priceEachObject.set("car", name);
                priceEachObject.set("estimate", price);
                priceEachObject.set("mileage", mileage);
                priceEachObject.set("overtime", "1.6元/分钟");
                priceArray.add(priceEachObject);
            }
        } else {
            for (int i = 1; i < 5; i++) {
                String name = i == 1 ? "普通型" : (i == 2 ? "舒适型" : (i == 3 ? "商务型" : "豪华型"));
                String price = i == 1 ? (charteredBusType == 1 ? "¥288" : "¥568")
                        : (i == 2 ? (charteredBusType == 1 ? "¥358" : "¥668")
                                : (i == 3 ? (charteredBusType == 1 ? "¥438" : "¥878")
                                        : (charteredBusType == 1 ? "¥598" : "¥1188")));
                String mileage = i == 1 ? "3.9元/公里" : (i == 2 ? "4.0元/公里" : (i == 3 ? "5.3元/公里" : "7.0元/公里"));
                String overtime = i == 1 ? "1.0元/分钟" : (i == 2 ? "1.0元/分钟" : (i == 3 ? "0.8元/分钟" : "1.8元/分钟"));
                JSONObject priceEachObject = new JSONObject();
                priceEachObject.set("car", name);
                priceEachObject.set("estimate", price);
                priceEachObject.set("mileage", mileage);
                priceEachObject.set("overtime", overtime);
                priceArray.add(priceEachObject);
            }
        }

        priceObject.set("list", priceArray);

        return priceObject;
    }

    /**
     * 处理途经点
     */
    private void handletPassingPoints(CreateOrderParam orderParam, String estimateId,
            List<CacheEstimateResult> carList) {
        // 将预估时的途经点缓存起来
        if (null != orderParam.getPassingPoints() && !orderParam.getPassingPoints().isEmpty()) {
            redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX + estimateId,
                    JSONUtil.toJsonStr(orderParam.getPassingPoints()), CacheConsts.TEN_MINUTE);

            List<EstimateCar> availableList = new ArrayList<>();

            for (CacheEstimateResult estimateResult : carList) {
                BigDecimal minEstimatePrice = BigDecimal.ZERO;
                BigDecimal maxEstimatePrice = BigDecimal.ZERO;
                try {
                    EstimateCar estimateCar = null;
                    List<EstimateCar> passingPointCarSourceList = estimateResult.getList().stream()
                            .filter(t -> t.getCarSourceId() == 5).collect(Collectors.toList());
                    if (passingPointCarSourceList.isEmpty()) {
                        estimateCar = getPassingPointCarSource(orderParam.getOrderId(), estimateResult.getId());
                        if (null == estimateCar)
                            continue;
                    }

                    if (null == estimateCar) {
                        estimateCar = passingPointCarSourceList.get(0);
                    }

                    availableList.add(estimateCar);

                    minEstimatePrice = estimateCar.getEstimatePrice();
                    for (int index = 0; index < estimateResult.getList().size(); index++) {
                        EstimateCar ec = estimateResult.getList().get(index);
                        if (Objects.equals(ec.getCarSourceId(), estimateCar.getCarSourceId()))
                            continue;
                        if (ec.getEstimatePrice().compareTo(minEstimatePrice) <= 0) {
                            BigDecimal additionalPrice = RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal(3))
                                    .setScale(2, RoundingMode.HALF_UP);
                            ec.setEstimatePrice(getDiscountPrice(minEstimatePrice.add(additionalPrice),
                                    orderParam.getCompanyId(), true));
                        }

                        if (ec.getEstimatePrice().compareTo(maxEstimatePrice) == 1)
                            maxEstimatePrice = ec.getEstimatePrice();
                    }
                    estimateResult.setPriceLab(minEstimatePrice + " ~ " + maxEstimatePrice); // 车型估价区间 如 "10 ~ 15"
                } catch (Exception ex) {
                    log.error("获取途径点出现异常【OrderService->handletPassingPoints,orderId:{}】", orderParam.getOrderId(), ex);
                }
            }

            if (availableList.isEmpty())
                throw new BusinessException("当前城市没有支持途经点的运力");

            redisUtil.set(CacheConsts.REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX + estimateId,
                    JSONUtil.toJsonStr(availableList), CacheConsts.ONE_HOUR);
        }
    }

    /**
     * 处理自定义预估显示
     */
    private void handletCustomEstiamte(CreateOrderParam orderParam, List<CacheEstimateResult> carList) {
        boolean openEstimateCustomDisplay = orderLimitService.isOpenEstimateCustomDisplay(orderParam.getCompanyId());
        if (openEstimateCustomDisplay) {
            String estimateCustomDisplay = orderLimitService.getEstimateCustomDisplay(orderParam.getCompanyId());
            JSONObject jsonObject = JSONUtil.parseObj(estimateCustomDisplay);
            if (null != jsonObject) {
                JSONObject reimbursementCnJson = jsonObject.getJSONObject("reimbursementCn");
                if (null != reimbursementCnJson) {
                    carList = carList.stream().map(o -> {
                        String reimbursementCn = o.getReimbursementCn();
                        if (StringUtils.isNotBlank(reimbursementCn)) {
                            reimbursementCn = reimbursementCn.replace("报销全部", "100%");
                            reimbursementCn = reimbursementCn.replace("报销", "");
                        }
                        List<KeyValueVo> list = new ArrayList<>();
                        KeyValueVo keyValue = new KeyValueVo();
                        keyValue.setKey(new KeyValueInfoVo(reimbursementCnJson.getStr("keyTxt"),
                                reimbursementCnJson.getStr("keyColor")));
                        keyValue.setValue(
                                new KeyValueInfoVo(reimbursementCn, reimbursementCnJson.getStr("valueColor")));
                        list.add(keyValue);
                        o.setDeductionCn(list);
                        return o;
                    }).collect(Collectors.toList());
                }
            }
        }
    }

    /**
     * 处理订单状态变化 新加 2024-10-14
     * 状态： 1 等待司机接单，2 司机接单，3 司机到达，4 开始行程，5 服务结束待支付，6 订单完成已支付，7 已取消
     * selectedCars 用户选择的车型
     */
    public void processOrderStatus(JSONObject dataObject, Long orderId, Short orderState,
            List<SelectedCar> selectedCars) throws Exception {
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        if (orderBase == null)
            return;

        if (orderState == 7 && redisUtil.hasKey(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderId))
            return;

        cipherService.addressEncryptForLocationJSONObject(orderBase.getCompanyId(), dataObject);

        Short previousOrderState = orderBase.getState();
        orderBase.setState((orderState == 5 || orderState == 6) ? 6 : orderState);// 状态为5或者6，统一更新为6

        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
        orderSource.setState((orderState == 5 || orderState == 6) ? 6 : orderState);// 状态为5或者6，统一更新为6
        orderSource.setUpdateTime(new Date());

        String oldVehicleNo = orderSource.getVehicleNo();

        JSONObject orderDetail = (JSONObject) dataObject.get("orderDetail");

        JSONObject orderInfo = (JSONObject) orderDetail.get("order");

        if (orderInfo.containsKey("isRelay") && orderInfo.getBool("isRelay", false)) {
            orderBase.setIsRelay(orderInfo.getBool("isRelay"));
        }

        RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();// 通知信息
        BigDecimal increaseAmount = null;
        String phoneForUser = null;

        // modify by guoxin
        BeanUtil.copyProperties(orderBase, requestOrderNotifyDto, true);
        BeanUtil.copyProperties(orderSource, requestOrderNotifyDto, true);

        String traceId = IdUtil.simpleUUID();

        // 为处理订单状态未连续变化的情况，几个状态一起处理
        switch (orderState) {
            case 2: // 接单
            {
                // 当司机接单时，处理免费升舱
                String customInfo = orderBase.getCustomInfo();
                if (customInfo != null) {
                    JSONObject jsonObject = new JSONObject(customInfo);
                    if (jsonObject != null && jsonObject.containsKey("upgrade")) {
                        Integer carLevel = orderInfo.getInt("carType", 0);
                        JSONObject upgradeJSONObject = new JSONObject(jsonObject.get("upgrade"));
                        if (upgradeJSONObject.getInt("upgradeCarLevel") == carLevel) {
                            requestOrderNotifyDto.setIsUpgrade(true);
                            upgradeJSONObject.set("isUpgradeSuccess", true);
                            jsonObject.set("upgrade", upgradeJSONObject);
                            orderBase.setCustomInfo(jsonObject.toString());
                            orderBase.setIsUpgrade(true);

                            // log.info("processOrderDetail ==> upgrade success={}", jsonObject.toString());

                            CacheICarOrder cacheICarOrder = (CacheICarOrder) redisUtil
                                    .get(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + orderSource.getCoreOrderId());
                            if (cacheICarOrder != null) {
                                cacheICarOrder.setIsUpgrade(true);
                                long currentExpire = redisUtil.getExpire(
                                        CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + orderSource.getCoreOrderId());
                                redisUtil.set(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + orderSource.getCoreOrderId(),
                                        cacheICarOrder, currentExpire);
                            }
                        }
                    }

                    if (orderInfo.containsKey("subCarType")) {
                        String subCarType = orderInfo.getStr("subCarType", null);
                        if (StrUtil.isNotEmpty(subCarType)) {
                            jsonObject.set("subCarLevel", subCarType);
                        }
                    }

                    orderSource.setRawCarTypeCode(orderInfo.getStr("subCarType", null));

                    if (jsonObject.containsKey("selectedCars")) {
                        List<SelectedCar> lastSelectedCars = JSONUtil.toList(
                                JSONUtil.toJsonStr(jsonObject.getJSONArray("selectedCars")), SelectedCar.class);
                        if (CollectionUtil.isNotEmpty(lastSelectedCars)) {

                            List<SelectedCar> matchedCarList = lastSelectedCars.stream()
                                    .filter(t -> NumberUtil.equals(t.getCarLevel(), orderInfo.getInt("carType"))
                                            && StrUtil.equals(t.getSubCarType(), orderInfo.getStr("subCarType"))
                                            && NumberUtil.equals(t.getCarSourceId(), orderInfo.getInt("carSource")))
                                    .collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(matchedCarList)) {
                                SelectedCar selectedCar = matchedCarList.stream()
                                        .filter(item -> StrUtil.isNotEmpty(item.getLabelCode())).findFirst()
                                        .orElse(matchedCarList.get(0));
                                orderSource.setSpCarTypeCode(selectedCar.getLabelCode());
                                orderSource.setEstimatePrice(selectedCar.getEstimatePrice());
                                orderSource.setIpathEstimatePrice(selectedCar.getEstimatePrice());
                                orderSource.setPlatformEstimatePrice(selectedCar.getEstimatePrice());

                                // 预估里程
                                if (ObjectUtil.isNotEmpty(selectedCar.getEstimateDistance())) {
                                    BigDecimal estimateDistance = selectedCar.getEstimateDistance();
                                    orderSource.setEstimateDistance(NumberUtil
                                            .mul(NumberUtil.toBigDecimal(estimateDistance), 1000.0).intValue());
                                }

                                // 预估时间
                                if (ObjectUtil.isNotEmpty(selectedCar.getEstimateTime())) {
                                    BigDecimal estimateTravelTime = selectedCar.getEstimateTime();
                                    orderSource.setEstimateTime(NumberUtil
                                            .mul(NumberUtil.toBigDecimal(estimateTravelTime), 60.0).intValue());
                                }

                            }
                        }
                    }

                    orderBase.setPassengerPhoneVirtual(orderInfo.getStr("passengerPhoneVirtual"));
                    orderBase.setUserPhoneVirtual(orderInfo.getStr("userPhoneVirtual"));
                    orderBaseMapper.updatePhoneVirtual(orderBase.getId(), orderBase.getUserPhoneVirtual(),
                            orderBase.getPassengerPhoneVirtual());
                }

                List<Long> invalidationCompanyList = regulationService.getRegulationConfig()
                        .getInvalidationCompanyList();
                boolean contains = invalidationCompanyList.contains(orderBase.getCompanyId());

                // 获取合作方管控配置
                RegulationBo regulationConfig = regulationService.getRegulationConfig();
                // 是否开启由合作方管控配置
                boolean isPartnerRegulation = regulationConfig.getPartnerRegulationCompanyList()
                        .contains(orderBase.getCompanyId());

                if (!contains) {
                    if (!isPartnerRegulation && redisUtil
                            .hasKey(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId())) {
                        redisUtil.hashDelete(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId(),
                                orderBase.getUserId().toString());
                    }
                } else {
                    if (!isPartnerRegulation
                            && regulationService.isInvalidationCompany(orderBase.getCompanyId(), orderState)
                            && redisUtil.hasKey(
                                    CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId())) {

                        redisUtil.hashDelete(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId(),
                                orderBase.getUserId().toString());

                    }
                }

                // 状态2时，判断是否开启了接单等时
                if (orderBase.getServiceType() == 1 && cacheService.isOpenTakeDistance(orderBase.getCompanyId())) {
                    try {
                        JSONObject driverPositionObj = getDriverLocation(orderSource.getCoreOrderId());
                        if (driverPositionObj != null && driverPositionObj.containsKey("currentLat")
                                && driverPositionObj.containsKey("currentLng")) {
                            String lat = driverPositionObj.getStr("currentLat");
                            String lng = driverPositionObj.getStr("currentLng");

                            JSONObject resJSONObject = getOriginalDistance(orderBase.getCompanyId(), lat,
                                    orderBase.getDepartLat(), lng, orderBase.getDepartLng());
                            if (null != resJSONObject) {
                                orderSource.setTaskDistance(resJSONObject.getInt("distance"));
                                orderSource.setTaskTime(resJSONObject.getInt("duration"));
                            }
                        }
                    } catch (Exception ex) {
                        String serverName = "accessOrderService";
                        String subject = active + "环境,获取接单距离,出现异常请查看";
                        String msg = serverName + "======>" + ex.getMessage();
                        msgService.sendMail(subject, msg, to, null);

                        log.error("处理订单回传[接单等是]出现异常【OrderService->updateOrderDetails,orderId:{}】", orderBase.getId(),
                                ex);
                    }
                }
            }
            case 3: // 司机到达
            case 4: { // 开始行程
                if (!orderDetail.isNull("driver")) {
                    JSONObject driverJsonObject = orderDetail.getJSONObject("driver");
                    BeanUtil.copyProperties(driverJsonObject, orderSource);
                    if (driverJsonObject.containsKey("phoneForUser")) {
                        phoneForUser = driverJsonObject.getStr("phoneForUser");
                    }

                    if (driverJsonObject.containsKey("vehicleNo")) {
                        String cancelVehicleno = userService.getCancelVehicleno(orderBase.getUserId());
                        boolean vehicleNo = cancelVehicleno.contains(driverJsonObject.getStr("vehicleNo"));
                        if (vehicleNo) {
                            userService.addUserMessage(orderBase.getUserId(),
                                    UserConstant.USER_ORDER_MESSAGE_OPTIMAL_DRIVER);
                        }
                    }
                }

                if (orderInfo.getInt("carSource") != null) {
                    Integer sourceId = orderInfo.getInt("carSource");
                    Integer carType = orderInfo.getInt("carType");
                    orderBase.setCarSources(CarLevel.getName(carType) + "|" + cacheService.getSourceName(sourceId, 1));
                    SceneBaseInfo scene = (SceneBaseInfo) redisUtil
                            .get(CacheConsts.REDIS_KEY_SCENE_INFO_PREFIX + orderBase.getSceneId().toString());
                    if (scene != null) {
                        orderBase.setAllowChangeDest(scene.getAllowChangeEndpoint() && orderInfo.getBool("allowMD"));
                    }
                    orderSource.setSourceCode(String.valueOf(sourceId));
                    String carSourceName = cacheService.getSourceName(sourceId, 1);
                    orderSource.setSourceNameCn(carSourceName);
                    orderSource.setCarLevel(carType);

                    // begin 针对南航的特殊处理逻辑
                    if (IPATH_SPECIAL_CUSTOMER != null && orderBase.getCompanyId().equals(IPATH_SPECIAL_CUSTOMER)) {
                        if (orderInfo.get("carTypeName") != null) {
                            if ("普通优选".equals(orderInfo.get("carTypeName"))) {
                                orderBase.setCarSources(
                                        CarLevel.COMFORTABLE.getName() + "|" + cacheService.getSourceName(sourceId, 1));
                                orderSource.setCarLevel(CarLevel.COMFORTABLE.getCode());
                            }
                        }
                    }
                    // end
                }

                if (orderInfo.get("driverPickOrderTime") != null) {
                    String driverPickOrderTimeStr = orderInfo.get("driverPickOrderTime").toString();
                    String format = driverPickOrderTimeStr.contains(String.valueOf('T')) ? "yyyy-MM-ddTHH:mm:ss"
                            : "yyyy-MM-dd HH:mm:ss";
                    orderSource.setDriverOrderTakingTime(DateUtil.parse(driverPickOrderTimeStr, format));
                }

                if (orderInfo.get("driverArrivedTime") != null) {
                    String driverArrivedTimeStr = orderInfo.get("driverArrivedTime").toString();
                    String format = driverArrivedTimeStr.contains(String.valueOf('T')) ? "yyyy-MM-ddTHH:mm:ss"
                            : "yyyy-MM-dd HH:mm:ss";
                    orderSource.setDriverArrivalTime(DateUtil.parse(driverArrivedTimeStr, format));
                }

                if (orderInfo.get("beginChargeTime") != null) {
                    String beginChargeTimeTimeStr = orderInfo.get("beginChargeTime").toString();
                    String format = beginChargeTimeTimeStr.contains(String.valueOf('T')) ? "yyyy-MM-ddTHH:mm:ss"
                            : "yyyy-MM-dd HH:mm:ss";
                    orderSource.setTravelBeginTime(DateUtil.parse(beginChargeTimeTimeStr, format));
                }

                // 判断是否经过了途径点
                if (orderInfo.containsKey("passingPoints")) {
                    JSONArray passedJsonArray = orderInfo.getJSONArray("passingPoints");
                    if (null != passedJsonArray && passedJsonArray.size() > 0) {
                        JSONObject customJsonObject = new JSONObject(orderBase.getCustomInfo());
                        if (null != customJsonObject && customJsonObject.containsKey("passingPoints")) {
                            JSONArray pathJsonArray = customJsonObject.getJSONArray("passingPoints");
                            if (pathJsonArray.size() == passedJsonArray.size()) {
                                for (int index = 0; index < passedJsonArray.size(); index++) {
                                    JSONObject passingPointJsonObject = passedJsonArray.getJSONObject(index);
                                    if (passingPointJsonObject.containsKey("actual")) {
                                        String actual = passingPointJsonObject.getStr("actual");
                                        pathJsonArray.getJSONObject(index).set("passed", StrUtil.isNotEmpty(actual));
                                        if (StrUtil.isNotEmpty(actual))
                                            pathJsonArray.getJSONObject(index).set("actual", actual);
                                    } else {
                                        pathJsonArray.getJSONObject(index).set("passed", false);
                                    }
                                }

                                orderBase.setCustomInfo(customJsonObject.toString());
                            }
                        }
                    }
                }

                // 运力托管
                if (orderInfo.containsKey("isProxy")) {
                    orderBase.setIsProxy(orderInfo.getBool("isProxy", false));
                }

                notifySettle(orderBase, orderSource, null);
                break;
            }
            // 修改只处理状态5的订单 add by Lok 20230324
            case 5: { // 服务结束待支付
                // begin 更新 orderSource 表

                if (regulationService.isInvalidationCompany(orderBase.getCompanyId(), orderState)) {
                    orderLimitService.deleteCustomInfo(orderBase.getCompanyId(), orderBase.getUserId());
                }

                // 获取合作方管控配置
                RegulationBo regulationConfig = regulationService.getRegulationConfig();
                // 是否开启由合作方管控配置
                boolean isPartnerRegulation = regulationConfig.getPartnerRegulationCompanyList()
                        .contains(orderBase.getCompanyId());

                // 美团结算中(行程结束，标记结算中)
                if (isPartnerRegulation) {
                    redisUtil.set(OrderConstant.ORDER_IS_SETTLING + orderId, 1, 15);
                }

                BigDecimal driveCarTime = BigDecimal.ZERO;// 行驶时间，当状态为5或者6时，此值一定不为空。如果此时结束时间为空，用此值和行程开始时间计算行程结束时间
                if (orderInfo.get("driveCarTime") != null) {
                    driveCarTime = new BigDecimal(orderInfo.get("driveCarTime").toString());
                    orderSource.setDuration(driveCarTime);// 行程持续时间（分钟）
                }

                if (orderInfo.get("finishTime") != null) {
                    String finishTimeStr = orderInfo.get("finishTime").toString();
                    String format = finishTimeStr.contains(String.valueOf('T')) ? "yyyy-MM-ddTHH:mm:ss"
                            : "yyyy-MM-dd HH:mm:ss";
                    orderSource.setTravelEndTime(DateUtil.parse(finishTimeStr, format));
                } else {
                    if (driveCarTime.compareTo(BigDecimal.ZERO) > 0 && orderSource.getTravelBeginTime() != null) {
                        BigDecimal secondPart = driveCarTime.subtract(new BigDecimal(driveCarTime.intValue()));
                        int second = secondPart.multiply(new BigDecimal(60)).intValue() + driveCarTime.intValue() * 60;

                        Date travelEndTime = DateUtil.offsetSecond(orderSource.getTravelBeginTime(), second);
                        orderSource.setTravelEndTime(travelEndTime);
                    }
                }

                if (orderInfo.get("actualStartLocation") != null) {
                    JSONObject actualStartLocationObject = (JSONObject) orderInfo.get("actualStartLocation");
                    orderSource.setActualPickupLat(actualStartLocationObject.getStr("lat"));
                    orderSource.setActualPickupLng(actualStartLocationObject.getStr("lng"));
                }
                if (ObjectUtil.isNotEmpty(orderInfo.getJSONObject("actualEndLocation"))) {
                    JSONObject actualEndLocationObject = (JSONObject) orderInfo.get("actualEndLocation");
                    orderSource.setActualDestLat(actualEndLocationObject.getStr("lat"));
                    orderSource.setActualDestLng(actualEndLocationObject.getStr("lng"));
                    if (StringUtils.isEmpty(actualEndLocationObject.getStr("address"))) {
                        LocationResResult location = coreTencentService.location(
                                actualEndLocationObject.getStr("lat") + "," + actualEndLocationObject.getStr("lng"));
                        if (null != location) {
                            String addressName = (location.getAddress() == null ? "" : location.getAddress());
                            // 地址名称
                            orderSource.setActualDestLocation(addressName);
                        }
                    } else {
                        orderSource.setActualDestLocation(actualEndLocationObject.getStr("address"));
                    }
                } else {
                    // 暂时只给药明康德提供
                    if (orderBase.getCompanyId() == 120090394327003921L) {
                        LastDriverPositionDto lastDriverPositionDto = trackService
                                .selectLastDriverPositionByJson(orderSource.getCoreOrderId());
                        if (null != lastDriverPositionDto && null != lastDriverPositionDto.getCurrentLat()
                                && null != lastDriverPositionDto.getCurrentLng()) {
                            orderSource.setActualDestLat(lastDriverPositionDto.getCurrentLat());
                            orderSource.setActualDestLng(lastDriverPositionDto.getCurrentLng());
                            LocationResResult location = coreTencentService
                                    .location(lastDriverPositionDto.getCurrentLat() + ","
                                            + lastDriverPositionDto.getCurrentLng());
                            if (null != location) {
                                orderSource.setActualDestLocation(
                                        location.getAddress() == null ? "" : location.getAddress());
                            }
                        }
                    }
                }

                if (orderInfo.get("payTime") != null) {
                    String payTimeStr = orderInfo.get("payTime").toString();
                    String format = payTimeStr.contains(String.valueOf('T')) ? "yyyy-MM-ddTHH:mm:ss"
                            : "yyyy-MM-dd HH:mm:ss";
                    orderSource.setPayTime(DateUtil.parse(payTimeStr, format));
                }
                if (orderInfo.get("normalDistance") != null) {
                    orderSource.setTravelDistance(new BigDecimal(orderInfo.getStr("normalDistance")));// 公里
                }

                // 实际预估金额待设置
                // {"serviceType":1,"driverArrivedTime":"2024-12-18
                // 09:38:39","subTransOrderId":1869195002071265280,"estimateDistance":1.12,"carSource":5,"cityId":0,"allowMD":true,"passengerPhoneVirtual":"17896609873","carType":1,"passengerPhone":"13654264698","orderTime":"2024-12-18
                // 09:36:29","startLocation":{"address":"广贤梁园-南门","lng":"121.5162181","name":"广贤梁园-南门","lat":"38.858056"},"carSourceName":"首汽","isRelay":false,"beginChargeTime":"2024-12-18
                // 09:41:58","platformOrderId":"B241218093631023000","hasEstRoute":true,"driverPickOrderTime":"2024-12-18
                // 09:36:31","endLocation":{"address":"大连腾讯大厦-1号门","lng":"121.523175","name":"大连腾讯大厦-1号门","lat":"38.861572"},"carTypeName":"普通","endCityId":52,"subCarType":"43","envId":"TJ109","isProxy":false,"subStatus":20502,"userPhoneVirtual":"","estimateTravelTime":5,"estimatePrice":11.5,"transOrderId":1869195002071265280,"status":4}
                // 预估里程
                if (ObjectUtil.isNotEmpty(orderInfo.get("estimateDistance"))) {
                    String estimateDistance = orderInfo.getStr("estimateDistance", null);
                    orderSource.setEstimateDistance(
                            NumberUtil.mul(NumberUtil.toBigDecimal(estimateDistance), 1000.0).intValue());
                }

                // 预估时间
                if (ObjectUtil.isNotEmpty(orderInfo.get("estimateTravelTime"))) {
                    String estimateTravelTime = orderInfo.getStr("estimateTravelTime", null);
                    orderSource.setEstimateTime(
                            NumberUtil.mul(NumberUtil.toBigDecimal(estimateTravelTime), 60.0).intValue());
                }

                JSONObject fee = (JSONObject) orderDetail.get("fee");

                JSONArray priceDetail = fee.getJSONArray("priceDetail");
                if (ObjectUtil.isEmpty(priceDetail)) {
                    // 如果 priceDetail为null 发送消息 30分钟之后再处理
                    delayProducer.processOrderDetail(dataObject);
                }

                List<FeeItem> feeItems = new ArrayList<>();
                for (Object object : priceDetail) {
                    JSONObject priceObject = (JSONObject) object;
                    FeeItem feeItem = new FeeItem();
                    feeItem.setNameCn(priceObject.getStr("name"));
                    feeItem.setType(priceObject.getStr("type"));
                    feeItem.setAmount(priceObject.getBigDecimal("amount"));
                    feeItems.add(feeItem);
                }
                JSONObject feesObject = new JSONObject();
                feesObject.set("fees", feeItems);
                orderSource.setFeeDetail(feesObject);

                BigDecimal totalAmount = new BigDecimal(fee.getStr("totalPrice"));
                orderSource.setAmount(totalAmount);

                // 加价
                if (ObjectUtil.isNotEmpty(orderBase.getCustomInfo())) {
                    JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
                    IncreaseAmountRule increaseAmountRule = customJSONObject
                            .getBean(IncreaseAmountConstant.INCREASE_AMOUNT_RULE, IncreaseAmountRule.class);
                    if (ObjectUtil.isNotEmpty(increaseAmountRule)) {
                        BigDecimal amount = increaseAmountRule.getAmount();
                        if (NumberUtil.equals(increaseAmountRule.getType(), 1)) {
                            // 将 分转为 元
                            increaseAmount = NumberUtil.div(amount, 100);
                            customJSONObject.set(IncreaseAmountConstant.INCREASE_AMOUNT, increaseAmount);
                        } else if (NumberUtil.equals(increaseAmountRule.getType(), 2)) {
                            // 获取 相乘之后的金额
                            increaseAmount = NumberUtil.round(NumberUtil.mul(totalAmount, amount), 2);
                            customJSONObject.set(IncreaseAmountConstant.INCREASE_AMOUNT, increaseAmount);
                        }

                        if (ObjectUtil.isNotEmpty(increaseAmount)) {
                            orderBase.setCustomInfo(customJSONObject.toString());
                        }
                        log.info("{} ==> orderId: {}: increaseAmount: {}", traceId, orderId, increaseAmount);
                    }
                }

                // end 更新 orderSource 表

                // 用户金额统计(扣减优惠券前)
                // 此逻辑去掉，暂无对此数据的使用 add by huzhen 2025/04/02 16:46
                // UserOrder userOrder = userOrderMapper.selectByUserId(orderBase.getUserId());
                // if (userOrder != null) {
                // BigDecimal userTotalOrderAmount = userOrder.getTotalOrderAmount() == null ?
                // BigDecimal.ZERO
                // : userOrder.getTotalOrderAmount();
                // userOrder.setTotalOrderAmount(userTotalOrderAmount.add(totalAmount));
                // userOrder.setTotalOrderCount(
                // userOrder.getTotalOrderCount() == null ? 1 : userOrder.getTotalOrderCount() +
                // 1);
                // userOrder.setUpdateTime(new Date());
                // userOrderMapper.updateByPrimaryKeySelective(userOrder);
                // }

                // 如果是个人支付企业：例如梓如 需要支付回调后状态变为6
                boolean isNeedPayNotify = orderLimitService.isOpenNeedPayNotifyCompanys(orderBase.getCompanyId());
                // H5接入用户个人收银台支付
                if (isNeedPayNotify) {
                    orderBase.setState(orderState);
                    orderBase.setPayState((short) 1);
                    orderSource.setState(orderState);
                }

                break;
            }
            case 7: { // 7 已取消
                if (StrUtil.isBlank(orderBase.getCancelReason())) {
                    orderBase.setCancelReason("平台取消");
                    // 免费升舱
                    if (orderBase.getCustomInfo() != null) {
                        JSONObject jsonObject = new JSONObject(orderBase.getCustomInfo());
                        if (jsonObject != null && jsonObject.containsKey("upgrade")) {
                            JSONObject upgradeJSONObject = new JSONObject(jsonObject.get("upgrade"));
                            boolean isUpgrade = upgradeJSONObject.getBool("isUpgradeSuccess");
                            if (previousOrderState == 1 || (previousOrderState > 1 && isUpgrade)) {
                                UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                                updateUsageCountDto.setUserId(orderBase.getUserId());
                                updateUsageCountDto.setCompanyId(orderBase.getCompanyId());
                                updateUsageCountDto.setAccountId(orderBase.getAccountId());
                                updateUsageCountDto.setUseUpgrade(false);
                                updateUsageCountDto.setOrderId(orderBase.getId());
                                updateUsageCountDto.setRecordDesc("cancelOrder");
                                updateUsageCountDto.setOrderTime(orderBase.getDepartTime());
                                systemService.updateUsageCount(updateUsageCountDto);
                            }
                        }
                    }
                }

                if (orderBase.getCancelTime() == null) {
                    orderBase.setCancelTime(new Date());
                }

                // 删除redis缓存
                redisUtil.hashDelete(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(orderBase.getUserId()),
                        String.valueOf(orderId));
                redisUtil.delete(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + orderSource.getCoreOrderId());
                redisUtil.delete(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId);
                redisUtil.delete(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId);

                if (regulationService.isInvalidationCompany(orderBase.getCompanyId(), orderState) && redisUtil
                        .hasKey(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId())) {
                    redisUtil.hashDelete(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId(),
                            orderBase.getUserId().toString());
                }
                break;
            }
            default:
                break;
        }

        if (redisUtil.hashHasKey(
                CacheConsts.REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX + String.valueOf(orderBase.getCompanyId()),
                String.valueOf(orderBase.getUserId())))

        {
            orderBase.setAbnormalStatus((short) 1);
        }

        // 改派状态
        boolean replaceOrderFlag = StrUtil.isNotEmpty(oldVehicleNo)
                && !oldVehicleNo.equals(orderSource.getVehicleNo());
        if (replaceOrderFlag) {
            // 改派订单 不收双选司机服务费
            if (orderBase.getCustomInfo() != null) {
                JSONObject customInfo = new JSONObject(orderBase.getCustomInfo());
                Object extraServices = JSONUtil.getByPath(customInfo, "extraServices");
                if (ObjectUtil.isNotEmpty(extraServices)) {
                    List<ExtraService> extraServiceList = JSONUtil.toList(JSONUtil.toJsonStr(extraServices),
                            ExtraService.class);
                    for (ExtraService extraService : extraServiceList) {
                        if ("ES0008".equals(extraService.getCode())) {
                            extraService.setIsNotifySettle(false);
                        }
                    }
                    customInfo.set("extraServices", extraServiceList);
                    orderBase.setCustomInfo(JSONUtil.toJsonStr(customInfo));
                }
            }
        }

        orderBase.setUpdateTime(new Date());
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);
        orderSourceMapper.updateByPrimaryKeySelective(orderSource);

        // 发送mq消息 订单id、订单状态、订单前一状态、是否改派
        orderTask.sendOrderStatusMQ(orderId, previousOrderState, orderState,
                StrUtil.isNotEmpty(oldVehicleNo) && !oldVehicleNo.equals(orderSource.getVehicleNo()), phoneForUser);
    }

    /**
     * 处理结算
     *
     * @throws Exception
     */
    @Async
    public void processSettleResult(JSONObject settleResultJsonObject) throws Exception {

        if (settleResultJsonObject.getInt("orderSettleResultInfo", 0) != 0)
            return;

        if (settleResultJsonObject.get("billMain") == null)
            return;

        Log iLog = logService.getLog(null, null, null, InterfaceEnum.RECEIVE_BILL_SETTLEMENT);
        iLog.setBody(JSONUtil.toJsonStr(settleResultJsonObject));

        try {
            JSONObject billMainObject = new JSONObject(settleResultJsonObject.get("billMain"));
            Long orderId = billMainObject.getLong("orderId");

            iLog.setOrderId(orderId);
            logService.saveLogAsync(iLog, null);

            OrderBase orderBase = null;
            OrderSource orderSource = null;

            Short state = 0;

            // 多获取两次
            int retryCnt = 0;
            do {
                Thread.sleep(300);
                retryCnt++;
                orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
                orderSource = orderSourceMapper.selectByOrderId(orderId);

                if (null == orderBase || null == orderSource)
                    return;

                state = orderBase.getState();
            } while ((state != 5 || state != 7) && retryCnt < 5);

            JSONObject customJsonObject = new JSONObject(orderBase.getCustomInfo());
            BigDecimal increaseAmount = customJsonObject.getBigDecimal(IncreaseAmountConstant.INCREASE_AMOUNT,
                    BigDecimal.ZERO);

            // 嘉宝加价金额
            if (ObjectUtil.isNotEmpty(increaseAmount)) {
                JSONUtil.putByPath(settleResultJsonObject, "billMain.increaseAmount", increaseAmount);
            }

            try {
                RemoteCallDto orderReportRemoteCallDto = new RemoteCallDto();
                orderReportRemoteCallDto.setPath("/api/v2/report/compensation/handleSettlement");
                orderReportRemoteCallDto.setContent(JSONUtil.toJsonStr(settleResultJsonObject));
                remoteCallFeign.call(orderReportRemoteCallDto);
            } catch (Exception ex) {
                log.error("调用core端新报表接口出现异常【OrderService.processSettleResult.report.compensation.handleSettlement】，订单id：{}", orderId, ex);
            }
            // 通知ka端报表
            delayProducer.sendDelaySettle(settleResultJsonObject);

            // 如果是个人支付企业：例如梓如 需要支付回调后状态变为6
            boolean isNeedPayNotify = orderLimitService.isOpenNeedPayNotifyCompanys(orderBase.getCompanyId());
            // 获取合作方管控配置
            RegulationBo regulationConfig = regulationService.getRegulationConfig();
            // 是否开启由合作方管控配置
            boolean isPartnerRegulation = regulationConfig.getPartnerRegulationCompanyList()
                    .contains(orderBase.getCompanyId());

            RequestOrderNotifyDto requestOrderNotifyDto = new RequestOrderNotifyDto();// 通知信息
            BeanUtil.copyProperties(orderBase, requestOrderNotifyDto, true);
            BeanUtil.copyProperties(orderSource, requestOrderNotifyDto, true);

            BigDecimal couponAmount = billMainObject.getBigDecimal("voucherMoney", BigDecimal.ZERO);
            BigDecimal totalExtraServiceFee = billMainObject.getBigDecimal("extraServiceMoney", BigDecimal.ZERO);
            BigDecimal companyBearAmount = billMainObject.getBigDecimal("companyPayMoney", BigDecimal.ZERO);
            BigDecimal userBearAmount = billMainObject.getBigDecimal("individualPayMoney", BigDecimal.ZERO);
            Short individualPayState = billMainObject.get("individualPayState", Short.class);

            if (isNeedPayNotify || isPartnerRegulation) {
                orderSource.setUserBearAmount(companyBearAmount);
                orderSource.setCompanyBearAmount(userBearAmount);
                requestOrderNotifyDto.setCompanyBearAmount(userBearAmount);
                requestOrderNotifyDto.setUserBearAmount(companyBearAmount);
                BigDecimal settleAmount = companyBearAmount.add(userBearAmount);
                orderSource.setAmount(settleAmount);
                boolean isZero = settleAmount.compareTo(BigDecimal.ZERO) == 0;
                if (isZero) {
                    if (state != 7) {
                        orderBase.setPayState((short) 2);
                    }
                } else {
                    orderBase.setPayState((short) 1);
                    // 用户未支付订单
                    if (orderLimitService.isOpenUnpaidOrderCompanys(orderBase.getCompanyId())) {
                        userService.addUnpaid(orderBase.getUserId(), orderBase.getCompanyId(), orderBase.getId());
                    }
                }
            } else {
                requestOrderNotifyDto.setCompanyBearAmount(companyBearAmount);
                requestOrderNotifyDto.setUserBearAmount(userBearAmount);
                orderSource.setCompanyBearAmount(companyBearAmount);
                orderSource.setUserBearAmount(userBearAmount);

                BigDecimal settleAmount = companyBearAmount.add(userBearAmount);
                orderSource.setAmount(settleAmount);
            }

            requestOrderNotifyDto.setCouponAmount(couponAmount);
            requestOrderNotifyDto.setExtralAmount(totalExtraServiceFee);
            requestOrderNotifyDto.setPersonalPayStatus(individualPayState);
            BigDecimal price = BigDecimal.ZERO;
            if (companyBearAmount != null) {
                price = price.add(companyBearAmount);
            }
            if (increaseAmount != null) {
                price = price.add(increaseAmount);
            }
            if (userBearAmount != null) {
                price = price.add(userBearAmount);
            }
            requestOrderNotifyDto.setPrice(price);

            if (individualPayState == 2) {
                requestOrderNotifyDto.setAllowInvoiceAmount(userBearAmount);
                requestOrderNotifyDto.setPersonalPaidAmount(userBearAmount);
                requestOrderNotifyDto.setPersonalPayableAmount(BigDecimal.ZERO);
            } else if (individualPayState == 1) {
                requestOrderNotifyDto.setAllowInvoiceAmount(BigDecimal.ZERO);
                requestOrderNotifyDto.setPersonalPaidAmount(BigDecimal.ZERO);
                requestOrderNotifyDto.setPersonalPayableAmount(userBearAmount);
            }

            BigDecimal cfgFeeAmount = BigDecimal.ZERO;
            List<JSONObject> cfgFeeAmountDetails = new ArrayList<>();
            List<JSONObject> serviceFeeList = new ArrayList<>();
            JSONArray billDetailArray = JSONUtil.parseArray(settleResultJsonObject.get("billDetails"));
            if (billDetailArray != null && billDetailArray.size() > 0) {
                for (int i = 0; i < billDetailArray.size(); i++) {
                    JSONObject detailObject = new JSONObject(billDetailArray.get(i));
                    if (detailObject.containsKey("baseFeeCode") && detailObject.get("baseFeeCode") != null) {
                        if ("base_feecode_transtotalfee_discount_upgrade"
                                .equals(detailObject.getStr("baseFeeCode"))) {
                            BigDecimal consumeMoney = detailObject.getBigDecimal("consumeMoney");// .multiply(new
                            if (null != orderSource.getFeeDetail()) {
                                JSONObject addFeeDetail = JSONUtil.parseObj(orderSource.getFeeDetail());
                                List<FeeItem> feeItemsTax = addFeeDetail.getBeanList("fees", FeeItem.class);
                                FeeItem feeItemTax = new FeeItem();
                                feeItemTax.setNameCn(detailObject.getStr("feeName"));
                                feeItemTax.setType("discount_fee");
                                feeItemTax.setAmount(consumeMoney);
                                feeItemsTax.add(feeItemTax);
                                addFeeDetail.set("fees", feeItemsTax);
                                orderSource.setFeeDetail(addFeeDetail);
                            }
                        } else if (detailObject.containsKey("billType") && detailObject.get("billType") != null
                                && "2003".equals(detailObject.get("billType").toString())) {
                            cfgFeeAmount = cfgFeeAmount.add(detailObject.getBigDecimal("consumeMoney"));
                            JSONObject feeObject = new JSONObject();
                            feeObject.set("key", detailObject.getStr("customfeesDisplayName"));
                            feeObject.set("value", detailObject.getBigDecimal("consumeMoney"));
                            cfgFeeAmountDetails.add(feeObject);
                        }

                        // 税费
                        if ("base_feecode_ipath_extra_show".equals(detailObject.getStr("baseFeeCode"))) {
                            BigDecimal taxFee = detailObject.getBigDecimal("consumeMoney");
                            // 子订单费用明细
                            if (null != orderSource.getFeeDetail()) {
                                JSONObject feesObjectTax = JSONUtil.parseObj(orderSource.getFeeDetail());
                                List<FeeItem> feeItemsTax = feesObjectTax.getBeanList("fees", FeeItem.class);
                                FeeItem feeItemTax = new FeeItem();
                                feeItemTax.setNameCn(detailObject.getStr("feeName"));
                                feeItemTax.setType(detailObject.getStr("baseFeeCode"));
                                feeItemTax.setAmount(taxFee);
                                feeItemsTax.add(feeItemTax);
                                feesObjectTax.set("fees", feeItemsTax);
                                orderSource.setFeeDetail(feesObjectTax);
                                BigDecimal pricetax = requestOrderNotifyDto.getPrice();
                                // 报表订单实际金额
                                requestOrderNotifyDto.setPrice(pricetax.add(taxFee));
                            }
                        }
                    }
                    // add by guoxin 通知报表服务服务费
                    JSONObject serviceFeeObj = new JSONObject();
                    if (detailObject.containsKey("baseFeeCode") && detailObject.get("baseFeeCode") != null) {
                        serviceFeeObj.set("feeCode", detailObject.getStr("baseFeeCode"));
                    }
                    if (detailObject.containsKey("customfeesDisplayName")
                            && detailObject.get("customfeesDisplayName") != null) {
                        serviceFeeObj.set("feeName", detailObject.getStr("customfeesDisplayName"));
                    } else {
                        serviceFeeObj.set("feeName", detailObject.getStr("feeName"));
                    }

                    if (detailObject.containsKey("consumeMoney") && detailObject.get("consumeMoney") != null) {
                        serviceFeeObj.set("consumeMoney", detailObject.getBigDecimal("consumeMoney"));
                    }
                    if (detailObject.containsKey("billType") && detailObject.get("billType") != null) {
                        serviceFeeObj.set("billType", detailObject.getBigDecimal("billType"));
                    }
                    serviceFeeList.add(serviceFeeObj);
                    // end
                }
            }

            JSONObject serviceFeeJsonObject = new JSONObject();
            serviceFeeJsonObject.set("serviceFeeDetails", serviceFeeList);
            requestOrderNotifyDto.setServiceFeeInfo(serviceFeeJsonObject.toString());
            requestOrderNotifyDto.setCfgFeeAmount(cfgFeeAmount);

            // 获取阶梯费用
            BigDecimal companyTieredfee = orderLimitService.getCompanyTieredfee(orderBase.getCompanyId(),
                    orderSource.getAmount());
            String customInfo = orderBase.getCustomInfo();
            if (customInfo != null) {
                JSONObject jo = new JSONObject(customInfo);
                if (null != companyTieredfee) {
                    jo.set("tieredfee", companyTieredfee);
                }
                if (jo != null && !jo.containsKey("cfgFeeAmountDetails") && cfgFeeAmountDetails.size() > 0) {
                    jo.set("cfgFeeAmountDetails", cfgFeeAmountDetails);
                }
                if (jo != null) {
                    requestOrderNotifyDto.setCustomInfo(jo.toString());
                    orderBase.setCustomInfo(jo.toString());
                }
            } else {
                JSONObject jo = new JSONObject();
                if (null != companyTieredfee) {
                    jo.set("tieredfee", companyTieredfee);
                }
                if (!jo.isEmpty()) {
                    requestOrderNotifyDto.setCustomInfo(jo.toString());
                    orderBase.setCustomInfo(jo.toString());
                }
            }

            if (settleResultJsonObject.containsKey("prePayFeeInfo")) {
                JSONObject prePayFeeInfoObject = new JSONObject(settleResultJsonObject.get("prePayFeeInfo"));
                BigDecimal unpaidMoney = prePayFeeInfoObject.getBigDecimal("unpaidMoney", BigDecimal.ZERO);// 待支付金额
                BigDecimal refundMoney = prePayFeeInfoObject.getBigDecimal("refundMoney", BigDecimal.ZERO);// 退款金额
                if (unpaidMoney.compareTo(BigDecimal.ZERO) > 0) {
                    requestOrderNotifyDto.setPrePayPayableAmount(unpaidMoney);
                } else if (refundMoney.compareTo(BigDecimal.ZERO) > 0) {
                    requestOrderNotifyDto.setPrePayPayableAmount(refundMoney.multiply(new BigDecimal(-1)));
                }
            }

            Boolean isAbnormal = checkAbnormalOrder(orderBase, orderSource, orderSource.getRawCarTypeCode());
            requestOrderNotifyDto.setIsAbnormal(isAbnormal);
            orderBase.setIsAbnormal(isAbnormal);

            boolean submit = true;
            boolean isComplaint = cacheService.getAutoComplaint(orderBase.getCompanyId());

            submit = orderBase.getState() != 7 || (orderBase.getState() == 7 && !isComplaint);

            // 将状态设置为null
            Short tempOrderBaseState = orderBase.getState();
            Short tempOrderSourceState = orderSource.getState();
            orderBase.setState(null);
            orderBase.setUpdateTime(new Date());
            orderSource.setState(null);
            orderSource.setUpdateTime(new Date());
            orderBaseMapper.updateByPrimaryKeySelective(orderBase);
            orderSourceMapper.updateByPrimaryKeySelective(orderSource);

            orderBase.setState(tempOrderBaseState);
            orderSource.setState(tempOrderSourceState);

            if (submit) {
                // 审批相关
                if (orderBase.getIsNeedApprove() == true) {
                    ComScene comScene = comSceneMapper.selectByPrimaryKey(orderBase.getSceneId());
                    Short approvalType = comScene.getApprovalType();
                    if (companyBearAmount.compareTo(BigDecimal.ZERO) == 1 && approvalType != (short) 1) { // 企业需支付
                        CompanyCallbackConfig callbackConfig = orderTask
                                .getCompanyCallbackConfig(orderBase.getCompanyId(), "afterApproval");
                        if (callbackConfig != null && callbackConfig.getNeedBack() != null) {// 审批外放
                            log.info("updateDatabase ==> after approval");
                            orderTask.sendAfterApprovalNotifyMsg(orderBase.getWorkFlowId(), orderBase.getId(),
                                    orderBase.getUserId(), orderBase.getSceneId(), orderBase.getSceneNameCn(),
                                    orderBase.getCompanyId(), NumberUtil.add(companyBearAmount, increaseAmount),
                                    orderBase.getUseCarReason(), false);
                        } else {

                            // 发消息通知工作流
                            WorkflowParam workflowParam = new WorkflowParam();
                            workflowParam.setBusinessType(1);
                            workflowParam.setBusinessId(orderBase.getId());
                            if (comScene.getApprovalType() == (short) 3) {
                                JSONObject comSceneCustomInfo = new JSONObject(comScene.getCustomInfo());
                                Long approvalId = comSceneCustomInfo.getLong("approvalId");
                                workflowParam.setWorkflowId(approvalId);
                            } else {
                                workflowParam.setWorkflowId(orderBase.getWorkFlowId());
                            }

                            workflowParam.setAction(1);
                            workflowParam.setSceneId(orderBase.getSceneId());
                            workflowParam.setCompanyId(orderBase.getCompanyId());
                            workflowParam.setUserId(orderBase.getUserId());
                            workflowParam.setAmount(NumberUtil.add(companyBearAmount, increaseAmount));
                            workflowParam.setResubmit(false);

                            JSONObject workflowCustomJsonObject = new JSONObject();
                            boolean isOpenIOMapping = cacheService.isOpenIO(orderBase.getCompanyId());
                            if (isOpenIOMapping) {
                                boolean unmapped = true;
                                UserBase userbase = userBaseMapper.selectByPrimaryKey(orderBase.getUserId());
                                String legalEntity = null == userbase ? "" : userbase.getBackupField3();
                                String costCenter = null == userbase ? "" : userbase.getBackupField4();
                                if (StrUtil.isNotEmpty(legalEntity) && StrUtil.isNotEmpty(costCenter)) {
                                    unmapped = !cacheService.hasIO(orderBase.getCompanyId(), costCenter, legalEntity);
                                }
                                workflowCustomJsonObject.set("rejectAfterSubmit", unmapped);
                                if (unmapped)
                                    workflowCustomJsonObject.set("reason", "请联系客服补充IO信息");

                            }

                            // 设置自动审批
                            ComScene scene = comSceneMapper.selectByPrimaryKey(orderBase.getSceneId());
                            if (ObjectUtil.isNotEmpty(scene) && ObjectUtil.isNotEmpty(scene.getItemValue())) {
                                SceneItemValue itemValue = JSONUtil.toBean(scene.getItemValue(), SceneItemValue.class);
                                if (ObjectUtil.isNotEmpty(itemValue)) {
                                    Date departTime = orderBase.getDepartTime();
                                    // 校验 场景自动审批
                                    boolean bool = checkSceneAutoApproval(departTime, itemValue,
                                            orderBase.getSceneId());
                                    if (bool) {
                                        workflowCustomJsonObject.set("sceneAutoApproval", true);
                                        workflowCustomJsonObject.set("sceneAutoApprovalAction", itemValue.getAction());
                                        workflowCustomJsonObject.set("sceneAutoActualApprover",
                                                itemValue.getActualApprover());
                                    }
                                }
                            }

                            workflowParam.setCustomInfo(workflowCustomJsonObject.toString());
                            orderTask.sendWorkflowApplyMsg(workflowParam);
                        }
                    } else if (companyBearAmount.compareTo(BigDecimal.ZERO) == 1 && approvalType == (short) 1) { // 行前审批
                        if (ObjectUtil.isNotEmpty(orderBase.getPreDepartApplyId())
                                && ObjectUtil.isNotEmpty(orderBase.getIsAbnormal()) && orderBase.getIsAbnormal()) {
                            // 订单异常,并且行前审批id不为空,验证行前审规则中是否包含发送行后审批规则

                            // 查询 行前审批异常订单需行后审批
                            List<CompanyPreApprovalSetting> prePaySetting = comSceneService
                                    .getPreApprovalSettingBySceneIdAndParaCode(orderBase.getCompanyId(),
                                            orderBase.getSceneId(), "6010");
                            log.info("订单: {} 异常, 行前审批id: {}, 行前 6010 规则: {}", orderBase.getId(),
                                    orderBase.getPreDepartApplyId(), JSONUtil.toJsonStr(prePaySetting));
                            if (ObjectUtil.isNotEmpty(prePaySetting)) {
                                CompanyPreApprovalSetting companyPreApprovalSetting = prePaySetting.stream()
                                        .sorted(Comparator.comparing(CompanyPreApprovalSetting::getId).reversed())
                                        .findFirst().get();
                                if (ObjectUtil.isNotEmpty(companyPreApprovalSetting)
                                        && BooleanUtil.toBoolean(companyPreApprovalSetting.getParaValue())) {
                                    // 发消息通知工作流
                                    WorkflowParam workflowParam = new WorkflowParam();
                                    workflowParam.setBusinessType(1);
                                    workflowParam.setBusinessId(orderBase.getId());
                                    workflowParam.setWorkflowId(orderBase.getWorkFlowId());
                                    workflowParam.setAction(1);
                                    workflowParam.setSceneId(orderBase.getSceneId());
                                    workflowParam.setCompanyId(orderBase.getCompanyId());
                                    workflowParam.setUserId(orderBase.getUserId());
                                    workflowParam.setAmount(NumberUtil.add(companyBearAmount, increaseAmount));
                                    workflowParam.setResubmit(false);
                                    workflowParam.setPreDepartApplyId(orderBase.getPreDepartApplyId());
                                    orderTask.sendWorkflowApplyMsg(workflowParam);
                                } else {
                                    log.info("规则为: {}, 无需发送", companyPreApprovalSetting);
                                }

                            } else {
                                log.info("规则为null,无需发送");
                            }
                        }
                    } else {
                        log.info("updateDatabase ==> no need approve");
                    }
                } else {
                    log.info("updateDatabase ==> company no need pay");
                }
            }

            if (isComplaint && state == 7) {
                FeedbackParam param = new FeedbackParam();
                param.setOrderId(orderBase.getId());
                param.setUserId(orderBase.getUserId());
                param.setScore((short) 1);
                param.setFeedbackTypeId((short) 7);
                param.setFeedback("订单取消费自动投诉");

                JSONArray complaintArray = new JSONArray();
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.set("label", "订单取消费自动投诉");
                jsonObject2.set("isCheck", true);
                jsonObject2.set("id", "1");
                complaintArray.add(jsonObject2);

                param.setComplaints(complaintArray);

                setOrderComplaint(param);
            }

            if (state != 7) {
                this.handleMsg(orderBase, orderSource, (short) 4, (short) 5, false, null, increaseAmount, 2,
                        requestOrderNotifyDto);
            } else {// 收到结算通知后，如果是取消有费用，再发一次通知
                Short preState = getOrderPreState(orderId, (short) 7);

                boolean cancelTypeBool = redisUtil
                        .hasKey(StrUtil.format(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderId));
                handleMsg(orderBase, orderSource, preState, (short) 7, false, null, null, cancelTypeBool ? 1 : 2, null);
                orderTask.sendOrderStatusChangedNotifyMsg(orderSource.getOrderId(), state, orderBase.getCompanyId());
            }
        } catch (Exception e) {
            log.error("结算异常: ", e);
        }
    }

    /**
     * 获取订单的前一个状态
     * 
     * @param orderId
     * @return
     */
    private Short getOrderPreState(Long orderId, short orderState) {
        Short result = (short) 1;
        try {
            if (redisUtil.hasKey(StrUtil.format(CacheConsts.REDIS_KEY_ORDER_STATUS_HISTORY, orderId))) {
                // 从缓存中获取第一个索引的订单状态,如果为空 跳过, 如果等于传入的状态取第
                Object orderStatusHistory = redisUtil
                        .listGet(StrUtil.format(CacheConsts.REDIS_KEY_ORDER_STATUS_HISTORY, orderId), 0);
                if (ObjectUtil.isNotEmpty(orderStatusHistory)) {
                    if (ObjectUtil.equals(orderState, Short.valueOf(StrUtil.toString(orderStatusHistory)))) {
                        orderStatusHistory = redisUtil
                                .listGet(StrUtil.format(CacheConsts.REDIS_KEY_ORDER_STATUS_HISTORY, orderId), 1);
                        if (ObjectUtil.isNotEmpty(orderStatusHistory)) {
                            result = Short.valueOf(StrUtil.toString(orderStatusHistory));
                        }
                    } else {
                        result = Short.valueOf(StrUtil.toString(orderStatusHistory));
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.info("获取订单的前一个状态失败: ", e);
        }

        return result;
    }

    /**
     * 订单状态变更
     *
     * @param orderId            订单id
     * @param previousOrderState 订单上一状态
     * @param orderState         订单状态
     * @param replaceOrderFlag   改派标识 true-改派 false-未改派
     * @param phoneForUser       乘客对应司机的虚拟号
     * @throws Exception
     */
    @Async
    public void notifyOrderStatus(Long orderId, Short previousOrderState, Short orderState, Boolean replaceOrderFlag,
            String phoneForUser) throws Exception {
        // 以下是对各个服务的通知
        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);

        if (null == orderBase || null == orderSource || orderState == 5)
            return;

        this.handleMsg(orderBase, orderSource, previousOrderState, orderState, replaceOrderFlag, phoneForUser,
                null, 2, null);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelOrder(Long userId, Long orderId, int type, String reason) throws Exception {

        OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);

        // 修改source表状态
        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
        if (null == orderBase || null == orderSource) {
            throw new BusinessException(OrderConstant.NOT_FOUND_ORDER);
        }

        // 需求:极速派单调度 取消
        dispatchService.cancelOrder(orderBase, orderSource);

        Short preState = orderBase.getState();

        String coreOrderId = orderSource.getCoreOrderId();
        orderSource.setState((short) 7);

        // 修改base表状态
        if (reason == null) {
            reason = "乘客取消订单";
        }

        redisUtil.set(CacheConsts.REDIS_KEY_CANCEL_ORDER_PREFIX + orderId, true, CacheConsts.TEN_SECOND);

        // 添加用户十分钟内取消的车牌号
        userService.addCancelVehicleno(orderBase.getUserId(), orderSource.getVehicleNo());

        orderBase.setState((short) 7);
        orderBase.setCancelReason(reason);
        orderBase.setCancelTime(new Date());
        orderBase.setUpdateTime(new Date());
        orderBaseMapper.updateByPrimaryKeySelective(orderBase);

        CacheOrder cacheOrder = cacheUtil.getUserCacheOrder(userId, orderId);
        // 返回orderParams
        Map<String, Object> map = new HashMap<>();
        if (cacheOrder != null) {
            map.put("orderParams", cacheOrder.getOrderParams());

            // 判断是否要缓存此下单参数，为重新下单用
            if (orderBase.getServiceType() == 1) {
                redisUtil.set(CacheConsts.REDIS_KEY_ORDER_PARAM_PREFIX + String.valueOf(orderId),
                        cacheOrder.getOrderParams(), CacheConsts.THIRTY_MINUTE_CACHE_EXPIRE_TIME);
            } else if (orderBase.getServiceType() == 2) {
                long minutes = DateUtil.between(new Date(), orderBase.getDepartTime(), DateUnit.MINUTE, false);
                if (minutes > 40L) {
                    redisUtil.set(CacheConsts.REDIS_KEY_ORDER_PARAM_PREFIX + String.valueOf(orderId),
                            cacheOrder.getOrderParams(), CacheConsts.THIRTY_MINUTE_CACHE_EXPIRE_TIME);
                }
            }
        }

        // 删除redis缓存
        redisUtil.hashDelete(CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId), String.valueOf(orderId));
        redisUtil.delete(CacheConsts.REDIS_KEY_ICAR_ORDER_PREFIX + coreOrderId);
        redisUtil.delete(CacheConsts.REDIS_KEY_ESTIMATE_PARA_PREFIX + orderId);
        redisUtil.delete(CacheConsts.REDIS_KEY_PLACEORDER_PARA_PREFIX + orderId);

        if (regulationService.isInvalidationCompany(orderBase.getCompanyId(), (short) 7)
                && redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId())) {
            redisUtil.hashDelete(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + orderBase.getCompanyId(),
                    orderBase.getUserId().toString());
        }
        BigDecimal cancelFee = null;

        if (orderBase.getServiceType() != 20) {// 包车无需向中台取消

            BaseResponse resp = coreOrderService.cancelOrder(orderBase.getCompanyId(), orderBase.getUserId(),
                    orderBase.getId(), orderSource.getCoreOrderId(), reason, preState);

            if (resp.getCode() != 0) {
                throw new BusinessException("取消订单失败，请稍候重试");
            } else if (preState >= 2) {// 取消费
                Object obj = resp.getData();
                if (ObjectUtil.isNotEmpty(obj)) {
                    JSONObject feeJsonObject = JSONUtil.parseObj(obj);
                    if (null != feeJsonObject && feeJsonObject.containsKey("fee")) {
                        cancelFee = feeJsonObject.getBigDecimal("fee", BigDecimal.ZERO);
                        if (cancelFee.compareTo(BigDecimal.ZERO) > 0) {
                            orderSource.setCompanyBearAmount(cancelFee);
                            // map.put("fee", cancelFee);
                        }
                    }
                }
            }
        }

        orderSourceMapper.updateByPrimaryKeySelective(orderSource);

        // 返回行前申请单Id
        Map<String, String> preApplyMap = new HashMap<>();
        if (orderBase.getPreDepartApplyId() != null) {
            preApplyMap.put("preDepartApplyId ", orderBase.getPreDepartApplyId().toString());
        } else {
            preApplyMap.put("preDepartApplyId", "");
        }
        map.put("preApply", preApplyMap);

        if (ObjectUtil.isEmpty(orderSource)) {
            handleMsg(orderBase, orderSource, preState, (short) 7, false, null, null, 1, null);
        }

        return map;
    }

    /**
     * 向各个服务发送消息
     *
     * @param orderBase
     * @param orderSource
     * @param previousOrderState
     * @param orderState
     * @param replaceOrderFlag   重派标识
     * @param phoneForUser       乘客对应司机虚拟号
     * @param increaseAmount     增加的金额
     * @param cancelType         取消类型 1-乘客 2-平台
     * @throws Exception
     */
    private void handleMsg(OrderBase orderBase, OrderSource orderSource, Short previousOrderState, Short orderState,
            Boolean replaceOrderFlag, String phoneForUser, BigDecimal increaseAmount, int cancelType,
            RequestOrderNotifyDto requestOrderNotifyDto)
            throws Exception {
        if (null == requestOrderNotifyDto) {
            requestOrderNotifyDto = new RequestOrderNotifyDto();
            requestOrderNotifyDto.setPreviousOrderState(previousOrderState);

            BeanUtil.copyProperties(orderBase, requestOrderNotifyDto, false);
            BeanUtil.copyProperties(orderSource, requestOrderNotifyDto, false);
        }
        requestOrderNotifyDto.setDistance(orderSource.getTravelDistance());

        // 调用system-service更新行前申请单占用状态
        if (orderBase.getPreDepartApplyId() != null
                && (5 == orderBase.getState() || 6 == orderBase.getState() || 7 == orderBase.getState())) {
            RequestUsageStateDto requestUsageStateDto = new RequestUsageStateDto();
            requestUsageStateDto.setOrderId(orderBase.getId());
            requestUsageStateDto.setCompanyId(orderBase.getCompanyId());
            requestUsageStateDto.setUsageTime(new Date());
            requestUsageStateDto.setPreDepartApplyId(orderBase.getPreDepartApplyId());
            if (orderBase.getState() == 7) {
                requestUsageStateDto.setState(0);
                requestUsageStateDto.setUsageMoney(new BigDecimal(0));
            } else if (orderBase.getState() == 5 || orderBase.getState() == 6) {
                requestUsageStateDto.setState(2);
                requestUsageStateDto.setUsageMoney(NumberUtil.add(orderSource.getCompanyBearAmount(), increaseAmount));
            }

            systemService.updatePreDepartApplyState(requestUsageStateDto);
        }

        if (orderState == 4) {// 将符合画像规则的数据缓存起来
            long startTime = System.currentTimeMillis();

            Log activityLog = logService.getLog(orderBase.getCompanyId(), orderBase.getUserId(), orderBase.getId(),
                    InterfaceEnum.LABEL_COMPLETE_HIT_RESULT);
            activityLog.setBody(
                    StrUtil.format("{\"orderId\":{},\"userId\":\"{}\"}", orderBase.getId(), orderBase.getUserId()));

            OrderSummary orderSummary = orderSummaryMapper.selectByPrimaryKey(orderBase.getId());
            if (null != orderSummary && StrUtil.isNotBlank(orderSummary.getSubTransOrderId())) {
                JSONArray templateJsonArray = getTemplateData(orderSummary, orderSource);
                if (null != templateJsonArray && templateJsonArray.size() > 0) {

                    cacheUtil.saveCache(CacheConsts.REDIS_KEY_COMPANY_CAR_TYPE_LABEL_ORDER,
                            String.valueOf(orderSummary.getSubTransOrderId()), null, null,
                            null, templateJsonArray);

                    long endTime = System.currentTimeMillis();
                    activityLog.setResMillsecond(endTime - startTime);
                    logService.saveLogAsync(activityLog,
                            StrUtil.format("{\"hit count\":{}}", templateJsonArray.size()));
                } else {
                    long endTime = System.currentTimeMillis();
                    activityLog.setResMillsecond(endTime - startTime);
                    logService.saveWarningLogAsync(activityLog, StrUtil.format("{\"hit count\":{}}", 0));
                }
            } else {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveErrorLogAsync(activityLog, "无订单信息或子订单号为空");
            }
        }

        if (orderState == 7) {
            if (cancelType == 1) {
                requestOrderNotifyDto.setCanceller("主动");
            } else {
                requestOrderNotifyDto.setCanceller("平台");
            }

            requestOrderNotifyDto.setFrozenAmount(orderSource.getEstimatePrice());
        }

        requestOrderNotifyDto.setOrderId(orderSource.getOrderId());
        requestOrderNotifyDto.setOrderSourceId(orderSource.getId());
        requestOrderNotifyDto.setSourceName(orderSource.getSourceNameCn());
        requestOrderNotifyDto.setEventType(orderState);
        if (requestOrderNotifyDto.getDriverPhone() == null) {
            if (requestOrderNotifyDto.getDriverPhoneVirtual() != null) {
                requestOrderNotifyDto.setDriverPhone(requestOrderNotifyDto.getDriverPhoneVirtual());
            }
        }
        if (orderSource.getCarLevel() != null) {
            requestOrderNotifyDto.setCarLevel(orderSource.getCarLevel());
        }
        if (orderSource.getSourceCode() != null) {
            requestOrderNotifyDto.setCarSource(Integer.valueOf(orderSource.getSourceCode()));
        }
        if (orderBase.getCustomInfo() != null) {
            JSONObject customInfo = new JSONObject(orderBase.getCustomInfo());
            if (customInfo.containsKey("prepay")) {
                JSONArray jsonArray = customInfo.getJSONArray("prepay");
                if (null != jsonArray && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject prepayObject = jsonArray.getJSONObject(i);
                        if (prepayObject.containsKey("isPrepay")) {
                            requestOrderNotifyDto.setIsPrePay(prepayObject.getBool("isPrepay"));
                        }
                    }
                }
            }

            if (customInfo.containsKey("customCarInfo")) {
                JSONArray jsonArray = customInfo.getJSONArray("customCarInfo");
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (int index = 0; index < jsonArray.size(); index++) {
                        JSONObject eachObject = new JSONObject(jsonArray.get(index));
                        if ("phone".equals(eachObject.getStr("type"))) {
                            if (eachObject.getStr("value") != null) {
                                requestOrderNotifyDto.setCustomPhone(eachObject.getStr("value"));
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (orderState == 2) {

            UserBase userBase = userBaseMapper.selectByPrimaryKey(orderBase.getUserId());
            if (userBase != null) {
                requestOrderNotifyDto.setEmergencyPhone(userBase.getEmergencyPhone());
            }
            String carSourceCode = orderSource.getSourceCode();
            int carLevel = orderSource.getCarLevel();
            if (orderBase.getIsUpgrade() != null && orderBase.getIsUpgrade()) {
                carLevel -= 1;
            }
            if (requestOrderNotifyDto.getCouponIds() == null) {
                requestOrderNotifyDto.setCouponIds(new ArrayList<>());
            }
            requestOrderNotifyDto.getCouponIds().clear();

            boolean containUpgrade = false;
            String customInfo = orderBase.getCustomInfo();
            if (customInfo != null) {
                JSONObject jsonObject = new JSONObject(customInfo);
                if (jsonObject != null) {
                    containUpgrade = jsonObject.containsKey("upgrade");
                    if (jsonObject.containsKey("selectedCars")) {
                        JSONArray selectedCarArray = JSONUtil.parseArray(jsonObject.get("selectedCars"));
                        Long usedCouponId = null;

                        for (Object object : selectedCarArray) {
                            JSONObject carObject = (JSONObject) object;
                            String selectedCarSource = carObject.getStr("carSourceId");
                            Integer selectedCarLevel = carObject.getInt("carLevel");
                            Long selectedCouponId = null;
                            if (carObject.containsKey("couponId")) {
                                selectedCouponId = carObject.getLong("couponId");
                            }

                            if (selectedCouponId != null
                                    && !requestOrderNotifyDto.getCouponIds().contains(selectedCouponId)) {
                                requestOrderNotifyDto.getCouponIds().add(selectedCouponId);
                            }

                            if (carSourceCode.equals(selectedCarSource) && carLevel == selectedCarLevel) {
                                usedCouponId = selectedCouponId;
                                requestOrderNotifyDto.setEstimateDistance(
                                        carObject.getBigDecimal("estimateDistance", BigDecimal.ZERO));
                                requestOrderNotifyDto
                                        .setEstimateTime(carObject.getBigDecimal("estimateTime", BigDecimal.ZERO));
                            }
                        }
                        if (usedCouponId != null && requestOrderNotifyDto.getCouponIds().contains(usedCouponId)) {
                            requestOrderNotifyDto.getCouponIds().remove(usedCouponId);
                        }
                    }

                    if (jsonObject.containsKey("extraServices")) {
                        if (requestOrderNotifyDto.getExtraServiceIds() == null) {
                            List<Long> extraIds = new ArrayList<>();
                            requestOrderNotifyDto.setExtraServiceIds(extraIds);
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("extraServices");
                        for (int index = 0; index < jsonArray.size(); index++) {
                            JSONObject obj = new JSONObject(jsonArray.get(index));
                            requestOrderNotifyDto.getExtraServiceIds().add(obj.getLong("id"));
                        }
                    }
                }
            }

            if (containUpgrade && (null == orderBase.getIsUpgrade() || !orderBase.getIsUpgrade())) {
                UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                updateUsageCountDto.setUserId(orderBase.getUserId());
                updateUsageCountDto.setCompanyId(orderBase.getCompanyId());
                updateUsageCountDto.setAccountId(orderBase.getAccountId());
                updateUsageCountDto.setUseUpgrade(false);
                updateUsageCountDto.setOrderId(orderBase.getId());
                updateUsageCountDto.setRecordDesc("takeOrder");
                updateUsageCountDto.setOrderTime(orderBase.getDepartTime());
                systemService.updateUsageCount(updateUsageCountDto);
            }

            // 通知extra-service
            if (requestOrderNotifyDto.getExtraServiceIds() != null
                    && requestOrderNotifyDto.getExtraServiceIds().size() > 0) {
                orderTask.notifyExtralService(requestOrderNotifyDto);
            }

            // 通知coupon-consume-service
            if (requestOrderNotifyDto.getCouponIds() != null && requestOrderNotifyDto.getCouponIds().size() > 0) {
                orderTask.notifyCouponService(requestOrderNotifyDto);
            }

            if (null == orderBase.getIsProxy() || !orderBase.getIsProxy()) {
                orderTask.notifyBillService(requestOrderNotifyDto);
            } else {
                RequestOrderNotifyDto copyNotifyDto = new RequestOrderNotifyDto();
                BeanUtil.copyProperties(requestOrderNotifyDto, copyNotifyDto);
                copyNotifyDto.setEventType((short) 7);
                orderTask.notifyBillService(copyNotifyDto);
            }
        }

        if (orderState != 6) {
            if (replaceOrderFlag) {
                requestOrderNotifyDto.setEventType((short) 9);// 改派
            }
            requestOrderNotifyDto.setPhoneForUser(phoneForUser);
            if (ObjectUtil.isNotEmpty(increaseAmount)) {
                requestOrderNotifyDto.setCompanyBearAmount(
                        NumberUtil.add(requestOrderNotifyDto.getCompanyBearAmount(), increaseAmount));
            }

            requestOrderNotifyDto.setBaseCreateTime(orderBase.getCreateTime());
            requestOrderNotifyDto.setDriverTakingTime(orderSource.getDriverOrderTakingTime());

            orderTask.notifyConfigureService(requestOrderNotifyDto);
        }

        boolean isNeedPayNotify = false, isPartnerRegulation = false;
        RegulationBo regulationConfig = null;
        if (orderState == 5) {
            // 如果是个人支付企业：例如梓如 需要支付回调后状态变为6
            isNeedPayNotify = orderLimitService.isOpenNeedPayNotifyCompanys(orderBase.getCompanyId());
            // 获取合作方管控配置
            regulationConfig = regulationService.getRegulationConfig();
            // 是否开启由合作方管控配置
            isPartnerRegulation = regulationConfig.getPartnerRegulationCompanyList()
                    .contains(orderBase.getCompanyId());
        }

        if (orderState == 4 || orderState == 5) {

            UserBase userBase = userBaseMapper.selectByPrimaryKey(orderBase.getUserId());
            if (userBase != null) {
                requestOrderNotifyDto.setEmergencyPhone(userBase.getEmergencyPhone());
            }

            String customInfo = orderBase.getCustomInfo();
            if (customInfo != null) {
                JSONObject jsonObject = new JSONObject(customInfo);
                if (jsonObject != null && jsonObject.containsKey("extraServices")) {

                    if (requestOrderNotifyDto.getExtraServiceIds() == null) {
                        List<Long> extraIds = new ArrayList<>();
                        requestOrderNotifyDto.setExtraServiceIds(extraIds);
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("extraServices");
                    for (int index = 0; index < jsonArray.size(); index++) {
                        JSONObject obj = new JSONObject(jsonArray.get(index));
                        requestOrderNotifyDto.getExtraServiceIds().add(obj.getLong("id"));
                    }

                    // 通知extra-service
                    orderTask.notifyExtralService(requestOrderNotifyDto);
                }
            }
        }

        if (orderState == 7) {
            // 优惠券
            String customerInfo = orderBase.getCustomInfo();
            if (customerInfo != null) {
                JSONObject customJSONObject = new JSONObject(customerInfo);
                if (customJSONObject != null && customJSONObject.containsKey("couponIds")) {
                    List<Long> couponIdList = customJSONObject.getBeanList("couponIds", Long.class);
                    if (couponIdList != null && couponIdList.size() > 0) {
                        requestOrderNotifyDto.setCouponIds(couponIdList);
                        // 改成同步通知
                        RequestChangeCouponState requestChangeCouponState = new RequestChangeCouponState();
                        requestChangeCouponState.setCouponIds(couponIdList);
                        requestChangeCouponState.setCouponState(1);
                        couponService.changeCouponState(couponIdList, 1, orderBase.getCompanyId(),
                                orderBase.getUserId(), orderBase.getId());
                    }
                }

                if (customJSONObject != null && customJSONObject.containsKey("upgrade")) {
                    JSONObject upgradeJSONObject = new JSONObject(customJSONObject.get("upgrade"));
                    boolean isUpgrade = upgradeJSONObject.getBool("isUpgradeSuccess");
                    if (previousOrderState == 1 || (previousOrderState > 1 && isUpgrade)) {
                        UpdateUsageCountDto updateUsageCountDto = new UpdateUsageCountDto();
                        updateUsageCountDto.setUserId(orderBase.getUserId());
                        updateUsageCountDto.setCompanyId(orderBase.getCompanyId());
                        updateUsageCountDto.setAccountId(orderBase.getAccountId());
                        updateUsageCountDto.setUseUpgrade(false);
                        updateUsageCountDto.setOrderId(orderBase.getId());
                        updateUsageCountDto.setRecordDesc("cancelOrder");
                        updateUsageCountDto.setOrderTime(orderBase.getDepartTime());
                        systemService.updateUsageCount(updateUsageCountDto);
                    }
                }

                if (cancelType == 1 && customJSONObject != null && customJSONObject.containsKey("extraServices")) {
                    JSONArray extralServiceJSONArray = customJSONObject.getJSONArray("extraServices");
                    if (extralServiceJSONArray != null && extralServiceJSONArray.size() > 0) {
                        int businessType = 0;

                        for (int i = 0; i < extralServiceJSONArray.size(); i++) {
                            JSONObject extralServiceJSONObject = extralServiceJSONArray.getJSONObject(i);
                            if (extralServiceJSONObject.containsKey("code")) {
                                if ("ES0005".equals(extralServiceJSONObject.getStr("code"))) {
                                    businessType = 1;
                                } else if ("ES0006".equals(extralServiceJSONObject.getStr("code"))) {
                                    businessType = 2;
                                }
                            }

                            if (businessType != 0) {
                                bookingService.cancelService(orderBase.getId(),
                                        orderBase.getCancelReason());
                                break;
                            }
                        }
                    }
                }
            }

            if (null == orderBase.getIsProxy() || !orderBase.getIsProxy()) {
                // 通知bill-core-service
                orderTask.notifyBillService(requestOrderNotifyDto);
            }

            // 通知extra-service
            orderTask.notifyExtralService(requestOrderNotifyDto);
        }

        if (orderState != 6) {
            if (orderState == 5) {
                if (null != orderBase.getIsAbnormal() && orderBase.getIsAbnormal()) {// 触发合规预警，通知报表
                    RequestNotifyReportForCompletionDto reportForCompletionDto = new RequestNotifyReportForCompletionDto();
                    reportForCompletionDto.setCompanyId(orderBase.getCompanyId());
                    reportForCompletionDto.setOrderId(orderBase.getId());
                    reportForCompletionDto.setCoreOrderId(orderSource.getCoreOrderId());
                    reportForCompletionDto.setCustomInfo(orderBase.getCustomInfo());
                    reportForCompletionDto.setIsAbnormal(true);
                    orderTask.notifyReportServiceCompleted(reportForCompletionDto);
                }

                if (!isNeedPayNotify) {
                    if (null == requestOrderNotifyDto.getIsSettle() || requestOrderNotifyDto.getIsSettle() == false) {
                        if (orderBase.getWorkFlowId() != null && orderBase.getIsNeedApprove() != null
                                && orderBase.getIsNeedApprove() == true && orderBase.getPreDepartApplyId() == null) {
                            orderState = 8;
                        } else {
                            orderState = 6;
                        }
                    } else {
                        return;
                    }
                }

                if (orderBase.getCustomInfo() != null) {
                    JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
                    if (customJSONObject != null && customJSONObject.containsKey("upgrade")) {
                        JSONObject upgradeJSONObject = new JSONObject(customJSONObject.get("upgrade"));
                        if (upgradeJSONObject.getBool("isUpgradeSuccess", false)) {
                            UsageRecordDto usageRecordDto = new UsageRecordDto();
                            usageRecordDto.setUserId(orderBase.getUserId());
                            usageRecordDto.setCompanyId(orderBase.getCompanyId());
                            usageRecordDto.setAccountId(orderBase.getAccountId());
                            usageRecordDto.setOrderId(orderBase.getId());
                            usageRecordDto.setUpgradLevel(orderSource.getCarLevel().toString());
                            systemService.addUsageRecord(usageRecordDto);
                        }
                    }
                }
            }
            orderTask.sendOrderStatusChangedNotifyMsg(orderSource.getOrderId(), orderState, orderBase.getCompanyId());
        }

        // 调用mst 静默支付=====================start
        if (isPartnerRegulation && orderState == 5) {
            // 静默支付(行程结束)
            boolean payState = this.getPayState(orderBase, regulationConfig);
            if (!payState) {
                payState = this.getPayState(orderBase, regulationConfig);
            }
        }

        // 调用mst 静默支付=====================end

        if (orderBase.getServiceType() == 20) { // 包车
            // 通知websocket
            JSONObject stateJsonObject = new JSONObject();
            stateJsonObject.set("coreOrderId", orderSource.getCoreOrderId());
            stateJsonObject.set("orderState", orderState);
            String key = orderBase.getId() + "_" + orderState;
            String msg = stateJsonObject.toString();
            boolean isGray = cacheUtil.isOpenGray(orderBase.getCompanyId(), orderBase.getUserId());
            orderTask.notifyWebsocketService(msg, key, orderBase.getCompanyId(), isGray);

            if (orderState == 2) {// 司机接单时通知websocket
                JSONObject takeOrderJsonObject = new JSONObject();
                takeOrderJsonObject.set("orderState", orderState);
                takeOrderJsonObject.set("coreOrderId", orderSource.getCoreOrderId());

                JSONObject sourceJsonObject = new JSONObject();
                sourceJsonObject.set("carSource", orderSource.getSourceNameCn());
                if (StrUtil.isNotEmpty(orderBase.getCarSources())) {
                    String carSources = orderBase.getCarSources();
                    String[] carSourceArray = carSources.split("|");
                    if (carSourceArray.length == 2) {
                        sourceJsonObject.set("carLevelName", carSourceArray[0]);
                    }
                }
                takeOrderJsonObject.set("source", sourceJsonObject);

                JSONObject driverJsonObject = new JSONObject();
                driverJsonObject.set("vehicleColor", orderSource.getVehicleColor());
                driverJsonObject.set("level", orderSource.getDriverLevel());
                driverJsonObject.set("avatar", orderSource.getDriverAvatar());
                driverJsonObject.set("vehicleNo", orderSource.getVehicleNo());
                driverJsonObject.set("phone", orderSource.getDriverPhone());
                driverJsonObject.set("name", orderSource.getDriverName());
                driverJsonObject.set("vehicleModel", orderSource.getVehicleModel());
                takeOrderJsonObject.set("driverInfo", driverJsonObject);

                String takeOrderKey = orderBase.getId() + "_" + orderState;
                String takeOrderMsg = takeOrderJsonObject.toString();
                orderTask.notifyWebsocketService(takeOrderMsg, takeOrderKey, orderBase.getCompanyId(), null);
            }
        }

        if (orderState == 7) {
            // 通知报表服务
            RequestReportNotifyCancelOrderDto cancelOrderDto = new RequestReportNotifyCancelOrderDto();
            cancelOrderDto.setId(orderBase.getId());
            cancelOrderDto.setStatus(orderState);
            cancelOrderDto.setCancelTime(orderBase.getCancelTime());
            cancelOrderDto.setCancelType(cancelType);
            cancelOrderDto.setCancelReason(orderBase.getCancelReason());
            orderTask.notifyReportServiceCancel(cancelOrderDto);
        }
    }

    private JSONArray getTemplateData(OrderSummary orderSummary, OrderSource orderSource) {

        if (orderSummary.getStatus() == 7)
            return null;

        List<CacheCarTypeLabelOrder> templateDataList = carTypeLabelService
                .getUserCarTypeLabelFinishNew(orderSummary.getUserId(), orderSummary.getCompanyId());
        log.info("符合用户完单画像的规则信息: userId: {}, templates: {}", orderSummary.getUserId(),
                JSONUtil.toJsonStr(templateDataList));
        log.info("orderSummary信息: {}", JSONUtil.toJsonStr(orderSummary));
        log.info("orderSource信息: {}", JSONUtil.toJsonStr(orderSource));

        if (templateDataList == null)
            return null;

        JSONArray ruleJsonArray = new JSONArray();

        if (CollectionUtil.isNotEmpty(templateDataList)) {
            templateDataList.sort(Comparator
                    .comparing((CacheCarTypeLabelOrder item) -> ObjectUtil.isNotEmpty(item.getUserId()),
                            Comparator.reverseOrder())
                    .thenComparing(CacheCarTypeLabelOrder::getCreatedTime));
            for (CacheCarTypeLabelOrder userCarTypeLabelOrder : templateDataList) {
                if (StrUtil.isBlank(orderSource.getSpCarTypeCode())
                        && StrUtil.isNotBlank(userCarTypeLabelOrder.getInItem1())) {
                    log.info("规则id: {}, 完单验证1,labelCode orderSource 为空, 规则不为空,跳过", userCarTypeLabelOrder.getId());
                    continue;
                }
                if (checkCondition(userCarTypeLabelOrder, orderSummary, orderSource)) {
                    log.info("规则id: {}, 完单验证2, 未通过, 跳过", userCarTypeLabelOrder.getId());
                    continue;
                }

                JSONObject ruleJsonObject = new JSONObject();
                ruleJsonObject.set("id", userCarTypeLabelOrder.getId());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getOrderAmount()))
                    ruleJsonObject.set("amountRange", userCarTypeLabelOrder.getOrderAmount());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getDistance()))
                    ruleJsonObject.set("distanceRange", userCarTypeLabelOrder.getDistance());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getDuration()))
                    ruleJsonObject.set("durationRange", userCarTypeLabelOrder.getDuration());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getOutItem1()))
                    ruleJsonObject.set("adjustAmount", userCarTypeLabelOrder.getOutItem1());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getOutItem2()))
                    ruleJsonObject.set("adjustDistance", userCarTypeLabelOrder.getOutItem2());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getOutItem3()))
                    ruleJsonObject.set("adjustDuration", userCarTypeLabelOrder.getOutItem3());
                if (StrUtil.isNotBlank(userCarTypeLabelOrder.getOutItem4()))
                    ruleJsonObject.set("adjustFeeDetail", JSONUtil.parseArray(userCarTypeLabelOrder.getOutItem4()));

                ruleJsonArray.add(ruleJsonObject);
            }
        }

        return ruleJsonArray;
    }

    private boolean checkCondition(CacheCarTypeLabelOrder userCarTypeLabelOrder, OrderSummary orderSummary,
            OrderSource orderSource) {
        if (StrUtil.isNotBlank(userCarTypeLabelOrder.getCarSource())
                && !userCarTypeLabelOrder.getCarSource().equals(orderSummary.getActualCarSourceCode().toString())) {
            log.info("carSorce: 规则: {} , orderSummary: {}, orderSource: {}", userCarTypeLabelOrder.getCarSource(),
                    orderSummary.getActualCarSourceCode(), orderSource.getSourceCode());
            return true;
        }

        if (null != userCarTypeLabelOrder.getCarLevel()
                && userCarTypeLabelOrder.getCarLevel().shortValue() != orderSummary.getActualCarLevel()) {
            log.info("carLevel: 规则: {} , orderSummary: {}, orderSource: {}", userCarTypeLabelOrder.getCarLevel(),
                    orderSummary.getActualCarLevel(), orderSource.getCarLevel());
            return true;
        }

        if (StrUtil.isNotBlank(userCarTypeLabelOrder.getCityCode())
                && !userCarTypeLabelOrder.getCityCode().equals(orderSummary.getCityCode())) {
            log.info("cityCode: 规则: {} , orderSummary: {}", userCarTypeLabelOrder.getCityCode(),
                    orderSummary.getCityCode());
            return true;
        }

        if (StrUtil.isNotBlank(userCarTypeLabelOrder.getInItem1())) {
            log.info("labelCode: 规则: {} , orderSource: {}", userCarTypeLabelOrder.getInItem1(),
                    orderSource.getSpCarTypeCode());

            if (StrUtil.isBlank(orderSource.getSpCarTypeCode())) {
                return true;
            }
            if (!userCarTypeLabelOrder.getInItem1().equals(orderSource.getSpCarTypeCode())) {
                return true;
            }
        }

        return false;

    }

    /**
     * 通知结算
     *
     * @param orderBase   订单id
     * @param orderSource 订单状态
     * @throws Exception
     */
    @Async
    public void notifySettle(OrderBase orderBase, OrderSource orderSource, JSONObject additionalJsonObject) {
        Log activityLog = logService.getLog(orderBase.getCompanyId(), orderBase.getUserId(), orderBase.getId(),
                InterfaceEnum.CORE_BILL_SETTLE_ORDERSETTLE);
        long startTime = System.currentTimeMillis();
        try {
            SettleReq settleReq = constructSettleReq(orderBase, orderSource, additionalJsonObject);

            // 调用结算
            RemoteCallDto settleOrderRemoteCallDto = new RemoteCallDto();
            settleOrderRemoteCallDto.setPath("/api/v2/bill/settle/orderSettle");
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(settleReq);
            settleOrderRemoteCallDto.setContent(msgStr);
            activityLog.setBody(msgStr);
            BaseResponse response = remoteCallFeign.call(settleOrderRemoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (response.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
            } else {
                logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("订单状态变更通知结算接口出现异常【OrderService.notifySettle.bill.settle.orderSettle】，订单id：{}", orderBase.getId(), ex);
        }
    }

    /**
     * 取消延时的结算订单(预约管家接站有取消费的订单，如果不调接口等待10秒结算，如果10秒内调接口不结算)
     *
     * @param orderBase 订单id
     * @throws Exception
     */
    public void notifySettleCance(OrderBase orderBase) {
        Log activityLog = logService.getLog(orderBase.getCompanyId(), orderBase.getUserId(), orderBase.getId(),
                InterfaceEnum.ORDER_NOTIFY_BILL_DELAY_CANCEL);
        long startTime = System.currentTimeMillis();
        try {
            SettleReq settleReq = new SettleReq();
            settleReq.setOrderId(orderBase.getId());

            // 调用结算
            RemoteCallDto settleOrderRemoteCallDto = new RemoteCallDto();
            settleOrderRemoteCallDto.setPath("/api/v2/bill/settle/orderSettle/delaySettle/cancel");
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(settleReq);
            settleOrderRemoteCallDto.setContent(msgStr);
            BaseResponse response = remoteCallFeign.call(settleOrderRemoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (response.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
            } else {
                logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("取消延时的结算遇到异常【OrderService.notifySettle.bill.settle.notifySettleCance.delaySettle.cancel】，订单id：{}", orderBase.getId(), ex);
        }
    }

    public void notifyBookingFee(Long companyId, Long originOrderId, Long userId, Long orderId, Long coreOrderId,
            BigDecimal cancelFee) {
        Log activityLog = logService.getLog(companyId, userId, originOrderId, InterfaceEnum.NOTIFY_BOOKING_CANCEL_FEE);
        long startTime = System.currentTimeMillis();
        try {
            JSONObject cancelFeeObject = new JSONObject();
            cancelFeeObject.set("orderID", orderId);
            cancelFeeObject.set("coreOrderID", coreOrderId);
            cancelFeeObject.set("cancleFee", cancelFee);
            activityLog.setBody(JSONUtil.toJsonStr(cancelFeeObject));

            RemoteCallDto settleOrderRemoteCallDto = new RemoteCallDto();
            settleOrderRemoteCallDto.setPath("/api/v2/booking/ChangeCancleFee");
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(cancelFeeObject);
            settleOrderRemoteCallDto.setContent(msgStr);
            BaseResponse response = remoteCallFeign.call(settleOrderRemoteCallDto);
            long endTime = System.currentTimeMillis();
            if (response.getCode() == 0) {
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
            } else {
                activityLog.setResMillsecond(endTime - startTime);
                activityLog.setErrorMsg(response.getMessage());
                logService.saveErrorLogAsync(activityLog, JSONUtil.toJsonStr(response));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("通知预约管家出现异常【OrderService.notifySettle.bill.settle.notifySettleCance.delaySettle.cancel】，订单id：{}", orderId, ex);
        }
    }

    private SettleReq constructSettleReq(OrderBase orderBase, OrderSource orderSource,
            JSONObject additionalJsonObject) {
        if (null == orderBase)
            return null;

        SettleReq settleReq = new SettleReq();
        settleReq.setOrderId(orderBase.getId());
        settleReq.setEventType(orderBase.getState() == 6 ? 5 : orderBase.getState());

        if (orderBase.getState() == 5 || orderBase.getState() == 6) {
            settleReq.setGrayFlag(null);
            settleReq.setAppId(null);
            if (null != additionalJsonObject) {

                if (additionalJsonObject.containsKey("transTotalFee")) {
                    settleReq.setTransTotalFee(additionalJsonObject.getBigDecimal("transTotalFee", BigDecimal.ZERO));

                }

                if (additionalJsonObject.containsKey("transFeeDetail")) {
                    settleReq.setTransFeeDetail(additionalJsonObject.getJSONArray("transFeeDetail"));

                }
            }

            dispatchService.notifyOrderStatus5SettleNew(settleReq, orderBase, orderSource);
        } else {
            CacheCompanyInfo companyInfo = cacheService.getCompanyInfo(orderBase.getCompanyId());
            if (null != companyInfo) {
                settleReq.setAppId(companyInfo.getAppId());
            }

            settleReq.setGrayFlag(receiveIsGray);
            settleReq.setCompanyId(orderBase.getCompanyId());
            settleReq.setUserId(orderBase.getUserId());
            settleReq.setCityCode(orderBase.getDepartCityCode());
            settleReq.setServiceType(orderBase.getServiceType());
            settleReq.setDepartTime(DateUtil.format(orderBase.getDepartTime(), "yyyy-MM-dd HH:mm:ss"));
            settleReq.setAccountId(orderBase.getAccountId());
            settleReq.setSceneId(orderBase.getSceneId());
            settleReq.setScenePublishId(orderBase.getScenePublishId());
            settleReq.setCarLevel(orderSource.getCarLevel());
            settleReq.setCarSource(orderSource.getSourceCode());
            settleReq.setIsPrepay(orderBase.getIsPrepay());
            settleReq.setPrePayPayableAmount(null);

            /** 处理报销策略 */
            boolean needIndividualPay = orderCheckService.isNeedIndividualPay(orderBase, orderSource);
            settleReq.setPersonalPay(needIndividualPay);

            if (!needIndividualPay) {
                List<ReimModel> reimModes = null;
                if ((redisUtil.get(CacheConsts.REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX
                        + orderBase.getScenePublishId())) != null) {
                    reimModes = JSONUtil
                            .toList(JSONUtil.parseArray(redisUtil.get(CacheConsts.REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX
                                    + orderBase.getScenePublishId())), ReimModel.class);
                } else {
                    RequestSceneInfoDto requestSceneInfoDto = new RequestSceneInfoDto();
                    requestSceneInfoDto.setUserId(orderBase.getUserId());
                    requestSceneInfoDto.setCompanyId(orderBase.getCompanyId());
                    requestSceneInfoDto.setSceneId(orderBase.getSceneId());
                    requestSceneInfoDto.setScenePublishId(orderBase.getScenePublishId());
                    requestSceneInfoDto.setOrderTime(orderBase.getDepartTime());
                    BaseResponse sceneResponse = configurationFeign.getSceneInfo(requestSceneInfoDto);
                    if (sceneResponse.getCode() != 0) {
                        throw new BusinessException("调用getSceneInfo接口失败:" + sceneResponse.getMessage());
                    }

                    JSONObject sceneDataObject = JSONUtil.parseObj(sceneResponse.getData());
                    SceneInfo sceneInfo = JSONUtil.toBean(sceneDataObject, SceneInfo.class);
                    redisUtil.set(
                            CacheConsts.REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX + orderBase.getScenePublishId().toString(),
                            sceneInfo.getReimModel(), CacheConsts.ORDER_CACHE_EXPIRE_TIME);
                    reimModes = sceneInfo.getReimModel();
                }

                for (ReimModel reimModel : reimModes) {
                    if (reimModel.getCarLevel() == settleReq.getCarLevel()) {
                        settleReq.setSceneReim(reimModel);
                        break;
                    }
                }
            }

            /** 自定义信息 */
            if (StrUtil.isNotBlank(orderBase.getCustomInfo())) {
                JSONObject customJsonObject = JSONUtil.parseObj(orderBase.getCustomInfo());

                /** 增值服务 */
                JSONArray extralServiceJsonArray = customJsonObject.getJSONArray("extraServices");
                if (null != extralServiceJsonArray && extralServiceJsonArray.size() > 0) {
                    if (null == settleReq.getExtrFeeList()) {
                        settleReq.setExtrFeeList(new ArrayList<>());
                    }

                    ServiceFeeModel serviceFeeModel = new ServiceFeeModel();

                    for (int i = 0; i < extralServiceJsonArray.size(); i++) {
                        JSONObject extralServiceJsonObject = extralServiceJsonArray.getJSONObject(i);
                        ExtraService extraService = JSONUtil.toBean(extralServiceJsonObject, ExtraService.class);
                        // 判断这个服务是否需要结算
                        boolean check = checkExtralServiceHasNotifySettle(customJsonObject, extraService);

                        if (check) {
                            settleReq.getExtrFeeList().add(extralServiceJsonObject.getLong("id"));
                        }
                    }

                    serviceFeeModel.setExtrFee(settleReq.getExtrFeeList());

                    settleReq.setServiceFees(serviceFeeModel);
                }

                /** 优惠券 */
                if (customJsonObject.containsKey("couponIds")) {
                    List<Long> couponIds = customJsonObject.getBeanList("couponIds", Long.class);
                    if (couponIds.size() > 0) {
                        if (couponIds.size() == 1 && couponIds.get(0).equals(0L)) {
                            couponIds = null;
                        }
                        if (null != couponIds) {
                            settleReq.setCouponIds(couponIds);
                        }
                    }
                }

                /** 免费升舱 */
                if (customJsonObject.containsKey("upgrade")) {
                    JSONObject upgradeJSONObject = new JSONObject(customJsonObject.get("upgrade"));
                    Boolean isUpgrade = upgradeJSONObject.getBool("isUpgradeSuccess", false);
                    if (isUpgrade) {
                        UpgradeModel upgradeModel = new UpgradeModel();
                        Integer carLevel = orderSource.getCarLevel();
                        Integer upgradeCarLevel = upgradeJSONObject.getInt("upgradeCarLevel");
                        if (upgradeCarLevel > carLevel) {
                            // 有可能重派单 导致降仓 给结算推升舱失败
                            upgradeModel.setUpgradeCar(false);
                        } else {
                            upgradeModel.setUpgradeCar(true);
                        }
                        upgradeModel.setCompanySettingMaxMoney(
                                upgradeJSONObject.getBigDecimal("maxDiscountAmount"));

                        JSONArray upgradeCarArray = JSONUtil.parseArray(upgradeJSONObject.get("upgradeCars"));
                        if (upgradeCarArray != null) {
                            for (int i = 0; i < upgradeCarArray.size(); i++) {
                                JSONObject upgradeCarObject = new JSONObject(upgradeCarArray.get(i));
                                if (upgradeCarObject.getStr("carSourceId")
                                        .equals(orderSource.getSourceCode())) {
                                    upgradeModel
                                            .setEstimatePrice(upgradeCarObject.getBigDecimal("estimatePrice"));
                                    break;
                                }
                            }
                        }

                        settleReq.setUpgradeCar(upgradeModel);

                        settleReq.setCarLevel(settleReq.getCarLevel() - 1);
                    }
                }

                dispatchService.notifyOrderStatus5SettleNew(settleReq, orderBase, orderSource);
            }
        }
        return settleReq;
    }

    /**
     * 校验增值服务是否需要通知结算
     * 
     * @param customJsonObject 订单自定义信息
     * @param extraService     增值服务
     * @return
     */
    private boolean checkExtralServiceHasNotifySettle(JSONObject customJsonObject, ExtraService extraService) {
        boolean result = true;
        //
        if (ObjectUtil.isNotEmpty(extraService.getIsNotifySettle())
                && BooleanUtil.isFalse(extraService.getIsNotifySettle())) {
            return false;
        }

        // 双选司机需要验证 下单选择的车型 如果只有一个 不需要支付
        if (StringUtils.equals(extraService.getCode(), "ES0008")) {
            Object byPath = JSONUtil.getByPath(customJsonObject, "selectedCars");
            if (ObjectUtil.isNotEmpty(byPath)) {
                JSONArray jsonArray = JSONUtil.parseArray(byPath);
                if (jsonArray.size() == 1) {
                    return false;
                }
            }
        }

        return result;
    }

    /**
     * 获取订单信息
     *
     * @param orderId
     * @throws Exception
     */
    @Override
    public OrderForReport getOrderForReportById(Long orderId) {
        OrderForReport orderForReport = null;
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_FOR_REPORT_PREFIX + String.valueOf(orderId))) {
            orderForReport = JSONUtil.toBean(
                    redisUtil.get(CacheConsts.REDIS_KEY_ORDER_FOR_REPORT_PREFIX + String.valueOf(orderId)).toString(),
                    OrderForReport.class);
        } else {
            orderForReport = orderBaseMapper.selectOrderForReportById(orderId);
        }

        return orderForReport;
    }

    /**
     * 设置是否选中联动
     *
     * @param carList
     * @param labels
     * @param sceneInfo
     */
    private void setEstimateCarsIsCheck(List<CacheEstimateResult> carList, List<CacheCarTypeLabelEstimateResult> labels,
            SceneInfo sceneInfo) {
        log.info("开始设置预估选中联动");
        try {
            if (ObjectUtil.isNotEmpty(sceneInfo)) {
                // 1. 有场景设置
                List<Integer> defaultLevel = null;
                List<Integer> requiredLevel = null;
                if (ObjectUtil.isNotEmpty(sceneInfo.getScene())) {
                    if (ObjectUtil.isNotEmpty(sceneInfo.getScene().getDefaultLevel())) {
                        defaultLevel = sceneInfo.getScene().getDefaultLevel();
                        requiredLevel = sceneInfo.getScene().getRequiredLevel();
                    }
                }

                if (ObjectUtil.isNotEmpty(defaultLevel)) {
                    // 模式1: 有场景设置,且设置了默认级别
                    processCarsSceneSetting(carList, labels, defaultLevel, requiredLevel);
                } else {
                    // 模式2: 没有场景设置
                    processCarsCarTypeLabelSetting(carList, labels, requiredLevel);
                }

            } else {
                // 模式2: 没有场景设置
                processCarsCarTypeLabelSetting(carList, labels, null);
            }

        } catch (Exception e) {
            log.error("设置预估选中联动异常: ", e);
        }

    }

    /**
     * 处理标签模式下的默认选中车型
     *
     * @param carList
     * @param labels
     * @param requiredLevel
     */
    private void processCarsCarTypeLabelSetting(List<CacheEstimateResult> carList,
            List<CacheCarTypeLabelEstimateResult> labels, List<Integer> requiredLevel) {
        log.info("标签模式...");
        List<String> dynamicCodes = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(labels)) {
            log.info("处理labels选中数据: ");
            for (CacheCarTypeLabelEstimateResult label : labels) {
                Set<CarTypeLabelEstimateVo> data = label.getData();
                if (ObjectUtil.isNotEmpty(data)) {
                    for (CarTypeLabelEstimateVo datum : data) {
                        for (EstimateCar estimateCar : datum.getList()) {
                            if (datum.isCheckAll()) {
                                log.info("标签: {}, labelCode: {} 为全选中, dynamicCode: {}, [选中状态] 设置为选中",
                                        estimateCar.getLabelName(), estimateCar.getLabelCode(),
                                        estimateCar.getDynamicCode());
                                dynamicCodes.add(estimateCar.getDynamicCode());
                                estimateCar.setChecked(true);
                            }
                            //
                            if (CollectionUtil.contains(requiredLevel, estimateCar.getCarLevel())) {
                                log.info("标签: {}, labelCode: {} , dynamicCode: {}, [不可取消] 设置为选中",
                                        estimateCar.getLabelName(), estimateCar.getLabelCode(),
                                        estimateCar.getDynamicCode());
                                estimateCar.setCancel(true);
                            }
                        }
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(carList) && ObjectUtil.isNotEmpty(dynamicCodes)) {
            log.info("处理cars选中数据: ");
            for (CacheEstimateResult cacheEstimateResult : carList) {
                for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                    if (CollectionUtil.contains(dynamicCodes, estimateCar.getDynamicCode())) {
                        log.info("dynamicCode: {}, 符合标签设置的默认选中车型等级,[选中状态] 设置为选中", estimateCar.getDynamicCode());
                        estimateCar.setChecked(true);
                    }
                    if (CollectionUtil.contains(requiredLevel, estimateCar.getCarLevel())) {
                        log.info("dynamicCode: {}, 符合场景设置的不可取消车型等级, [不可取消] 设置为不可取消", estimateCar.getDynamicCode());
                        estimateCar.setCancel(true);
                    }
                }

                long checkedCount = cacheEstimateResult.getList().stream()
                        .filter(item -> BooleanUtil.isTrue(item.getChecked())).count();
                if (ObjectUtil.equals(checkedCount,
                        Long.parseLong(String.valueOf(cacheEstimateResult.getList().size())))) {
                    cacheEstimateResult.setCheckAll(true);
                } else {
                    cacheEstimateResult.setCheckAll(false);
                }

            }
        }
    }

    /**
     * 处理场景模式下的默认选中车型
     *
     * @param carList
     * @param labels
     * @param defaultLevel
     * @param requiredLevel
     */
    private void processCarsSceneSetting(List<CacheEstimateResult> carList,
            List<CacheCarTypeLabelEstimateResult> labels, List<Integer> defaultLevel, List<Integer> requiredLevel) {
        log.info("场景模式...");
        if (ObjectUtil.isNotEmpty(carList)) {
            for (CacheEstimateResult cacheEstimateResult : carList) {
                if (ObjectUtil.isNotEmpty(cacheEstimateResult.getList())) {
                    for (EstimateCar estimateCar : cacheEstimateResult.getList()) {
                        if (CollectionUtil.contains(defaultLevel, estimateCar.getCarLevel())) {
                            log.info("dynamicCode: {}, 符合场景设置的默认选中车型等级,[选中状态] 设置为选中", estimateCar.getDynamicCode());
                            estimateCar.setChecked(true);
                        } else {
                            estimateCar.setChecked(null);
                        }
                        if (CollectionUtil.contains(requiredLevel, estimateCar.getCarLevel())) {
                            log.info("dynamicCode: {}, 符合场景设置的不可取消车型等级,[不可取消] 设置为选中", estimateCar.getDynamicCode());
                            estimateCar.setCancel(true);
                        }
                    }

                    long checkedCount = cacheEstimateResult.getList().stream()
                            .filter(item -> BooleanUtil.isTrue(item.getChecked())).count();
                    if (ObjectUtil.equals(checkedCount,
                            Long.parseLong(String.valueOf(cacheEstimateResult.getList().size())))) {
                        cacheEstimateResult.setCheckAll(true);
                    } else {
                        cacheEstimateResult.setCheckAll(false);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(labels)) {
            for (CacheCarTypeLabelEstimateResult label : labels) {
                Set<CarTypeLabelEstimateVo> data = label.getData();
                if (ObjectUtil.isNotEmpty(data)) {
                    for (CarTypeLabelEstimateVo datum : data) {
                        if (ObjectUtil.isNotEmpty(datum.getList())) {
                            for (EstimateCar estimateCar : datum.getList()) {
                                if (CollectionUtil.contains(defaultLevel, estimateCar.getCarLevel())) {
                                    log.info("dynamicCode: {}, 符合场景设置的默认选中车型等级,[选中状态] 设置为选中",
                                            estimateCar.getDynamicCode());
                                    estimateCar.setChecked(true);
                                } else {
                                    estimateCar.setChecked(false);
                                }
                                if (CollectionUtil.contains(requiredLevel, estimateCar.getCarLevel())) {
                                    log.info("dynamicCode: {}, 符合场景设置的不可取消车型等级,[不可取消] 设置为选中",
                                            estimateCar.getDynamicCode());
                                    estimateCar.setCancel(true);
                                }
                            }
                        }
                        long checkedCount = datum.getList().stream()
                                .filter(item -> BooleanUtil.isTrue(item.getChecked())).count();
                        if (ObjectUtil.equals(checkedCount, Long.parseLong(String.valueOf(datum.getList().size())))) {
                            datum.setCheckAll(true);
                        } else {
                            datum.setCheckAll(false);
                        }

                        if (checkedCount > 0) {
                            BigDecimal max = datum.getList().stream()
                                    .filter(item -> BooleanUtil.isTrue(item.getChecked()))
                                    .map(EstimateCar::getEstimatePrice).distinct().max(BigDecimal::compareTo)
                                    .orElse(null);
                            BigDecimal min = datum.getList().stream()
                                    .filter(item -> BooleanUtil.isTrue(item.getChecked()))
                                    .map(EstimateCar::getEstimatePrice).distinct().min(BigDecimal::compareTo)
                                    .orElse(null);
                            datum.setPriceLab(NumberUtil.equals(max, min) ? StrUtil.toString(max)
                                    : StrUtil.format("{}-{}", min, max));
                        } else {
                            BigDecimal max = datum.getList().stream().map(EstimateCar::getEstimatePrice).distinct()
                                    .max(BigDecimal::compareTo).orElse(null);
                            BigDecimal min = datum.getList().stream().map(EstimateCar::getEstimatePrice).distinct()
                                    .min(BigDecimal::compareTo).orElse(null);
                            datum.setPriceLab(NumberUtil.equals(max, min) ? StrUtil.toString(max)
                                    : StrUtil.format("{}-{}", min, max));
                        }
                    }
                }
            }
        }
    }
}