package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 预付订单 下单失败 通知bill-core-service的消息体
 */
@Data
public class RequestPrepayOrderExceptionBillNotifyDto extends RequestBillNotifyBaseDto{
}
