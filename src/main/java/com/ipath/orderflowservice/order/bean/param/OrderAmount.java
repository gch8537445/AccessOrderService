package com.ipath.orderflowservice.order.bean.param;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderAmount {
    private Long id;  // orderId
    private BigDecimal amount;
}
