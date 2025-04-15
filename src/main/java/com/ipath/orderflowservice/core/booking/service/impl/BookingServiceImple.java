package com.ipath.orderflowservice.core.booking.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.core.booking.service.BookingService;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.feignclient.dto.RequestBookingNotifyDto;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.service.CacheService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预约管家 Service
 */
@Service
@Slf4j
public class BookingServiceImple implements BookingService {

    @Autowired
    private LogService logService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    /**
     * 通知booking-service 开始服务
     * 
     * @throws JsonProcessingException
     */
    public void startService(CreateOrderParam param, Long orderId, String coreOrderId,
            int estimateDistance, int businessType) throws JsonProcessingException {
        RequestBookingNotifyDto bookingNotifyDto = new RequestBookingNotifyDto();
        BeanUtil.copyProperties(param, bookingNotifyDto);

        bookingNotifyDto.setOrderId(orderId);
        bookingNotifyDto.setCoreOrderId(coreOrderId);
        bookingNotifyDto.setBusinessType(businessType);
        bookingNotifyDto.setPlaceOrderPhone(param.getUserPhone());
        bookingNotifyDto.setDepartLocation(param.getPickupLocation());
        bookingNotifyDto.setDepartLocationName(param.getPickupLocationName());
        bookingNotifyDto.setEstimateDistance(new BigDecimal(estimateDistance).divide(new BigDecimal(1000)));
        bookingNotifyDto.setCarType((short) 0);// 下单时无法获取真正接单的车型，约定传0
        bookingNotifyDto.setTrainNumber(param.getTrainNumber());
        bookingNotifyDto.setTrainDep(param.getTrainDep());
        bookingNotifyDto.setTrainArr(param.getTrainArr());
        bookingNotifyDto.setTrainState(1);
        bookingNotifyDto.setTrainEstArrTime(param.getDepartTime());
        bookingNotifyDto.setTrainActArrTime(param.getDepartTime());

        CacheCompanyInfo companyInfo = cacheService.getCompanyInfo(param.getCompanyId());
        bookingNotifyDto.setCompanyName(null == companyInfo ? "" : companyInfo.getCompanyName());

        ObjectMapper objectMapper = new ObjectMapper();
        String estimateCarTypes = objectMapper.writeValueAsString(param.getCars());
        bookingNotifyDto.setEstimateCarTypes(estimateCarTypes);

        ObjectMapper contentMapper = new ObjectMapper();
        String content = contentMapper.writeValueAsString(bookingNotifyDto);

        Long startTime = System.currentTimeMillis();
        Log iLog = logService.getLog(param.getCompanyId(), param.getUserId(), orderId,
                InterfaceEnum.NOTIFY_BOOKING_START_SERVICE);
        iLog.setBody(content);

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/booking/StartService");
            remoteCallDto.setContent(content);
            BaseResponse response = remoteCallFeign.call(remoteCallDto);
            Long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);

            if (response.getCode() == 0) {
                logService.saveLogAsync(iLog, JSONUtil.toJsonStr(response));
            } else {
                logService.saveErrorLogAsync(iLog, JSONUtil.toJsonStr(response));
                log.error("调用预约管家接口出现异常【BookingService->startService.booking.StartService,para:{}】", content,
                        new Exception(JSONUtil.toJsonStr(response)));
            }
        } catch (Exception ex) {
            Long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, ex);
            log.error("调用预约管家接口出现异常【BookingService->startService,para:{}】", content, ex);
        }
    }

    /**
     * 通知booking-service 取消订单
     */
    public void cancelService(Long orderId, String cancelReason) {
        JSONObject paramObject = new JSONObject();
        paramObject.set("orderId", orderId);
        paramObject.set("cancleReason", cancelReason);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(null, null, orderId,
                InterfaceEnum.NOTIFY_BOOKING_CANCEL_ORDER);
        activityLog.setBody(JSONUtil.toJsonStr(paramObject));

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/booking/CancleService");
            remoteCallDto.setContent(paramObject.toString());
            BaseResponse response = remoteCallFeign.call(remoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            if (response.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
            } else {
                logService.saveErrorLogAsync(activityLog, response.getMessage());
                log.error("调用预约管家接口出现异常【BookingService->cancelService.booking.CancleService,para:{}】",
                        paramObject.toString(), new Exception(JSONUtil.toJsonStr(response)));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("调用预约管家接口出现异常【BookingService->cancelService,para:{}】", paramObject.toString(), ex);
        }
    }

    public void changeStatus(Short status, Long orderId) {

        JSONObject paramObject = new JSONObject();
        paramObject.set("orderId", orderId);
        paramObject.set("eventType", status);

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(null, null, orderId,
                InterfaceEnum.BOOKING_CALLBACK);
        activityLog.setBody(JSONUtil.toJsonStr(paramObject));

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            remoteCallDto.setPath("/api/v2/booking/CallBack");
            remoteCallDto.setContent(paramObject.toString());
            BaseResponse response = remoteCallFeign.call(remoteCallDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            if (response.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));
            } else {
                logService.saveErrorLogAsync(activityLog, response.getMessage());
                log.error("调用预约管家接口出现异常【BookingService->changeStatus.booking.CallBack,para:{}】", paramObject.toString(),
                        new Exception(JSONUtil.toJsonStr(response)));
            }
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, ex);
            log.error("调用预约管家接口出现异常【BookingService->changeStatus,para:{}】", paramObject.toString(), ex);
        }
    }
}
