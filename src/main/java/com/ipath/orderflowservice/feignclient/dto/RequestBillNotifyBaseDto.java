package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 通知bill-core-service基类
 */
@Data
public class RequestBillNotifyBaseDto {
    /**
     * 事件类型
     * 1,下单成功  2,司机已接单  3,司机到达 5,行程结束  6,订单完成  7,订单取消
     */
    private Short eventType;

    /**
     * 订单id
     */
    private Long orderId;
}
