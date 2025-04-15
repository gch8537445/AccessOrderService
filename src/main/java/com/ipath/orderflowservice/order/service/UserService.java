package com.ipath.orderflowservice.order.service;

import cn.hutool.json.JSONObject;
import com.ipath.orderflowservice.feignclient.dto.UserBaseInfoDto;
import com.ipath.orderflowservice.order.bean.RedisCpolRegulationInfoDefAddress;
import com.ipath.orderflowservice.order.bean.RedisCpolRegulationInfoVo;
import com.ipath.orderflowservice.order.bean.vo.RecommendedLocationInfoVo;

import java.util.List;

/**
 * 用户 Service
 */
public interface UserService {

    boolean existUnpaid(Long userId, Long companyId) throws Exception;

    boolean existUserCheck(Long userId, Long companyId) ;

    boolean addUnpaid(Long userId, Long companyId, Long orderId) throws Exception;

    JSONObject getUserInfoByUserId(Long userId) throws Exception;

    UserBaseInfoDto getUserBaseInfoDtoByUserId(Long userId) throws Exception;

    Boolean addCancelVehicleno(Long userId, String vehicleNo);

    String getCancelVehicleno(Long userId);

    boolean isEnglishLanguage(Long companyId, Long userId);

    List<RecommendedLocationInfoVo> getRecommendedLocation(Long companyId, Long userId, String lat, String lng, String recommendType,Integer count);

    void deleteOrderLimitInfoDefAddress(Long companyId, Long userId, String departLat, String departLng);

    boolean addUserMessage(Long userId, String message);

    void saveUserPassenger(Long userId,String passengerName);
}
