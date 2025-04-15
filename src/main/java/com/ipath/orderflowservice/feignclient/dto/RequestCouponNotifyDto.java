package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;

/**
 * 通知coupon-core-service基类
 */
@Data
public class RequestCouponNotifyDto {
    /**
     * 事件类型
     * 1,下单成功  2,司机已接单  3,司机到达 5,行程结束  6,订单完成  7,订单取消
     */
    private Short eventType;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 优惠券id列表
     */
    private List<Long> couponIds;
}
