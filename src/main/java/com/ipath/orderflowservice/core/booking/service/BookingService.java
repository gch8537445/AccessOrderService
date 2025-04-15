package com.ipath.orderflowservice.core.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;

/**
 * 预约管家 Service
 */
public interface BookingService {

    /**
     * 开始服务
     * @param param
     * @param orderId
     * @param coreOrderId
     * @param estimateDistance
     * @param businessType
     */
    void startService(CreateOrderParam param, Long orderId, String coreOrderId, int estimateDistance, int businessType) throws JsonProcessingException;

    /**
     * 取消订单
     * @param orderId
     * @param cancelReason
     */
    void cancelService(Long orderId, String cancelReason);

    /**
     * 状态变更
     * @param status
     * @param orderId
     */
    void changeStatus(Short status, Long orderId);
}
