package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.map.service.CoreMapService;
import com.ipath.orderflowservice.order.bean.*;
import com.ipath.orderflowservice.order.bean.bo.CompanyOrderLimit;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.OrderLimitConstant;
import com.ipath.orderflowservice.order.bean.param.Address;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.KeyValue;
import com.ipath.orderflowservice.order.bean.vo.AirportCrossCityMapping;
import com.ipath.orderflowservice.order.bean.vo.AirportPoint;
import com.ipath.orderflowservice.order.bean.vo.OrderLimitConfigValueCompanyVo;
import com.ipath.orderflowservice.order.dao.CityMappingMapper;
import com.ipath.orderflowservice.order.dao.OrderBaseMapper;
import com.ipath.orderflowservice.order.dao.OrderLimitConfigMapper;
import com.ipath.orderflowservice.order.dao.bean.CityMapping;
import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitMappingVo;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitVo;
import com.ipath.orderflowservice.order.enums.CompanyLimitTypexEnum;
import com.ipath.orderflowservice.order.enums.OrderLimitTypexEnum;
import com.ipath.orderflowservice.order.service.OrderLimitService;
import com.ipath.orderflowservice.order.util.CacheUtil;
import com.ipath.orderflowservice.order.util.CoordinateUtil;
import com.jd.open.api.sdk.internal.JSON.JSON;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderLimitServiceImpl implements OrderLimitService {

    // 公司配置开关
    /**
     * 下单限制
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_COMPANYS = "order_limit_companys";
    /**
     * h5每次只允许下一单限制
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONE_H5 = "order_limit_one_h5_companys";
    /**
     * 机场映射
     **/
    private static final String REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANYS = "airport_city_mapping_companys";
    /**
     * 免费升舱
     **/
    private static final String REDIS_KEY_PLACEORDER_UPGRADE_CAR_LEVL_COMPANYS = "placeorder_upgrade_car_levl_companys";
    /**
     * 大额预付
     **/
    private static final String REDIS_KEY_PLACEORDER_BIG_AMOUNT_PRE_PAY_COMPANYS = "placeorder_big_amount_pre_pay_companys";
    /**
     * 配置费用
     **/
    private static final String REDIS_KEY_STTING_FEE_COMPANYS = "setting_fee_companys";
    /**
     * 城市转换
     **/
    private static final String REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANYS = "placeorder_city_convert_companys";
    /**
     * 下单通知
     **/
    private static final String REDIS_KEY_PLACEORDER_NOTIFY_COMPANYS = "placeorder_notify_companys";
    /**
     * 阶梯费
     **/
    private static final String REDIS_KEY_COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANYS = "COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANYS";
    /**
     * 关闭费用明细
     **/
    private static final String REDIS_KEY_CLOSE_BILL_INFO_COMPANYS = "close_bill_info_companys";
    /**
     * 未支付订单
     **/
    private static final String REDIS_KEY_COMPANY_UNPAID_ORDER = "company_unpaid_order";
    /**
     * 需要支付付款回调
     **/
    private static final String REDIS_KEY_NEED_PAY_NOTIFY_COMPANYS = "need_pay_notify_companys";
    /**
     * 预估自定义显示
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_ESTIMATE_CUSTOM_DISPLAY = "order_limit_onoff_estimate_custom_display";
    /**
     * 订单详情自定义显示
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_ORDERINFO_CUSTOM_DISPLAY = "order_limit_onoff_orderinfo_custom_display";
    /**
     * 审批单自定义显示
     */
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_APPROVALINFO_CUSTOM_DISPLAY = "order_limit_onoff_approvalinfo_custom_display:";

    /**
     * 异常订单确认后通知报表服务的公司
     */
    private static final String REDIS_KEY_NOTIFY_REPORT_WHEN_ABNORMAL_CONFIRMED = "notify_report_when_abnormal_confirmed_";

    /**
     * 取消费转待支付列表
     */
    private static final String REDIS_KEY_CANCEL_FEE_TO_WAIT_PAY = "cancel_fee_to_wait_pay";

    // 公司配置信息
    /**
     * 公司
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG = "order_limit_company_config_";
    /**
     * 机场映射
     **/
    private static final String REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANY_CONFIG = "airport_city_mapping_company_config_";
    /**
     * 城市转换
     **/
    private static final String REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANY_CONFIG = "placeorder_city_convert_company_config_";
    /**
     * 下单通知
     **/
    private static final String REDIS_KEY_PLACEORDER_NOTIFY_COMPANY_CONFIG = "placeorder_notify_company_config_";
    /**
     * 阶梯费
     **/
    private static final String REDIS_KEY_COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANY = "COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANY_";
    /**
     * 预估自定义显示
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_ESTIMATE_CUSTOM_DISPLAY_CONFIG = "order_limit_onoff_estimate_custom_display_config_";
    /**
     * 订单详情自定义显示
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_ORDERINFO_CUSTOM_DISPLAY_CONFIG = "order_limit_onoff_orderinfo_custom_display_config_";

    /**
     * 审批单列表自定义显示
     **/
    private static final String REDIS_KEY_ORDER_LIMIT_ONOFF_APPROVALINFO_CUSTOM_DISPLAY_CONFIG = "order_limit_onoff_approvalinfo_custom_display_config_";

    @Autowired
    private CityMappingMapper cityMappingMapper;
    @Autowired
    private OrderLimitConfigMapper orderLimitConfigMapper;
    @Autowired
    private OrderBaseMapper orderBaseMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private CoreMapService coreMapService;

    /**
     * 删除指定缓存
     *
     * @param companyId
     * @param key
     * @return
     */
    @Override
    public String delRedisKey(Long companyId, String key) {
        StringBuilder result = new StringBuilder();
        result.append(key + companyId);
        switch (key) {
            case REDIS_KEY_ORDER_LIMIT_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_ORDER_LIMIT_COMPANYS);
                break;
            case REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANYS);
                break;
            case REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG:
                this.redisKeyLog(result, REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG + companyId);
                break;
            case REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANY_CONFIG:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANY_CONFIG + companyId);
            case REDIS_KEY_PLACEORDER_UPGRADE_CAR_LEVL_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_UPGRADE_CAR_LEVL_COMPANYS);
                break;
            case REDIS_KEY_PLACEORDER_BIG_AMOUNT_PRE_PAY_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_BIG_AMOUNT_PRE_PAY_COMPANYS);
                break;
            case REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANYS);
                break;
            case REDIS_KEY_PLACEORDER_NOTIFY_COMPANYS:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_NOTIFY_COMPANYS);
                break;
            case REDIS_KEY_PLACEORDER_NOTIFY_COMPANY_CONFIG:
                this.redisKeyLog(result, REDIS_KEY_PLACEORDER_NOTIFY_COMPANY_CONFIG + companyId);
                break;
            default:
                result.append("_未找匹配到指定key");
        }
        return result.toString();
    }

    public void redisKeyLog(StringBuilder result, String key) {
        if (redisUtil.hasKey(key)) {
            result.append("_存在");
            redisUtil.delete(key);
            result.append("_删除后");
        }
        if (!redisUtil.hasKey(key)) {
            result.append("_不存在");
        }
    }

    /**
     * 获取设置了下单限制的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getOrderLimitCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_ORDER_LIMIT_COMPANYS,
                OrderLimitTypexEnum.PLACEORDER_LIMIT_COMPANY.getType());
        return redisCacheCompanys;
    }

    /**
     * 获取设置了一次跳转只允许下一单限制的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getOrderLimitOneH5Companys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_ORDER_LIMIT_ONE_H5,
                OrderLimitTypexEnum.PLACEORDER_LIMIT_ONE_H5.getType());
        return redisCacheCompanys;
    }

    /**
     * 进行中订单数量限制
     *
     * @return
     */
    @Override
    public List<Long> checkRunningOneOrderCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                "checkOneOrderCompanys",
                "checkOneOrderCompanys");
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否开启下单限制配置
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenOrderLimitConfig(Long companyId) {
        List<Long> orderLimitCompanys = this.getOrderLimitCompanys();
        if (orderLimitCompanys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取下单限制公司配置
     *
     * @param companyId
     * @return
     */
    @Override
    public OrderLimitConfigValueCompanyVo getOrderLimitConfig(Long companyId) {
        OrderLimitConfigValueCompanyVo result = new OrderLimitConfigValueCompanyVo();
        if (redisUtil.hasKey(REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG + companyId)) {
            Object o = redisUtil.get(REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG + companyId);
            result = (OrderLimitConfigValueCompanyVo) o;
        } else {
            OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
            orderLimitConfig.setCompanyId(companyId);
            orderLimitConfig.setType(OrderLimitTypexEnum.PLACEORDER_LIMIT_COMPANY.getType());
            OrderLimitConfig orderLimitConfigOne = orderLimitConfigMapper.selectOne(orderLimitConfig);
            if (null != orderLimitConfigOne) {
                Long cId = orderLimitConfigOne.getCompanyId();
                String value = orderLimitConfigOne.getValue();
                result.setCompanyId(cId);
                result.setValue(value);
                redisUtil.set(REDIS_KEY_ORDER_LIMIT_COMPANY_CONFIG + companyId, result);
            }
        }
        return result;
    }

    /**
     * 获取redis 存储的下单限制
     *
     * @param companyId
     * @param userId
     * @return
     */
    @Override
    public RedisCpol getRedisCpol(Long companyId, Long userId) {
        RedisCpol redisCpol = null;
        try {
            redisCpol = new cn.hutool.json.JSONObject(
                    redisUtil.hashGet(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString()))
                    .toBean(RedisCpol.class);
        } catch (Exception e) {
            log.error("获取企业自定义限制信息(通过公司id,用户id) redis 异常：{}", e);
        }
        return redisCpol;
    }

    /**
     * 保存redis 存储的下单限制 （调试使用）
     *
     * @param redisCpol
     * @param companyId
     * @param userId
     * @return
     */
    @Override
    public RedisCpol saveRedisCpol(RedisCpol redisCpol, Long companyId, Long userId) {
        redisUtil.hashPut(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString(),
                JSONUtil.toJsonStr(redisCpol), CacheConsts.ONE_HOUR);
        return getRedisCpol(companyId, userId);
    }

    /**
     * 获取企业自定义限制信息(通过公司id,用户id)
     * <p>
     * 直接在redis获取，key值可能每次下单都不同
     *
     * @param companyId
     * @param userId
     * @return
     */
    @Override
    public List<CompanyLimitVo> getCompanyLimitList(Long companyId, Long userId, String departCityCode,
            CreateOrderParam createOrderParam, List<KeyValue> customLimitMapping) {
        List<CompanyLimitVo> companyLimitList = new ArrayList<>();
        RedisCpol redisCpol = null;
        try {
            redisCpol = new cn.hutool.json.JSONObject(
                    redisUtil.hashGet(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString()))
                    .toBean(RedisCpol.class);
        } catch (Exception e) {
            log.error("获取企业自定义限制信息(通过公司id,用户id) redis 异常：{}", e);
            return null;
        }
        try {
            if (null == redisCpol) {
                return null;
            }
            RedisCpolRegulationInfo regulationInfo = redisCpol.getRegulationInfo();
            if (null == regulationInfo) {
                return null;
            }
            // 用车类型
            List<String> rideTypes = regulationInfo.getRideTypes();
            this.getRideTypes(companyLimitList, rideTypes);
            // 验证是否是交通枢纽
            if (null != customLimitMapping) {
                List<Short> serverTypes = regulationInfo.getServerTypes();
                this.getIsVerifyTrafficHub(customLimitMapping, serverTypes, createOrderParam.getServiceType());
            }

            // 申请单有效期
            RedisCpolRegulationInfoValidDate validDate = regulationInfo.getValidDate();
            this.getValidDate(companyLimitList, validDate);
            // 金额管控
            RedisCpolRegulationInfoAmount amount = regulationInfo.getAmount();
            this.getAmount(companyLimitList, amount, departCityCode, companyId, userId);
            // 次数限制
            RedisCpolRegulationInfoNumber number = regulationInfo.getNumber();
            this.getNumber(companyLimitList, number);
            // 地点管控
            RedisCpolRegulationInfoAddress address = regulationInfo.getAddress();
            this.getAddress(companyLimitList, address, companyId, customLimitMapping);

            // 可用城市
            // RedisCpolRegulationInfoAddress city = regulationInfo.getAddress();
            // this.getCity(companyLimitList, city);
            // this.getCity(companyLimitList, city);

            if (null != customLimitMapping) {
                // 是否允许跨城
                if (null != regulationInfo.getCrossRegional()) {
                    boolean crossRegional = regulationInfo.getCrossRegional();
                    KeyValue keyValue = new KeyValue();
                    keyValue.setKey("3008");
                    keyValue.setValue(crossRegional ? "true" : "false");
                    keyValue.setDescribe("是否允许跨城： true-允许 false-不允许。注：对于包含交通枢纽的无效");
                    if (!crossRegional) {
                        // 不允许跨城, 验证 是否配置了机场跨城映射
                        boolean bool = checkAirportCrossCityMapping(companyId, createOrderParam);
                        keyValue.setValue(bool ? "true" : "false");
                    }
                    customLimitMapping.add(keyValue);
                }
            }

            // 场景
            // List<RedisCpolRegulationInfoScene> scene = regulationInfo.getScene();
            // this.getScenea(companyLimitList, scene);
        } catch (Exception e) {
            log.error("获取企业自定义限制信息(通过公司id,用户id) 解析 异常：{} 获取redis信息{}", e, JSONUtil.toJsonStr(redisCpol));
        }
        return companyLimitList;
    }

    /**
     * 校验机场跨城配置
     *
     * @param companyId
     * @param createOrderParam
     * @return
     */
    @Override
    public boolean checkAirportCrossCityMapping(Long companyId, CreateOrderParam createOrderParam) {
        try {
            if (this.isOpenAirportCrossCityMapping(companyId)) {

                // 获取 企业配置信息
                String value = this.getAirportCrossCityMapping(companyId);
                JSONArray jsonArray = JSONUtil.parseArray(value);

                List<AirportCrossCityMapping> list = jsonArray.toList(AirportCrossCityMapping.class);

                // 上车坐标
                Integer departCityCode = Integer.valueOf(createOrderParam.getDepartCityCode());
                Double departLat = Double.valueOf(createOrderParam.getDepartLat());
                Double departLng = Double.valueOf(createOrderParam.getDepartLng());

                // 下车坐标
                Integer destCityCode = Integer.valueOf(createOrderParam.getDestCityCode());
                Double destLat = Double.valueOf(createOrderParam.getDestLat());
                Double destLng = Double.valueOf(createOrderParam.getDestLng());

                log.info("开始校验机场跨城配置: companyId: {}, depart: {}, dest: {}", companyId,
                        StrUtil.format("cityCode: {},lat: {}, lng: {}", departCityCode, departLat, departLng),
                        StrUtil.format("cityCode: {},lat: {}, lng: {}", destCityCode, destLat, destLng));
                // 获取起点在 机场所在城市, 终点在 映射城市 的配置信息
                List<AirportCrossCityMapping> departMappingList = list.stream().filter(item -> {
                    return item.getCityCode().contains(departCityCode)
                            && item.getMappingCityCode().contains(destCityCode);
                }).collect(Collectors.toList());

                // 获取起点在 映射城市, 终点在 机场所在城市 的配置信息
                List<AirportCrossCityMapping> destMappingList = list.stream().filter(item -> {
                    return item.getCityCode().contains(destCityCode)
                            && item.getMappingCityCode().contains(departCityCode);
                }).collect(Collectors.toList());

                // 如果起点终点都不在映射规则内,则不可 跨城用车
                if (ObjectUtils.isEmpty(departMappingList) && ObjectUtils.isEmpty(destMappingList)) {
                    log.info("机场跨城配置: 城市不匹配");
                    return false;
                }

                // 如果 起点在机场所在城市 ,终点在映射规则中匹配的城市, 起点坐标是交通枢纽, 并且起点坐标在机场坐标附近指定半径(米)内, 则可以跨城用车
                if (ObjectUtils.isNotEmpty(departMappingList)) {
                    // 起点坐标是否为交通枢纽
                    boolean isTrafficHubDepart = coreMapService.istraffichub(createOrderParam.getDepartLng(),
                            createOrderParam.getDepartLat(), createOrderParam.getCompanyId());
                    if (isTrafficHubDepart) {
                        for (AirportCrossCityMapping airportCrossCityMapping : departMappingList) {
                            AirportPoint location = airportCrossCityMapping.getLocation();
                            // 获取 起点 坐标与机场坐标 的距离
                            Integer distanceMeter = CoordinateUtil.getDistanceMeter(departLat, departLng,
                                    location.getLat(), location.getLng());
                            // 起点 坐标与机场的距离 < 规则指定距离
                            if (distanceMeter <= airportCrossCityMapping.getRadius()) {
                                return true;
                            } else {
                                log.info("机场跨城配置: 起点坐标不在机场规定半径范围内, 机场信息: {}, 起点信息: {}, 规则半径: {}, 实际距离: {}",
                                        StrUtil.format("[ 名称: {}, 城市code: {}, 坐标: {},{} ]",
                                                airportCrossCityMapping.getTitle(),
                                                airportCrossCityMapping.getCityCode(),
                                                airportCrossCityMapping.getLocation().getLat(),
                                                airportCrossCityMapping.getLocation().getLng()),
                                        StrUtil.format("[ 名称: {}, 城市code: {}, 坐标: {},{} ]",
                                                createOrderParam.getPickupLocationName(), departCityCode, departLat,
                                                departLng),
                                        airportCrossCityMapping.getRadius(),
                                        distanceMeter);
                            }
                        }
                    } else {
                        log.info("机场跨城配置: 起点坐标不是交通枢纽, 名称:{} 坐标: {}", createOrderParam.getPickupLocationName(),
                                StrUtil.format("{},{}", departLat, departLng));
                    }
                }

                // 如果 终点在机场所在城市, 起点在 映射规则城市, 终点的坐标是交通枢纽, 并且终点坐标在机场坐标指定半径(米)内, 则可以跨城用车
                if (ObjectUtils.isNotEmpty(destMappingList)) {
                    // 终点坐标是否为交通枢纽
                    boolean isTrafficHubDest = coreMapService.istraffichub(createOrderParam.getDestLng(),
                            createOrderParam.getDestLat(), createOrderParam.getCompanyId());
                    if (isTrafficHubDest) {
                        for (AirportCrossCityMapping airportCrossCityMapping : destMappingList) {
                            AirportPoint location = airportCrossCityMapping.getLocation();
                            // 获取 终点 坐标与机场坐标 的距离
                            Integer distanceMeter = CoordinateUtil.getDistanceMeter(destLat, destLng, location.getLat(),
                                    location.getLng());
                            // 终点 坐标与机场的距离 < 规则指定距离
                            if (distanceMeter <= airportCrossCityMapping.getRadius()) {
                                return true;
                            } else {
                                log.info("机场跨城配置: 终点坐标不在机场规定半径范围内, 机场信息: {}, 起点信息: {}, 规则半径: {}, 实际距离: {}",
                                        StrUtil.format("[ 名称: {}, 城市code: {}, 坐标: {},{} ]",
                                                airportCrossCityMapping.getTitle(),
                                                airportCrossCityMapping.getCityCode(),
                                                airportCrossCityMapping.getLocation().getLat(),
                                                airportCrossCityMapping.getLocation().getLng()),
                                        StrUtil.format("[ 名称: {}, 城市code: {}, 坐标: {},{} ]",
                                                createOrderParam.getDestLocationName(), destCityCode, destLat, destLng),
                                        airportCrossCityMapping.getRadius(),
                                        distanceMeter);
                            }
                        }
                    } else {
                        log.info("机场跨城配置: 终点坐标不是交通枢纽, 名称:{} 坐标: {}", createOrderParam.getDestLocationName(),
                                StrUtil.format("{},{}", destLat, destLng));
                    }
                }
            }
        } catch (Exception e) {
            log.info("校验机场跨城映射失败: error:{}, companyId: {},param: {} ", e.getMessage(), companyId,
                    JSONUtil.toJsonStr(createOrderParam));
            return false;
        }
        return false;
    }

    /**
     * 服务类型 接机 是否必须包含交通枢纽
     *
     * @param customLimitMapping
     * @param serverTypes
     */
    private void getIsVerifyTrafficHub(List<KeyValue> customLimitMapping, List<Short> serverTypes, Short serverType) {
        boolean is = false;
        if (CollectionUtils.isNotEmpty(serverTypes)) {
            Short num = 6;
            if (serverTypes.contains(num) && serverType == 6) {
                is = true;
            }
        }
        KeyValue keyValue = new KeyValue();
        keyValue.setKey("3055");
        keyValue.setValue(is ? "true" : "false");
        keyValue.setDescribe("是否必须包含交通枢纽: true-是 false-否");
        customLimitMapping.add(keyValue);
    }

    /**
     * 可用城市
     *
     * @param companyLimitList
     * @param address
     */
    private void getCity(List<CompanyLimitVo> companyLimitList, RedisCpolRegulationInfoAddress address) {
        List<Map<String, String>> list = new ArrayList<>();

        if (null != address && null != address.getDest()) {
            if (!address.getDest().isTrafficHub()) {
                String cityCodes = address.getDest().getCityCode();
                if (StringUtils.isNotBlank(cityCodes)) {
                    String[] split = cityCodes.split(",");
                    for (String s : split) {
                        Map<String, String> map = new HashMap<>();
                        map.put("value", s);
                        map.put("text", "");
                        list.add(map);
                    }
                }
            }
        }
        if (null != address && null != address.getOrigin()) {
            if (!address.getOrigin().isTrafficHub()) {

                String cityCodes = address.getOrigin().getCityCode();
                if (StringUtils.isNotBlank(cityCodes)) {
                    String[] split = cityCodes.split(",");
                    for (String s : split) {
                        Map<String, String> map = new HashMap<>();
                        map.put("value", s);
                        map.put("text", "");
                        list.add(map);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            String str = JSONUtil.toJsonStr(list);
            companyLimitList.add(new CompanyLimitVo(
                    CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_11.getType(),
                    str));
        }
    }

    /**
     * 用车类型
     *
     * @param companyLimitList
     * @param rideTypes
     * @return
     */
    private void getRideTypes(List<CompanyLimitVo> companyLimitList, List<String> rideTypes) {
        if (CollectionUtils.isNotEmpty(rideTypes)) {
            String rideTypesStr = JSONUtil.toJsonStr(rideTypes);
            companyLimitList.add(new CompanyLimitVo(
                    CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_10.getType(),
                    rideTypesStr));
        }
    }

    /**
     * 申请单有效期
     *
     * @param companyLimitList
     * @param validDate
     */
    private void getValidDate(List<CompanyLimitVo> companyLimitList, RedisCpolRegulationInfoValidDate validDate) {
        if (null != validDate) {
            if (null != validDate.getFrom()) {
                companyLimitList.add(new CompanyLimitVo(
                        CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_8.getType(),
                        DateUtil.format(validDate.getFrom(), "yyyy-MM-dd HH:mm:ss")));
            }
            if (null != validDate.getTo()) {
                companyLimitList.add(new CompanyLimitVo(
                        CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_9.getType(),
                        DateUtil.format(validDate.getTo(), "yyyy-MM-dd HH:mm:ss")));
            }
        }
    }

    /**
     * 拼装金额管控限制参数
     *
     * @param companyLimitList
     * @param amount
     * @param departCityCode
     */
    public void getAmount(List<CompanyLimitVo> companyLimitList, RedisCpolRegulationInfoAmount amount,
            String departCityCode, Long companyId, Long userId) {
        cn.hutool.json.JSONObject cityConvertMapping = this.getCityConvertMapping(companyId);
        boolean opengetCityConvertCompanys = this.isOpengetCityConvertCompanys(companyId);
        if (null != amount) {
            BigDecimal base = new BigDecimal("100");
            // 城市金额限制
            List<RedisCpolRegulationInfoAmountCity> city = amount.getCity();

            boolean isCity = false;
            if (CollectionUtils.isNotEmpty(city)) {
                List<String> cityStrList = new ArrayList<>();
                if (opengetCityConvertCompanys && null != cityConvertMapping) {
                    cityStrList = city.stream().map(o -> cityConvertMapping.getStr(o.getCityCode()))
                            .collect(Collectors.toList());
                } else {
                    cityStrList = city.stream().map(o -> o.getCityCode()).collect(Collectors.toList());
                }
                isCity = cityStrList.contains(departCityCode);
            }

            if (isCity) {

                if (null != cityConvertMapping) {
                    city = city.stream().map(o -> {
                        o.setCityCode(cityConvertMapping.getStr(o.getCityCode()));
                        return o;
                    }).collect(Collectors.toList());
                }

                Map<String, RedisCpolRegulationInfoAmountCity> cityCodeMap = city.stream().collect(Collectors
                        .toMap(RedisCpolRegulationInfoAmountCity::getCityCode, Function.identity(), (v1, v2) -> v1));
                RedisCpolRegulationInfoAmountCity redisCpolRegulationInfoAmountCity = cityCodeMap.get(departCityCode);
                if (null != redisCpolRegulationInfoAmountCity) {
                    if (null != redisCpolRegulationInfoAmountCity.getLimitedAmount()) {
                        this.setCompanyLimitList(companyLimitList,
                                redisCpolRegulationInfoAmountCity.getLimitedAmount().divide(base),
                                CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_1.getType(),
                                redisCpolRegulationInfoAmountCity.getCityCode());
                    }
                    if (null != redisCpolRegulationInfoAmountCity.getLimitedDayAmount()) {
                        this.setCompanyLimitList(companyLimitList,
                                redisCpolRegulationInfoAmountCity.getLimitedDayAmount().divide(base),
                                CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_2.getType(),
                                redisCpolRegulationInfoAmountCity.getCityCode());
                    }

                    if (null != redisCpolRegulationInfoAmountCity.getLimitedWeekAmount()) {
                        this.setCompanyLimitList(companyLimitList,
                                redisCpolRegulationInfoAmountCity.getLimitedWeekAmount().divide(base),
                                CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_12.getType(),
                                redisCpolRegulationInfoAmountCity.getCityCode());
                    }
                    if (null != redisCpolRegulationInfoAmountCity.getLimitedMonthAmount()) {
                        this.setCompanyLimitList(companyLimitList,
                                redisCpolRegulationInfoAmountCity.getLimitedMonthAmount().divide(base),
                                CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_3.getType(),
                                redisCpolRegulationInfoAmountCity.getCityCode());
                    }
                    if (null != redisCpolRegulationInfoAmountCity.getLimitedTotalAmount()) {
                        this.setCompanyLimitList(companyLimitList,
                                redisCpolRegulationInfoAmountCity.getLimitedTotalAmount().divide(base),
                                CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_4.getType(),
                                redisCpolRegulationInfoAmountCity.getCityCode());
                    }
                }

                // 金额限制
            } else {
                if (null != amount.getLimitedAmount()) {
                    this.setCompanyLimitList(companyLimitList,
                            amount.getLimitedAmount().divide(base),
                            CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_1.getType(), null);
                }
                if (null != amount.getLimitedDayAmount()) {
                    this.setCompanyLimitList(companyLimitList,
                            amount.getLimitedDayAmount().divide(base),
                            CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_2.getType(), null);
                }
                if (null != amount.getLimitedWeekAmount()) {
                    this.setCompanyLimitList(companyLimitList,
                            amount.getLimitedWeekAmount().divide(base),
                            CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_12.getType(), null);
                }
                if (null != amount.getLimitedMonthAmount()) {
                    this.setCompanyLimitList(companyLimitList,
                            amount.getLimitedMonthAmount().divide(base),
                            CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_3.getType(), null);
                }
                if (null != amount.getLimitedTotalAmount()) {
                    this.setCompanyLimitList(companyLimitList,
                            amount.getLimitedTotalAmount().divide(base),
                            CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_4.getType(), null);
                }
            }
        }
    }

    public void setCompanyLimitList(List<CompanyLimitVo> companyLimitList, BigDecimal amount, int type,
            String cityCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", true);
        map.put("value", amount);
        if (StringUtils.isNotBlank(cityCode)) {
            map.put("cityCode", cityCode);
        }
        companyLimitList.add(new CompanyLimitVo(type, JSONUtil.toJsonStr(map)));
    }

    /**
     * 拼装次数限制限制参数
     *
     * @param companyLimitList
     * @param number
     */
    private void getNumber(List<CompanyLimitVo> companyLimitList, RedisCpolRegulationInfoNumber number) {
        if (null != number) {
            if (null != number.getLimitedPeriodNumber()) {
                Map<String, Object> map = new HashMap<>();
                map.put("enabled", true);
                map.put("value", number.getLimitedPeriodNumber());
                companyLimitList.add(new CompanyLimitVo(
                        CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_5.getType(),
                        JSONUtil.toJsonStr(map)));
            }
            if (null != number.getLimitedMonthNumber()) {
                Map<String, Object> map = new HashMap<>();
                map.put("enabled", true);
                map.put("value", number.getLimitedMonthNumber());
                companyLimitList.add(new CompanyLimitVo(
                        CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_6.getType(),
                        JSONUtil.toJsonStr(map)));
            }
        }
    }

    /**
     * 拼装地点管控限制参数
     *
     * @param companyLimitList
     * @param address
     */
    private void getAddress(List<CompanyLimitVo> companyLimitList, RedisCpolRegulationInfoAddress address,
            Long companyId, List<KeyValue> customLimitMapping) {
        cn.hutool.json.JSONObject cityConvertMapping = this.getCityConvertMapping(companyId);
        if (null != address) {
            RedisCpolRegulationInfoAddressInfo any = address.getAny();
            if (null != any) {
                String cityCode = any.getCityCode();
                if (null != cityConvertMapping) {
                    List<String> split = Arrays.asList(cityCode.split(","));
                    split = split.stream().map(o -> o = cityConvertMapping.getStr(o)).collect(Collectors.toList());
                    cityCode = JSONUtil.toJsonStr(String.join(",", split));
                }
                any.setCityCode(cityCode);

                if (null == customLimitMapping) {
                    customLimitMapping = new ArrayList<KeyValue>();
                }
                KeyValue keyValue = new KeyValue();
                keyValue.setKey("3056");
                keyValue.setValue(JSONUtil.toJsonStr(any));
                keyValue.setDescribe("任一点包含交通枢纽：起点和终点任意一点包含交通枢纽，另一端在固定的城市中即可。如果两端都是交通枢纽也允许。");
                customLimitMapping.add(keyValue);
                return;
            }

            // 转换地址格式
            Address addr = new Address();
            if (null != address.getType()) {
                addr.setType(address.getType());
            }

            if (null != address.getOrigin() && !address.getOrigin().isTrafficHub()) {
                String cityCode = address.getOrigin().getCityCode();

                if (StringUtils.isNotBlank(cityCode) && null != cityConvertMapping) {
                    List<String> split = Arrays.asList(cityCode.split(","));
                    split = split.stream().map(o -> o = cityConvertMapping.getStr(o)).collect(Collectors.toList());
                    cityCode = JSONUtil.toJsonStr(String.join(",", split));
                }

                if (StringUtils.isBlank(cityCode)) {
                    addr.setBeginAny(true);
                    addr.setBeginName("任意地点");
                } else {
                    addr.setBeginAny(false);
                    addr.setBeginName("");
                }

                addr.setBeginCity(cityCode);
                addr.setBeginDistance("");
                List<RedisCpolRegulationInfoAddressInfoCoordinate> coordinate = address.getOrigin().getCoordinate();
                addr.setBeginCoordinate(coordinate);
                addr.setBeginTrafficHub(address.getOrigin().isTrafficHub());

            } else if (null != address.getOrigin() && address.getOrigin().isTrafficHub()) {
                addr.setBeginTrafficHub(address.getOrigin().isTrafficHub());
            }

            if (null != address.getDest() && !address.getDest().isTrafficHub()) {
                String cityCode = address.getDest().getCityCode();

                if (StringUtils.isNotBlank(cityCode) && null != cityConvertMapping) {
                    List<String> split = Arrays.asList(cityCode.split(","));
                    split = split.stream().map(o -> o = cityConvertMapping.getStr(o)).collect(Collectors.toList());
                    cityCode = JSONUtil.toJsonStr(String.join(",", split));
                }

                if (StringUtils.isBlank(cityCode)) {
                    addr.setEndAny(true);
                    addr.setEndName("任意地点");
                } else {
                    addr.setEndAny(false);
                    addr.setEndName("");
                }

                addr.setEndCity(cityCode);
                addr.setEndDistance("");
                List<RedisCpolRegulationInfoAddressInfoCoordinate> coordinate = address.getDest().getCoordinate();
                addr.setEndCoordinate(coordinate);
                addr.setEndTrafficHub(address.getDest().isTrafficHub());
            } else if (null != address.getDest() && address.getDest().isTrafficHub()) {
                addr.setEndTrafficHub(address.getDest().isTrafficHub());
            }

            // if(null == addr.getBeginCity() && null == addr.getEndCity()
            // && !addr.isBeginTrafficHub()
            // && !addr.isEndTrafficHub()){
            // return;
            // }
            companyLimitList.add(new CompanyLimitVo(
                    CompanyLimitTypexEnum.COMPANY_LIMIT_TYPE_7.getType(),
                    JSONUtil.toJsonStr(addr)));

        }
    }

    /**
     * 拼装场景限制参数
     *
     * @param companyLimitList
     * @param scene
     */
    private void getScenea(List<CompanyLimitVo> companyLimitList, List<RedisCpolRegulationInfoScene> scene) {
        if (null != scene) {

        }
    }

    /**
     * 获取企业自定义限制信息映射
     *
     * @param companyId
     * @param userId
     * @param departCityCode
     * @return
     */
    @Override
    public List<KeyValue> getCustomLimitMapping(Long companyId, Long userId, String departCityCode,
            CreateOrderParam createOrderParam) {
        List<KeyValue> customLimitMapping = new ArrayList<>();
        List<CompanyLimitVo> companyLimitList = this.getCompanyLimitList(companyId, userId, departCityCode,
                createOrderParam, customLimitMapping);
        if (null == companyLimitList) {
            return null;
        }
        if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + companyId)) {
            List<CompanyLimitMappingVo> mappingList = orderBaseMapper.selectLimitMappingByCompanyId(companyId);
            if (mappingList != null && mappingList.size() > 0) {
                redisUtil.set(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + companyId, mappingList);
            }
        }
        List<CompanyLimitMappingVo> mappingList = (List<CompanyLimitMappingVo>) redisUtil
                .get(CacheConsts.REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING + companyId);
        if (CollectionUtils.isEmpty(mappingList)) {
            return null;
        }
        for (CompanyLimitMappingVo vo : mappingList) {
            for (CompanyLimitVo companyLimitVo : companyLimitList) {
                if (vo.getType() == companyLimitVo.getType()) {
                    KeyValue keyValue = new KeyValue();
                    keyValue.setKey(vo.getParaCode());
                    keyValue.setValue(companyLimitVo.getValue());
                    customLimitMapping.add(keyValue);
                    break;
                }
            }
        }

        return customLimitMapping;
    }

    /**
     * 判断当前用户是否有进行中的订单（配置了下单限制的公司）
     * 说明：下单限制的客户，每次登录最多下一单，切进行中的订单只有一笔，目前通过缓存获取进行中的订单
     *
     * @param companyId 公司id
     * @param userId    用户id
     * @return true-存在 false-不存在
     */
    @Override
    public boolean existsRunningOrders(Long companyId, Long userId) {
        boolean isLimit = true;
        boolean isLimitH5 = true;
        boolean isRunning = true;
        List<Long> orderLimitCompanys = this.getOrderLimitCompanys();
        if (CollectionUtils.isEmpty(orderLimitCompanys)) {
            isLimit = false;
        }
        if (isLimit && !orderLimitCompanys.contains(companyId)) {
            isLimit = false;
        }

        List<Long> orderLimitOneCompanys = this.getOrderLimitOneH5Companys();
        if (CollectionUtils.isEmpty(orderLimitOneCompanys)) {
            isLimitH5 = false;
        }
        if (isLimitH5 && !orderLimitOneCompanys.contains(companyId)) {
            isLimitH5 = false;
        }

        List<Long> longs = this.checkRunningOneOrderCompanys();

        if (CollectionUtils.isEmpty(longs)) {
            isRunning = false;
        }
        if (isRunning && !longs.contains(companyId)) {
            isRunning = false;
        }

        if (!isLimit && !isLimitH5 && !isRunning) {
            return false;
        }

        Integer runningOrderCount = orderBaseMapper.selectRunningOrderByUserId(userId);
        if (null != runningOrderCount && runningOrderCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteCustomInfo(Long companyId, Long userId) {
        List<Long> orderLimitOneCompanys = this.getOrderLimitOneH5Companys();
        if (CollectionUtils.isNotEmpty(orderLimitOneCompanys) && orderLimitOneCompanys.contains(companyId)
                && redisUtil.hasKey(CacheConsts.REDIS_KEY_USER_CUSTOM_INFO + userId.toString())) {
            redisUtil.delete(CacheConsts.REDIS_KEY_USER_CUSTOM_INFO + userId.toString());
        }
    }

    /**
     * 获取设置了免费升舱的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getUpgradeCarLevlCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_PLACEORDER_UPGRADE_CAR_LEVL_COMPANYS,
                OrderLimitTypexEnum.PLACEORDER_UPGRADE_CAR_LEVL.getType());
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否开启了免费升舱（根据公司id）
     *
     * @return
     */
    @Override
    public boolean isOpenUpgradeCarLevlCompanys(Long companyId) {
        List<Long> upgradeCarLevlCompanys = this.getUpgradeCarLevlCompanys();
        if (upgradeCarLevlCompanys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取设置了大额预付的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getBigAmountPrePayCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_PLACEORDER_BIG_AMOUNT_PRE_PAY_COMPANYS,
                OrderLimitTypexEnum.PLACEORDER_BIG_AMOUNT_PRE_PAY.getType());
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否开启了大额预付（根据公司id）
     *
     * @return
     */
    @Override
    public boolean isOpenBigAmountPrePayCompanys(Long companyId) {
        List<Long> companys = this.getBigAmountPrePayCompanys();
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 判断公司是否开启了配置费用（根据公司id，运维后台编辑企业-->基础设置-->费用配置）
     *
     * @return
     */
    @Override
    public boolean isOpenSettingFee(Long companyId) {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_STTING_FEE_COMPANYS,
                OrderLimitTypexEnum.SETTING_FEE.getType());
        if (redisCacheCompanys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取设置了城市转换的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getCityConvertCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANYS,
                OrderLimitTypexEnum.PLACEORDER_CITY_CONVERT.getType());
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否开启了城市转换（根据公司id）
     *
     * @return
     */
    @Override
    public boolean isOpengetCityConvertCompanys(Long companyId) {
        List<Long> companys = this.getCityConvertCompanys();
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 城市转换
     *
     * @param companyId
     */
    @Override
    public cn.hutool.json.JSONObject getCityConvertMapping(Long companyId) {
        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
        boolean opengetCityConvertCompanys = this.isOpengetCityConvertCompanys(companyId);
        if (!opengetCityConvertCompanys) {
            return null;
        }

        String key = REDIS_KEY_PLACEORDER_CITY_CONVERT_COMPANY_CONFIG + companyId;

        OrderLimitConfig result = new OrderLimitConfig();
        if (redisUtil.hasKey(key)) {
            Object o = redisUtil.get(key);
            jsonObject = JSONUtil.parseObj(o);
        } else {
            List<CityMapping> cityMappings = cityMappingMapper.selectByCompanyId(companyId);
            if (CollectionUtils.isNotEmpty(cityMappings)) {
                for (int i = 0; i < cityMappings.size(); i++) {
                    jsonObject.put(cityMappings.get(i).getPartnerCityId(), cityMappings.get(i).getOurCityId());
                }
                redisUtil.set(key, JSONUtil.toJsonStr(jsonObject), CacheConsts.STABLE_CACHE_EXPIRE_TIME);
            }
        }

        return jsonObject;

    }

    /**
     * 获取设置了下单通知配置的公司列表
     *
     * @return
     */
    @Override
    public List<Long> getCompanyPlaceorderNotifyCompanys() {
        List<Long> redisCacheCompanys = this.getRedisCacheCompanys(
                REDIS_KEY_PLACEORDER_NOTIFY_COMPANYS,
                OrderLimitTypexEnum.PLACEORDER_NOTIFY.getType());
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否开启了下单通知配置（根据公司id）
     *
     * @return
     */
    @Override
    public boolean isOpengetCompanyPlaceorderNotify(Long companyId) {
        List<Long> companys = this.getCompanyPlaceorderNotifyCompanys();
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取公司下单通知配置是否开启
     *
     * @return
     */
    @Override
    public boolean getCompanyPlaceorderNotifyConfig(CreateOrderParam orderParam, String type) {
        boolean opengetCityConvertCompanys = this.isOpengetCompanyPlaceorderNotify(orderParam.getCompanyId());
        if (!opengetCityConvertCompanys) {
            // 没配置就通知
            return true;
        }
        OrderLimitConfig result = this.getRedisCacheCompanyConfig(REDIS_KEY_PLACEORDER_NOTIFY_COMPANY_CONFIG,
                OrderLimitTypexEnum.PLACEORDER_NOTIFY.getType(),
                orderParam.getCompanyId());
        String value = result.getValue();
        if (StringUtils.isEmpty(value)) {
            // 没配置就通知
            return true;
        }
        JSONObject jsonObject = JSONObject.parseObject(value);
        String o = (String) jsonObject.get(type);
        if (StringUtils.equals(o, "F")) {
            return false;
        }
        return true;
    }

    /**
     * 计算阶梯费用
     *
     * @param companyId
     * @param amount
     * @return
     */
    @Override
    public BigDecimal getCompanyTieredfee(Long companyId, BigDecimal amount) {
        log.info("getCompanyTieredfee======> companyId:{},amount:{}", companyId, amount);
        String type = "tieredfee";
        boolean openCompany = this.isOpenCompany(REDIS_KEY_COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANYS, type, companyId);
        if (openCompany) {
            OrderLimitConfig orderLimitConfig = this
                    .getRedisCacheCompanyConfig(REDIS_KEY_COMPANY_CUSTOM_CONFIG_TIEREDFEE_COMPANY, type, companyId);
            String value = orderLimitConfig.getValue();
            JSONObject jsonObject = JSONObject.parseObject(value);
            BigDecimal fee = null;
            if (null != jsonObject) {
                com.alibaba.fastjson.JSONArray tieredfee = jsonObject.getJSONArray("tieredfee");
                if (amount != null && null != tieredfee && tieredfee.size() > 0) {
                    for (int i = 0; i < tieredfee.size(); i++) {
                        JSONObject jsonObject1 = tieredfee.getJSONObject(i);
                        BigDecimal start = new BigDecimal(jsonObject1.getString("start"));
                        BigDecimal end = new BigDecimal(jsonObject1.getString("end"));
                        if (amount.compareTo(start) != -1 && amount.compareTo(end) == -1) {
                            fee = jsonObject1.getBigDecimal("fee");
                            break;
                        }
                    }
                }
            }
            log.info("getCompanyTieredfee======> companyId:{},amount:{},fee:{}", companyId, amount, fee);
            return fee;
        }
        return null;
    }

    @Override
    public boolean isOpenShowBillCompanys(Long companyId) {
        // 屏蔽订单详情的公司
        List<Long> companys = this.getRedisCacheCompanys(REDIS_KEY_CLOSE_BILL_INFO_COMPANYS, "isBillClose");
        if (companys.contains(companyId)) {
            return false;
        }
        return true;
    }

    /**
     * 判断公司是否异常订单确认通知报表（根据公司id）
     *
     * @return
     */
    @Override
    public boolean isOpenAnbnormalConfirmedNotifyReport(Long companyId) {
        OrderLimitConfig orderLimitConfig = this.getRedisCacheCompanyConfig(
                REDIS_KEY_NOTIFY_REPORT_WHEN_ABNORMAL_CONFIRMED, "isNotifyReportWhenAbnormalConfirmed", companyId);
        if (null == orderLimitConfig || null == orderLimitConfig.getCompanyId()) {
            return false;
        }
        return true;
    }

    /**
     * 只允许新能源车型接单
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOnlyAllowGreenTravel(Long companyId) {

        List<Long> companys = this.getRedisCacheCompanyIdListByType(OrderLimitConstant.TYPE_ISONLYALLOWGREENTRAVEL);
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOpenUnpaidOrderCompanys(Long companyId) {
        // 屏蔽订单详情的公司isUnpaidOrder
        List<Long> companys = this.getRedisCacheCompanys(REDIS_KEY_COMPANY_UNPAID_ORDER, "isUnpaidOrder");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOpenNeedPayNotifyCompanys(Long companyId) {

        List<Long> companys = this.getRedisCacheCompanys(REDIS_KEY_NEED_PAY_NOTIFY_COMPANYS, "isNeedPayNotify");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 判断公司是否开启下单关联第三方id
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenPartnerOrderId(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                "company:place:order:switch:isOpenPartnerOrderId",
                "isOpenPartnerOrderId");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取下单关联第三方id配置code
     *
     * @param companyId
     * @return
     */
    @Override
    public String getPartnerOrderIdCode(Long companyId) {
        OrderLimitConfig isOpenPartnerOrderId = this.getRedisCacheCompanyConfig(
                "company:place:order:switch:isOpenPartnerOrderId",
                "isOpenPartnerOrderId",
                companyId);
        String value = isOpenPartnerOrderId.getValue();
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(value);
        String code = jsonObject.getStr("code");
        return code;

    }

    /**
     * 判断公司是否开启预估自定义显示
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenEstimateCustomDisplay(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                REDIS_KEY_ORDER_LIMIT_ONOFF_ESTIMATE_CUSTOM_DISPLAY,
                "isOpenEstimateCustomDisplay");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 预估自定义显示配置
     *
     * @param companyId
     * @return
     */
    @Override
    public String getEstimateCustomDisplay(Long companyId) {
        OrderLimitConfig estimateCustomDisplay = this.getRedisCacheCompanyConfig(
                REDIS_KEY_ORDER_LIMIT_ONOFF_ESTIMATE_CUSTOM_DISPLAY_CONFIG,
                "isOpenEstimateCustomDisplay",
                companyId);
        String value = estimateCustomDisplay.getValue();
        return value;

    }

    /**
     * 判断公司是否开启订单明细自定义显示
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenOrderInfoCustomDisplay(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                REDIS_KEY_ORDER_LIMIT_ONOFF_ORDERINFO_CUSTOM_DISPLAY,
                "isOpenOrderinfoCustomDisplay");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 订单明细自定义显示配置
     *
     * @param companyId
     * @return
     */
    @Override
    public String getOrderInfoCustomDisplay(Long companyId) {
        OrderLimitConfig estimateCustomDisplay = this.getRedisCacheCompanyConfig(
                REDIS_KEY_ORDER_LIMIT_ONOFF_ORDERINFO_CUSTOM_DISPLAY_CONFIG,
                "isOpenOrderinfoCustomDisplay",
                companyId);
        String value = estimateCustomDisplay.getValue();
        return value;
    }

    /**
     * 判断公司是否开启审批单列表自定义显示
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenApprovalListCustomDisplay(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                REDIS_KEY_ORDER_LIMIT_ONOFF_APPROVALINFO_CUSTOM_DISPLAY,
                "isOpenApprovalListCustomDisplay");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 审批单列表自定义显示配置
     *
     * @param companyId
     * @return
     */
    @Override
    public String getApprovalListCustomDisplay(Long companyId) {
        OrderLimitConfig approvalListCustomDisplay = this.getRedisCacheCompanyConfig(
                REDIS_KEY_ORDER_LIMIT_ONOFF_APPROVALINFO_CUSTOM_DISPLAY_CONFIG,
                "isOpenApprovalListCustomDisplay",
                companyId);
        String value = approvalListCustomDisplay.getValue();
        return value;
    }

    /**
     * 判断公司是否开启了某个配置
     *
     * @param companyId
     * @param key
     * @return
     */
    @Override
    public boolean isOpenCompany(String key, String type, Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(key, type);
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取开启配置的公司id列表
     *
     * @param key  缓存key
     * @param type 配置类型
     * @return
     */
    @Override
    public List<Long> getRedisCacheCompanys(String key, String type) {
        List<Long> result = new ArrayList<>();
        if (redisUtil.hasKey(key)) {
            Object o = redisUtil.get(key);
            result = (List<Long>) o;
        } else {
            OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
            orderLimitConfig.setType(type);
            List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
            result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(result)) {
                redisUtil.set(key, result);
            }
        }
        return result;
    }

    /**
     * 获取企业下单限制开关 (根据公司id与类型)
     *
     * @param type 开关配置类型
     * @return
     */
    @Override
    public boolean isOpenCompanyOrderLimitByType(Long companyId, String type) {
        List<CompanyOrderLimit> companyOrderLimitAll = this.getCompanyOrderLimitAll();
        if (CollectionUtils.isNotEmpty(companyOrderLimitAll)) {
            int size = companyOrderLimitAll.stream()
                    .filter(cl -> StringUtils.equals(cl.getType(), type)
                            && cl.getCompanyList().contains(companyId))
                    .collect(Collectors.toList()).size();
            if (size > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取企业下单限制 (根据公司id与类型)
     *
     * @param type 开关配置类型
     * @return
     */
    @Override
    public OrderLimitConfig getCompanyOrderLimitByType(Long companyId, String type) {
        List<CompanyOrderLimit> companyOrderLimitAll = this.getCompanyOrderLimitAll();
        if (CollectionUtils.isNotEmpty(companyOrderLimitAll)) {
            List<CompanyOrderLimit> collect = companyOrderLimitAll.stream()
                    .filter(cl -> StringUtils.equals(cl.getType(), type)
                            && cl.getCompanyList().contains(companyId))
                    .collect(Collectors.toList());
            if (collect.size() > 0) {
                return collect.get(0).getCompanyConfigList().get(0);
            }
        }
        return null;
    }

    /**
     * 获取企业下单限制 (获取全部)
     *
     * @return
     */
    @Override
    public List<CompanyOrderLimit> getCompanyOrderLimitAll() {
        String key = "companyOrderLimit";
        List<CompanyOrderLimit> typeList = new ArrayList<>();
        if (redisUtil.hasKey(key)) {
            Object o = redisUtil.get(key);
            typeList = (List<CompanyOrderLimit>) o;
        } else {
            typeList = this.getCompanyOrderLimitAllRe();
        }
        return typeList;
    }

    /**
     * 获取企业下单限制 (刷新获取全部)
     *
     * @return
     */
    @Override
    public List<CompanyOrderLimit> getCompanyOrderLimitAllRe() {
        String key = "companyOrderLimit";
        List<CompanyOrderLimit> typeList = new ArrayList<>();
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        Map<String, List<OrderLimitConfig>> typeMap = orderLimitConfigs.stream()
                .collect(Collectors.groupingBy(OrderLimitConfig::getType));
        if (null != typeMap) {
            for (Map.Entry<String, List<OrderLimitConfig>> entry : typeMap.entrySet()) {
                String typeKey = entry.getKey();
                List<OrderLimitConfig> typeValue = entry.getValue();
                CompanyOrderLimit companyOrderLimit = new CompanyOrderLimit();
                companyOrderLimit.setType(typeKey);
                companyOrderLimit.setCompanyConfigList(typeValue);
                companyOrderLimit
                        .setCompanyList(typeValue.stream().map(o -> o.getCompanyId()).collect(Collectors.toList()));
                typeList.add(companyOrderLimit);
            }
            redisUtil.set(key, typeList);
        }
        return typeList;
    }

    /**
     * 根据配置项类型获取对应的公司id列表
     *
     * @param type 配置类型
     * @return
     */
    @Override
    public List<Long> getRedisCacheCompanyIdListByType(String type) {
        String key = OrderLimitConstant.REDIS_KEY_PREFIX + type;
        List<Long> result = new ArrayList<>();
        if (redisUtil.hasKey(key)) {
            Map<Object, Object> map = redisUtil.hashGetAll(key);
            if (null != map) {
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    result.add(Long.parseLong(String.valueOf(entry.getKey())));
                }
            }
        } else {
            OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
            orderLimitConfig.setType(type);
            List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
            if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
                for (OrderLimitConfig o : orderLimitConfigs) {
                    result.add(o.getCompanyId());
                    redisUtil.hashPut(key, o.getCompanyId().toString(), o.getValue());
                }
            } else {
                redisUtil.hashPut(key, "88888", "");
            }
        }
        return result;
    }

    /**
     * 根据类型对应的公司id获取配置值
     *
     * @param type      配置类型
     * @param companyId 公司id
     * @return
     */
    @Override
    public String getRedisCacheValueByType(String type, Long companyId) {
        String key = OrderLimitConstant.REDIS_KEY_PREFIX + type;
        String result = "";
        if (redisUtil.hashHasKey(key, companyId.toString())) {
            Object o = redisUtil.hashGet(key, companyId.toString());
            if (null != o) {
                result = String.valueOf(o);
            }
        } else {
            OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
            orderLimitConfig.setType(type);
            orderLimitConfig.setCompanyId(companyId);
            List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
            if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
                OrderLimitConfig config = orderLimitConfigs.get(0);
                result = config.getValue();
                redisUtil.hashPut(key, config.getCompanyId().toString(), config.getValue());
            } else {
                redisUtil.hashPut(key, companyId.toString(), "");
            }
        }
        return result;
    }

    /**
     * 获取公司配置信息
     *
     * @param key       缓存key
     * @param type      配置类型
     * @param companyId 公司id
     * @return
     */
    @Override
    public OrderLimitConfig getRedisCacheCompanyConfig(String key, String type, Long companyId) {
        OrderLimitConfig result = new OrderLimitConfig();
        if (redisUtil.hasKey(key + companyId)) {
            Object o = redisUtil.get(key + companyId);
            result = (OrderLimitConfig) o;
        } else {
            OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
            orderLimitConfig.setCompanyId(companyId);
            orderLimitConfig.setType(type);
            result = orderLimitConfigMapper.selectOne(orderLimitConfig);
            if (null != result) {
                redisUtil.set(key + companyId, result, CacheConsts.STABLE_CACHE_EXPIRE_TIME);
            }
        }
        return result;
    }

    @Override
    public boolean isOpenAirportCrossCityMapping(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANYS,
                "isOpenAirportCityMapping");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }

    @Override
    public String getAirportCrossCityMapping(Long companyId) {
        OrderLimitConfig approvalListCustomDisplay = this.getRedisCacheCompanyConfig(
                REDIS_KEY_PLACEORDER_AIRPORT_CITY_MAPPING_COMPANY_CONFIG,
                "isOpenAirportCityMapping",
                companyId);
        return approvalListCustomDisplay.getValue();
    }

    @Override
    public boolean isOpenCancelFeeToWaitPay(Long companyId) {
        List<Long> companys = this.getRedisCacheCompanys(
                REDIS_KEY_CANCEL_FEE_TO_WAIT_PAY,
                "isOpenCancelFeeToWaitPay");
        if (companys.contains(companyId)) {
            return true;
        }
        return false;
    }
}