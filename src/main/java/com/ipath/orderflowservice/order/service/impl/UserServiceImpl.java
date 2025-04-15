package com.ipath.orderflowservice.order.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.core.map.bean.SearchRes;
import com.ipath.orderflowservice.core.map.service.CoreMapService;
import com.ipath.orderflowservice.feignclient.UserFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestGetUserBaseInfoDto;
import com.ipath.orderflowservice.feignclient.dto.UserBaseInfoDto;
import com.ipath.orderflowservice.order.bean.*;
import com.ipath.orderflowservice.order.bean.bo.RegulationBo;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.OrderLimitConstant;
import com.ipath.orderflowservice.order.bean.constant.UserConstant;
import com.ipath.orderflowservice.order.bean.vo.CacheUserInfo;
import com.ipath.orderflowservice.order.bean.vo.RecommendedLocationConfigVo;
import com.ipath.orderflowservice.order.bean.vo.RecommendedLocationInfoLocationVo;
import com.ipath.orderflowservice.order.bean.vo.RecommendedLocationInfoVo;
import com.ipath.orderflowservice.order.dao.CompanyUserConfigMapper;
import com.ipath.orderflowservice.order.dao.OrderPlaceHistoryMonthMapper;
import com.ipath.orderflowservice.order.dao.bean.CompanyLocations;
import com.ipath.orderflowservice.order.dao.bean.CompanyUserConfig;
import com.ipath.orderflowservice.order.service.*;
import com.ipath.orderflowservice.order.util.CoordinateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 用户 ServiceImpl
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private CompanyUserConfigMapper companyUserConfigMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private OrderPlaceHistoryMonthMapper orderPlaceHistoryMonthMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SnowFlakeUtil snowFlakeUtil;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private CoreMapService coreMapService;
    @Autowired
    private CipherService cipherService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OrderLimitService orderLimitService;
    @Autowired
    private RegulationService regulationService;

    private static final String COMPANY_USER_CONFIG_IS_SHOW_GREEN_TRAVEL = "company_user_config_is_show_green_travel:";
    private static final String REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS = "company:user:config:unpaid:orders:";
    private static final String REDIS_KEY_COMPANY_USER_CONFIG_CHECK = "company:user:config:check";


    @Override
    public boolean existUnpaid(Long userId, Long companyId) throws Exception {

        try {
            boolean b = redisUtil.hasKey(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId);
            if (b) {
                return true;
            } else {
                CompanyUserConfig companyUserConfig = new CompanyUserConfig();
                companyUserConfig.setCompanyId(companyId);
                companyUserConfig.setUserId(userId);
                companyUserConfig.setType("2");
                CompanyUserConfig rec = companyUserConfigMapper.selectOne(companyUserConfig);
                if (null != rec) {
                    Object value = rec.getValue();
                    JSONArray array = JSONUtil.parseArray(value);
                    if (null != array && array.size() > 0) {
                        redisUtil.set(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId, value, 3600 * 1 * 24L);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("existUnpaid ==> userId:{},companyId{},异常： {}!", userId, companyId, e);
        }
        return false;

    }

    //屏蔽订单挂起功能，如果后面其他企业需要，请解开注释，并屏蔽上面方法
//    @Override
//    public boolean existUnpaid(Long userId, Long companyId) throws Exception {
//
//        try {
//            boolean b = redisUtil.hasKey(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId);
//            if (b) {
//                Object value = redisUtil.get(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId);
//                JSONArray array = JSONUtil.parseArray(value);
//                List<OrderPendingMapping> orderPendingMappings = orderPendingMappingMapper.selectByUserId(userId);
//                for (int i = 0; i < orderPendingMappings.size(); i++) {
//                    array.remove(orderPendingMappings.get(i).getOrderId());
//                }
//                if (null != array && array.size() > 0) {
//                    return true;
//                }
//            } else {
//                CompanyUserConfig companyUserConfig = new CompanyUserConfig();
//                companyUserConfig.setCompanyId(companyId);
//                companyUserConfig.setUserId(userId);
//                companyUserConfig.setType("2");
//                CompanyUserConfig rec = companyUserConfigMapper.selectOne(companyUserConfig);
//                if (null != rec) {
//                    Object value = rec.getValue();
//                    JSONArray array = JSONUtil.parseArray(value);
//
//                    List<OrderPendingMapping> orderPendingMappings = orderPendingMappingMapper.selectByUserId(userId);
//                    for (int i = 0; i < orderPendingMappings.size(); i++) {
//                        array.remove(orderPendingMappings.get(i).getOrderId());
//                    }
//
//                    if (null != array && array.size() > 0) {
//                        redisUtil.set(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId, value, 3600 * 1 * 24L);
//                        return true;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("existUnpaid ==> userId:{},companyId{},异常： {}!", userId, companyId, e);
//        }
//        return false;
//
//    }

    @Override
    public boolean existUserCheck(Long userId, Long companyId) {

        try {
            boolean b = redisUtil.hasKey(REDIS_KEY_COMPANY_USER_CONFIG_CHECK + userId);
            if (b) {
                return true;
            } else {
                CompanyUserConfig companyUserConfig = new CompanyUserConfig();
                companyUserConfig.setCompanyId(companyId);
                companyUserConfig.setUserId(userId);
                companyUserConfig.setType("3");
                CompanyUserConfig rec = companyUserConfigMapper.selectOne(companyUserConfig);
                if (null != rec) {
                    redisUtil.set(REDIS_KEY_COMPANY_USER_CONFIG_CHECK + userId, "", 3600 * 1 * 24L);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("existUserCheck ==> userId:{},companyId{},异常： {}!", userId, companyId, e);
        }
        return false;

    }

    @Override
    public boolean addUnpaid(Long userId, Long companyId, Long orderId) throws Exception {
        try {

            CompanyUserConfig companyUserConfigSelect = new CompanyUserConfig();
            companyUserConfigSelect.setCompanyId(companyId);
            companyUserConfigSelect.setUserId(userId);
            companyUserConfigSelect.setType("2");
            CompanyUserConfig rec = companyUserConfigMapper.selectOne(companyUserConfigSelect);
            JSONArray array = new JSONArray();
            if (null != rec) {
                Object value = rec.getValue();
                array = JSONUtil.parseArray(value);
                array.add(orderId);
                rec.setValue(JSONUtil.toJsonStr(array));
                int i = companyUserConfigMapper.updateByPrimaryKeySelectiveJsonP(rec);
            } else {
                CompanyUserConfig companyUserConfig = new CompanyUserConfig();
                companyUserConfig.setCompanyId(companyId);
                companyUserConfig.setUserId(userId);
                array.add(orderId);
                companyUserConfig.setValue(JSONUtil.toJsonStr(array));
                companyUserConfig.setType("2");
                companyUserConfig.setId(snowFlakeUtil.getNextId());
                int i = companyUserConfigMapper.insertSelectiveJsonP(companyUserConfig);
            }

            redisUtil.delete(REDIS_KEY_COMPANY_USER_CONFIG_UNPAID_ORDERS + userId);
            return true;
        } catch (Exception e) {
            log.error("addUnpaid ===> 异常{}", e);
            return false;
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getUserInfoByUserId(Long userId) throws Exception {
        RequestGetUserBaseInfoDto requestGetUserBaseInfoDto = new RequestGetUserBaseInfoDto();
        requestGetUserBaseInfoDto.setUserId(userId);
        BaseResponse resp = userFeign.getUserBaseInfo(requestGetUserBaseInfoDto);
        if (resp.getCode() != 0) {
            throw new BusinessException(resp.getMessage());
        }
        JSONObject userObject = new JSONObject(resp.getData());
        return userObject;
    }


    /**
     * 获取用户基础信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public UserBaseInfoDto getUserBaseInfoDtoByUserId(Long userId) throws Exception {
        RequestGetUserBaseInfoDto requestGetUserBaseInfoDto = new RequestGetUserBaseInfoDto();
        requestGetUserBaseInfoDto.setUserId(userId);
        BaseResponse resp = userFeign.getUserBaseInfo(requestGetUserBaseInfoDto);
        if (resp.getCode() != 0) {
            throw new BusinessException(resp.getMessage());
        }
        return JSONUtil.toBean(JSONUtil.toJsonStr(resp.getData()),UserBaseInfoDto.class);
    }



    /**
     * 添加用户十分钟内取消的车牌号
     *
     * @param userId
     * @param vehicleNo
     * @return
     */
    @Override
    public Boolean addCancelVehicleno(Long userId, String vehicleNo) {
        try {
            if (StringUtils.isBlank(vehicleNo)) {
                return false;
            }
            String key = UserConstant.USER_ORDER_CANCEL_VEHICLENOS;
            String jsonStr = JSONUtil.toJsonStr(redisUtil.hashGet(key, userId.toString()));
            List<String> list = JSONUtil.toList(jsonStr, String.class);
            list.add(vehicleNo);
            list = list.stream().distinct().collect(Collectors.toList());
            boolean result = redisUtil.hashPut(key, userId.toString(), list, CacheConsts.TEN_MINUTE);
            if (result) {
                String userKey = key + ":" + userId + ":" + vehicleNo;
                result = redisUtil.set(userKey, vehicleNo, CacheConsts.TEN_MINUTE);
            }
            return result;
        } catch (Exception e) {
            log.error("addCancelVehicleno ===> 异常：{}", e.toString());
            return false;
        }
    }

    /**
     * 获取用户十分钟内取消的车牌号 多个逗号分割，方便下单调用core使用
     */
    @Override
    public String getCancelVehicleno(Long userId) {
        try {
            StringBuilder result = new StringBuilder();
            String key = UserConstant.USER_ORDER_CANCEL_VEHICLENOS;
            String jsonStr = JSONUtil.toJsonStr(redisUtil.hashGet(key, userId.toString()));
            List<String> list = JSONUtil.toList(jsonStr, String.class);
            list = list.stream().distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                return "";
            }
            List<String> rmList = new ArrayList<>();
            for (String vehicleNo : list) {
                String userKey = key + ":" + userId + ":" + vehicleNo;
                if (redisUtil.hasKey(userKey)) {
                    result.append(vehicleNo);
                    result.append(",");
                } else {
                    rmList.add(vehicleNo);
                }
            }
            list.removeAll(rmList);
            redisUtil.hashPut(key, userId.toString(), list, CacheConsts.TEN_MINUTE);
            return result.toString().replaceFirst(",$", "");
        } catch (Exception e) {
            log.error("getCancelVehicleno ===> 异常：{}", e.toString());
            return "";
        }
    }

    /**
     * 判断用户是否使用英语
     *
     * @param companyId 公司id
     * @param userId    用户id
     * @return
     */
    @Override
    public boolean isEnglishLanguage(Long companyId, Long userId) {
        CacheUserInfo userInfo = cacheService.getUserInfo(companyId, userId);
        if (null == userInfo) {
            return false;
        }

        if (null != userInfo.getLanguage() && userInfo.getLanguage() == (short) 2) {
            return true;
        }
        return false;
    }


    /**
     * 获取用户推荐上车地点
     * <p>
     * 说明：
     * A类点： 腾讯地图反馈点
     * B类点： 人工标注点
     * C类点： 常用乘车点
     *
     * @param userId 用户id
     * @param lng    经度
     * @param lat    纬度
     * @return
     */
    @Override
    public List<RecommendedLocationInfoVo> getRecommendedLocation(Long companyId, Long userId, String lng, String lat, String recommendType, Integer count) {
        if (StrUtil.isEmpty(recommendType)) {
            recommendType = "1";
        }
        if (ObjectUtil.isEmpty(count)) {
            count = 0;
        }
        List<RecommendedLocationInfoVo> result = new ArrayList<>();
        RecommendedLocationConfigVo recommendedLocationConfig = this.getCompanyConfigRecommendedLocation(companyId, recommendType);
        if (ObjectUtil.isEmpty(recommendedLocationConfig)) {
            return result;
        }

        result = getRecommendedLocationInfoLogic(companyId, userId, lng, lat, recommendedLocationConfig, count);

        return result;
    }


    private RecommendedLocationConfigVo getCompanyConfigRecommendedLocation(Long companyId, String recommendType) {
        String recommendConfigStr = orderLimitService.getRedisCacheValueByType(
                OrderLimitConstant.COMPANY_CONFIG_RECOMMENDED_LOCATION,
                companyId);

        if (ObjectUtil.isEmpty(recommendConfigStr)) {
            recommendConfigStr = orderLimitService.getRedisCacheValueByType(
                    OrderLimitConstant.COMPANY_CONFIG_RECOMMENDED_LOCATION,
                    0L);
            if (ObjectUtil.isEmpty(recommendConfigStr)) {
                List<RecommendedLocationConfigVo> temp = new ArrayList<>();
                temp.add(new RecommendedLocationConfigVo(1, 1, 100, null, null));
                temp.add(new RecommendedLocationConfigVo(2, 2, 10, 5, 2));
                temp.add(new RecommendedLocationConfigVo(3, 3, 100, null, null));
                temp.add(new RecommendedLocationConfigVo(4, 4, 100, null, null));
                recommendConfigStr = JSONUtil.toJsonStr(temp);
            }
        }

        List<RecommendedLocationConfigVo> list = JSONUtil.toList(new JSONArray(recommendConfigStr), RecommendedLocationConfigVo.class);


        if (CollectionUtils.isNotEmpty(list)) {
            for (RecommendedLocationConfigVo vo : list) {
                if (StringUtils.equals(recommendType, String.valueOf(vo.getType()))) {
                    return vo;
                }
            }
        }

        return null;
    }

    /**
     * 吸附逻辑1
     *
     * @param companyId
     * @param userId
     * @param lng
     * @param lat
     * @param recommendedLocationConfig
     * @param count
     * @return
     */
    private List<RecommendedLocationInfoVo> getRecommendedLocationInfoLogic(Long companyId, Long userId, String lng, String lat, RecommendedLocationConfigVo recommendedLocationConfig, Integer count) {
        // 获取距离限制半径
//        int radius = this.getCompanyConfigRecommendedLocationRadius(companyId);
        int radius = recommendedLocationConfig.getRadius();

        // 根据配置的半径获取各类点数据
        //A类点
        List<RecommendedLocationInfoVo> mapLocation = this.getFilterMapLocation(radius, lng, lat, companyId);
        //B类点
        List<RecommendedLocationInfoVo> companyLocation = this.getFilterCompanyLocation(radius, companyId, lng, lat);
        //C类点
        List<RecommendedLocationInfoVo> userLocation = this.getFilterUserLocation(radius, companyId, userId, lng, lat);

        List<RecommendedLocationInfoVo> result = new ArrayList<>();

        // 根据当前配置的方案执行对应的吸附逻辑

        switch (recommendedLocationConfig.getLogic()) {
            case 1: // 逻辑一
                result = getRecommendedLocationInfoByLogicOne(companyId, userId, lng, lat, mapLocation, companyLocation, userLocation);
                break;
            case 2: // 逻辑二
                result = getRecommendedLocationInfoByLogicTwo(lng, lat,mapLocation, companyLocation, userLocation, count,
                        recommendedLocationConfig.getDistance(), recommendedLocationConfig.getCount());
                break;
            case 3: // 逻辑三
                result.addAll(mapLocation);
                result.addAll(companyLocation);
                result.addAll(userLocation);
                break;
            case 4: // 逻辑四
                result.addAll(mapLocation);
                result.addAll(companyLocation);
                result.addAll(userLocation);
                break;
        }

        return result;
    }

    /**
     *  通过逻辑二获取推荐点
     * @param lng
     * @param lat
     * @param mapLocation A 类点
     * @param companyLocation B 类点
     * @param userLocation C 类点
     * @param currentCount 当前拖动地图的次数
     * @param distance 设置成吸附点距离
     * @param count 拖动地图次数
     * @return
     */
    private List<RecommendedLocationInfoVo> getRecommendedLocationInfoByLogicTwo(String lng, String lat,
                                                                                 List<RecommendedLocationInfoVo> mapLocation,
                                                                                 List<RecommendedLocationInfoVo> companyLocation,
                                                                                 List<RecommendedLocationInfoVo> userLocation,
                                                                                 Integer currentCount, Integer distance, Integer count) {
        log.info("通过逻辑二获取推荐点");
        List<RecommendedLocationInfoVo> result = new ArrayList<>();
        result.addAll(mapLocation);
        result.addAll(companyLocation);
        result.addAll(userLocation);
        // 当前拖动地图的次数在配置的次数内, 可以设置吸附点
        if (currentCount < count) {
            result = result.stream().map(item -> {
                if (ObjectUtil.isEmpty(item.getDistance())){
                    item.setDistance((int) CoordinateUtil.distance(lng, lat, item.getLocation().getLng(), item.getLocation().getLat()));
                }
                // 当前点的距离在规定范围内设置成吸附
                if (item.getDistance() < distance) {
                    item.setAdsorb(true);
                }
                return item;
            }).sorted(Comparator.comparingInt(RecommendedLocationInfoVo::getDistance)).collect(Collectors.toList());
        }
        return result;
    }

    private List<RecommendedLocationInfoVo> getRecommendedLocationInfoByLogicOne(Long companyId, Long userId, String lng, String lat, List<RecommendedLocationInfoVo> mapLocation, List<RecommendedLocationInfoVo> companyLocation, List<RecommendedLocationInfoVo> userLocation) {
        List<RecommendedLocationInfoVo> result = new ArrayList<>();
        //是否存在A类点
        boolean existLocationA = CollectionUtils.isNotEmpty(mapLocation) ? true : false;
        //是否存在B类点
        boolean existLocationB = CollectionUtils.isNotEmpty(companyLocation) ? true : false;
        //是否存在C类点
        boolean existLocationC = CollectionUtils.isNotEmpty(userLocation) ? true : false;

        if (existLocationC) {
            // 判断是否有多个C类点
            if (userLocation.size() > 1) {
                // 获取次数不同的数据个数
                long count = userLocation.stream().map(o -> o.getScore()).distinct().count();
                if (count > 1) {
                    // 次数不同
                    userLocation = userLocation.stream().sorted(Comparator.comparing(RecommendedLocationInfoVo::getScore, Comparator.nullsFirst(Integer::compareTo).reversed())).limit(1L).collect(Collectors.toList());
                    userLocation = this.getAdsorbPoint(userLocation);
                    result.addAll(userLocation);
                } else {
                    // 次数相同
                    userLocation = this.distinct(userLocation).stream().limit(1L).collect(Collectors.toList());
                    userLocation = this.getAdsorbPoint(userLocation);
                    result.addAll(userLocation);
                }
            } else {
                userLocation = this.getAdsorbPoint(userLocation);
                result.addAll(userLocation);
            }

            if (CollectionUtils.isNotEmpty(mapLocation)) {
                List<RecommendedLocationInfoVo> distinctMapLocation = new ArrayList<>();
                distinctMapLocation = this.distinct(mapLocation);
                distinctMapLocation = distinctMapLocation.stream().limit(1L).collect(Collectors.toList());
                result.addAll(distinctMapLocation);
            }
            if (CollectionUtils.isNotEmpty(companyLocation)) {
                List<RecommendedLocationInfoVo> distinctCompanyLocation = new ArrayList<>();
                distinctCompanyLocation = this.distinct(companyLocation);
                distinctCompanyLocation = distinctCompanyLocation.stream().limit(1L).collect(Collectors.toList());
                result.addAll(distinctCompanyLocation);
            }
            if (CollectionUtils.isNotEmpty(result)) {
                result = this.distinct(result);
            }
        } else {
            if (existLocationB) {
                result.addAll(companyLocation);
                result.addAll(mapLocation);
                result = this.distinct(result);
                List<RecommendedLocationInfoVo> a = result.stream().filter(o -> StringUtils.equals(o.getSource(), "A")).limit(2L).collect(Collectors.toList());
                List<RecommendedLocationInfoVo> b = result.stream().filter(o -> StringUtils.equals(o.getSource(), "B")).limit(1L).collect(Collectors.toList());
                result = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(b)) {
                    result.addAll(b);
                    result = this.getAdsorbPoint(b);
                }
                if (CollectionUtils.isNotEmpty(a)) {
                    result.addAll(a);
                }
            } else {
                if (existLocationA) {
                    result.addAll(mapLocation);
                    result = this.distinct(result);
                    result = this.customType1(result, lng, lat, companyId, userId);
                    result = result.stream().limit(3L).collect(Collectors.toList());
                }
            }
        }
        return result;
    }

    private List<RecommendedLocationInfoVo> customType1(List<RecommendedLocationInfoVo> result, String lng, String lat,Long companyId, Long userId) {
//        RedisCpolRegulationInfoVo orderLimitInfo = userService.getOrderLimitInfo(companyId, userId);
//        if(null == orderLimitInfo.getDefAddress()){
//            return result;
//        }


        RegulationBo regulationConfig = regulationService.getRegulationConfig();
        List<Long> recommendedLocationList = regulationConfig.getRecommendedLocationList();
        List<JSONObject> recommendedLocationConfigList = regulationConfig.getRecommendedLocationConfigList();
        if (CollectionUtils.isNotEmpty(recommendedLocationList) && CollectionUtils.isNotEmpty(recommendedLocationConfigList)) {
            JSONObject jsonObject = recommendedLocationConfigList.get(0);
            String typeConfig = jsonObject.getStr("type");
            Integer radiusConfig = jsonObject.getInt("radius");
            if (StringUtils.equals(typeConfig, "1")) {
                result.stream().filter(o ->
                        {
                            Integer distance = (int) CoordinateUtil.distance(lng, lat, o.getLocation().getLng(), o.getLocation().getLat());
                            if (distance <= radiusConfig) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                ).collect(Collectors.toList());
            }
        }

        if(CollectionUtils.isNotEmpty(result)){
            result.get(0).setAdsorb(true);
        }
        return result;
    }

    /**
     * 获取过滤后的腾讯地图反馈点列表
     */
    public List<RecommendedLocationInfoVo> getFilterMapLocation(int radius, String lng, String lat, Long companyId) {
        List<RecommendedLocationInfoVo> list = new ArrayList<>();
        //腾讯地图反馈点列表（地图服务附近地址）
        List<SearchRes> mapLocation = coreMapService.search(lng, lat ,companyId);
        if (CollectionUtils.isNotEmpty(mapLocation)) {
            list = mapLocation.stream().map(o -> {
                RecommendedLocationInfoVo recommendedLocationInfoVo = new RecommendedLocationInfoVo();
                Integer distance = (int)CoordinateUtil.distance(lng, lat, o.getLocation().getLng(), o.getLocation().getLat());
                if (distance <= radius) {
                    recommendedLocationInfoVo.setId(o.getId());
                    recommendedLocationInfoVo.setTitle(o.getTitle());
                    recommendedLocationInfoVo.setDistance(o.getDistance());
                    recommendedLocationInfoVo.setScore(0);
                    recommendedLocationInfoVo.setAdsorb(false);
                    recommendedLocationInfoVo.setSource("A");
                    RecommendedLocationInfoLocationVo location = new RecommendedLocationInfoLocationVo();
                    location.setLat(o.getLocation().getLat());
                    location.setLng(o.getLocation().getLng());
                    recommendedLocationInfoVo.setLocation(location);
                    return recommendedLocationInfoVo;
                } else {
                    return null;
                }

            }).filter(o -> null != o).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 获取过滤后的人工标注点列表
     */
    public List<RecommendedLocationInfoVo> getFilterCompanyLocation(int radius, Long companyId, String lng, String lat) {
        List<RecommendedLocationInfoVo> list = new ArrayList<>();
        //人工标注点列表(公司推荐上车点)
        List<CompanyLocations> companyLocation = companyService.getCompanyRecommendedLocationByCompanyId(companyId);
        if (CollectionUtils.isNotEmpty(companyLocation)) {
            list = companyLocation.stream().map(o -> {
                RecommendedLocationInfoVo recommendedLocationInfoVo = new RecommendedLocationInfoVo();
                //Integer distance = coreMapService.getdistance(lng, lat, o.getGpsLng().toString(), o.getGpsLat().toString());
                Integer distance = (int)CoordinateUtil.distance(lng, lat, o.getGpsLng().toString(), o.getGpsLat().toString());
                recommendedLocationInfoVo.setId(o.getId().toString());
                recommendedLocationInfoVo.setTitle(o.getName());
                recommendedLocationInfoVo.setDistance(distance);
                recommendedLocationInfoVo.setScore(0);
                recommendedLocationInfoVo.setAdsorb(false);
                recommendedLocationInfoVo.setSource("B");
                RecommendedLocationInfoLocationVo location = new RecommendedLocationInfoLocationVo();
                location.setLng(o.getGpsLng().toString());
                location.setLat(o.getGpsLat().toString());
                recommendedLocationInfoVo.setLocation(location);
                return recommendedLocationInfoVo;
            }).filter(o -> o.getDistance() != null && o.getDistance() <= radius).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 获取过滤后的常用乘车点列表
     */
    public List<RecommendedLocationInfoVo> getFilterUserLocation(int radius, Long companyId, Long userId, String lng, String lat) {
        List<RecommendedLocationInfoVo> list = new ArrayList<>();
        List<OrderPlaceHistoryMonthCount> userLocation = new ArrayList<>();
        //常用乘车点列表（一个月内下单 出发地址）
        List<OrderPlaceHistoryMonthCount> userPickupLocation = orderPlaceHistoryMonthMapper.selectPickupCountInfoByuserId(userId);
        userLocation.addAll(userPickupLocation);

        //常用乘车点列表（一个月内下单 到达地址）
        List<OrderPlaceHistoryMonthCount> userDestLocation = orderPlaceHistoryMonthMapper.selectDestCountInfoByuserId(userId);
        userLocation.addAll(userDestLocation);

        Map<String, OrderPlaceHistoryMonthCount> collect = userLocation.stream()
                .collect(Collectors.toMap(
                        OrderPlaceHistoryMonthCount::getPickupLocationName,
                        Function.identity(),
                        (o1, o2) -> {
                            if (o1.getId().compareTo(o2.getId()) < 0) {
                                // 如果当前的o1的ID小于o2的ID，用o2替换o1，并累加count
                                o2.setCount(o1.getCount() + o2.getCount());
                                return o2; // 返回o2，因为它有更大的ID或保持了原ID最大记录的其他信息
                            } else {
                                // 否则，保持o1，因为它已经有较大的ID或这里保持不变
                                o1.setCount(o1.getCount() + o2.getCount());
                                return o1;
                            }
                        },
                        HashMap::new)); // 或者使用你想要的Map实现类

        userLocation = collect.values().stream().collect(Collectors.toList());



        list = getRecommendedLocationInfoVos(2,radius, companyId, lng, lat, userLocation);

        return list;
    }

    private List<RecommendedLocationInfoVo> getRecommendedLocationInfoVos(int score,int radius, Long companyId, String lng, String lat, List<OrderPlaceHistoryMonthCount> userLocation) {
        List<RecommendedLocationInfoVo> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userLocation)) {
            list = userLocation.stream().map(o -> {
                RecommendedLocationInfoVo recommendedLocationInfoVo = new RecommendedLocationInfoVo();
                //Integer distance = coreMapService.getdistance(lng, lat, o.getDepartLng(), o.getDepartLat());
                Integer distance = (int)CoordinateUtil.distance(lng, lat, o.getDepartLng(), o.getDepartLat());
                recommendedLocationInfoVo.setId(o.getUserId().toString());
                recommendedLocationInfoVo.setTitle(cipherService.addressDecrypt(companyId, o.getPickupLocationName()));
                recommendedLocationInfoVo.setDistance(distance);
                recommendedLocationInfoVo.setAdsorb(false);
                recommendedLocationInfoVo.setSource("C");
                RecommendedLocationInfoLocationVo location = new RecommendedLocationInfoLocationVo();
                location.setLng(o.getDepartLng());
                location.setLat(o.getDepartLat());
                recommendedLocationInfoVo.setLocation(location);
                recommendedLocationInfoVo.setScore(o.getCount());
                return recommendedLocationInfoVo;
            }).filter(o -> o.getDistance() != null && o.getDistance() <= radius && o.getScore() >= score).distinct().collect(Collectors.toList());
        }




        return list;
    }

    /**
     * 推荐地址去重复排序
     */
    private List<RecommendedLocationInfoVo> distinct(List<RecommendedLocationInfoVo> list) {
        List<String> listTempTitle = new ArrayList<>();
        List<String> listTempLocation = new ArrayList<>();

        list = list.stream().map(o -> {
                    DecimalFormat df = new DecimalFormat("0.000000");
                    String lng = df.format(Double.valueOf(o.getLocation().getLng()));
                    String lat = df.format(Double.valueOf(o.getLocation().getLat()));
                    boolean contains = listTempLocation.contains(lng + lat);
                    listTempLocation.add(lng + lat);
                    if (contains) {
                        return null;
                    } else {
                        return o;
                    }
                }
        ).filter(o -> null != o).collect(Collectors.toList());

        list = list.stream().map(o -> {
                    boolean contains = listTempTitle.contains(o.getTitle());
                    listTempTitle.add(o.getTitle());
                    if (contains) {
                        return null;
                    } else {
                        return o;
                    }
                }
        ).filter(o -> null != o).collect(Collectors.toList());

        list = list.stream().sorted(Comparator
                // 距离升序（由于是升序，nullsFirst()方法会将null值放在前面）
                .comparing(RecommendedLocationInfoVo::getDistance, Comparator.nullsFirst(Integer::compareTo))
        ).collect(Collectors.toList());

        return list;
    }

    /**
     * 设置吸附点
     *
     * @param list
     */
    public List<RecommendedLocationInfoVo> getAdsorbPoint(List<RecommendedLocationInfoVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        RecommendedLocationInfoVo recommendedLocationInfoVo = list.get(0);
        recommendedLocationInfoVo.setAdsorb(true);
        list.set(0, recommendedLocationInfoVo);
        return list;
    }

    @Override
    public void deleteOrderLimitInfoDefAddress(Long companyId, Long userId, String departLat, String departLng) {
        RedisCpol redisCpol = orderLimitService.getRedisCpol(companyId, userId);
        if (null != redisCpol) {
            RedisCpolRegulationInfo regulationInfo = redisCpol.getRegulationInfo();
            if (null != regulationInfo) {
                if (null != regulationInfo.getDefAddress() && (null == regulationInfo.getDefAddress().getIsConfirm() || !regulationInfo.getDefAddress().getIsConfirm())) {
                    Object o = redisUtil.hashGet(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString());
                    JSONObject jsonObject = new JSONObject(o);
                    jsonObject.getJSONObject("regulationInfo").getJSONObject("defAddress").set("destOld",regulationInfo.getDefAddress().getDest());
                    jsonObject.getJSONObject("regulationInfo").getJSONObject("defAddress").set("dest",null);
                    redisUtil.hashPut(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString(),jsonObject.toString(),CacheConsts.ONE_DAY);
                }else {
                    Object o = redisUtil.hashGet(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString());
                    JSONObject jsonObject = new JSONObject(o);
                    RedisCpolRegulationInfoDefAddress redisCpolRegulationInfoDefAddress = new RedisCpolRegulationInfoDefAddress();
                    redisCpolRegulationInfoDefAddress.setIsConfirm(false);
                    RedisCpolRegulationInfoDefAddressPoint redisCpolRegulationInfoDefAddressPoint = new RedisCpolRegulationInfoDefAddressPoint();
                    redisCpolRegulationInfoDefAddressPoint.setLat(departLat);
                    redisCpolRegulationInfoDefAddressPoint.setLon(departLng);
                    redisCpolRegulationInfoDefAddress.setDepart(redisCpolRegulationInfoDefAddressPoint);
                    jsonObject.getJSONObject("regulationInfo").set("defAddress",redisCpolRegulationInfoDefAddress);
                    if(null != regulationInfo.getDefAddress() && null != regulationInfo.getDefAddress().getIsConfirm()){
                        jsonObject.getJSONObject("regulationInfo").getJSONObject("defAddress").set("isConfirm",regulationInfo.getDefAddress().getIsConfirm());
                    }
                    redisUtil.hashPut(CacheConsts.REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT + companyId, userId.toString(),jsonObject.toString(),CacheConsts.ONE_DAY);

                }
            }
        }
    }

    /**
     * 添加用户提示信息
     * @param userId
     * @param message
     * @return
     */
    @Override
    public boolean addUserMessage(Long userId, String message){
        String key = UserConstant.USER_ORDER_MESSAGES;
        boolean b = redisUtil.hashPut(key, userId.toString(), message, CacheConsts.ORDER_CACHE_EXPIRE_TIME);
        return b;
    }

    @Override
    public void saveUserPassenger(Long userId, String passengerName) {
        try {
            String key = UserConstant.USER_LATELY_PASSENGER;
            if (redisUtil.hashHasKey(key, userId.toString())) {
    //            List<String> list = (List<String>) redisUtil.hashGet(key, userId.toString());
                List<String> list = JSONUtil.toList(JSONUtil.parseArray(redisUtil.hashGet(key, userId.toString())), String.class);
                boolean contains = list.contains(passengerName);
                if (CollectionUtils.isNotEmpty(list) && list.size() > 29) {
                    if(!contains){
                        list.remove(29);
                    }
                }
                if(contains){
                    list.remove(passengerName);
                }
                list.add(0,passengerName);
                redisUtil.hashPut(key, userId.toString(), list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(passengerName);
                redisUtil.hashPut(key, userId.toString(), list);
            }
        } catch (Exception e) {
            log.info("saveUserPassenger异常: ", e);
        }
    }
}