package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPay {
    private Long orderId;
    private BigDecimal payAmount;
}
