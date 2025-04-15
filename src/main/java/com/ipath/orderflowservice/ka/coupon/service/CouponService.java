package com.ipath.orderflowservice.ka.coupon.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.orderflowservice.feignclient.BillFeign;
import com.ipath.orderflowservice.feignclient.ConfigurationFeign;
import com.ipath.orderflowservice.feignclient.CouponConsumeFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestChangeCouponState;
import com.ipath.orderflowservice.feignclient.dto.RequestSceneInfoDto;
import com.ipath.orderflowservice.feignclient.dto.RequestUserCouponDto;
import com.ipath.orderflowservice.ka.configuration.service.ConfigurationService;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.CouponResult;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ka端优惠券服务
 */
@Service
@Slf4j
public class CouponService {

    @Autowired
    private CouponConsumeFeign couponConsumeFeign;
    @Autowired
    private LogService logService;

    /**
     * 获取用户可用的优惠券
     */
    public List<CouponResult> getUserCoupons(Long companyId, Long userId, Date orderTime, String cityCode,
            String traceId) {
        Log activityLog = logService.getLog(companyId, userId, null, InterfaceEnum.COUPON_GET_COUPONS_OF_USER, traceId);
        long startTime = System.currentTimeMillis();

        List<CouponResult> availableCoupons = new ArrayList<>();
        RequestUserCouponDto requestUserCouponDto = new RequestUserCouponDto();
        requestUserCouponDto.setCompanyId(companyId);
        requestUserCouponDto.setUserId(userId);
        activityLog.setBody(JSONUtil.toJsonStr(requestUserCouponDto));
        BaseResponse baseResponse = null;
        try {
            baseResponse = couponConsumeFeign.getUserCoupons(requestUserCouponDto);

            long endTime = System.currentTimeMillis();

            if (baseResponse.getCode() == 0) {
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse.getData()));

                JSONArray couponArray = JSONUtil.parseArray(baseResponse.getData());
                for (int i = 0; i < couponArray.size(); i++) {
                    Date validFrom = null;
                    Date validTo = null;
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

                        if (null == orderTime) {
                            orderTime = new Date();
                        }

                        if (validFrom != null && validTo != null) {
                            if (DateUtil.compare(validFrom, orderTime) > 0
                                    || DateUtil.compare(validTo, orderTime) < 0) {
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
                            if (!cityCodes.contains(cityCode)) {
                                continue;
                            }
                        } else if ("exclude".equals(cityMode)) {
                            if (cityCodes.contains(cityCode)) {
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
            } else {
                log.error(
                        "调用优惠券接口出现异常【CouponService->getUserCoupons.couponconsume.GetCouponsOfUser,companyId:{}, userId:{}, orderTime:{}, cityCode:{}】",
                        companyId, userId, orderTime, cityCode, new Exception(JSONUtil.toJsonStr(baseResponse)));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("调用优惠券接口出现异常【CouponService->getUserCoupons,companyId:{}, userId:{}, orderTime:{}, cityCode:{}】",
                    companyId, userId, orderTime, cityCode, ex);
        }

        return availableCoupons;
    }

    public void changeCouponState(List<Long> couponIds, Integer couponStatus, Long companyId, Long userId,
            Long orderId) {
        Log activityLog = logService.getLog(companyId, userId, orderId, InterfaceEnum.CHANGE_COUPON_STATUS);
        long startTime = System.currentTimeMillis();

        try {
            RequestChangeCouponState requestChangeCouponState = new RequestChangeCouponState();
            requestChangeCouponState.setCouponIds(couponIds);
            requestChangeCouponState.setCouponState(1);

            activityLog.setBody(JSONUtil.toJsonStr(requestChangeCouponState));

            BaseResponse baseResponse = couponConsumeFeign.changeCouponState(requestChangeCouponState);

            long endTime = System.currentTimeMillis();

            if (baseResponse.getCode() != 0) {
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveErrorLogAsync(activityLog, new Exception(baseResponse.getMessage()));
                log.error(
                        "调用修改优惠券状态接口出现异常【CouponService->changeCouponState.couponconsume.ChangeCouponState,companyId:{}, userId:{}, couponStatus:{}, couponIds:{}】",
                        companyId, userId, couponStatus, StrUtil.join(",", couponIds),
                        new Exception(baseResponse.getMessage()));
            } else {
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse.getMessage()));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error(
                    "修改优惠券状态出现异常【CouponService->changeCouponState,companyId:{}, userId:{}, couponStatus:{}, couponIds:{}】",
                    companyId, userId, couponStatus, StrUtil.join(",", couponIds), ex);
        }
    }
}
