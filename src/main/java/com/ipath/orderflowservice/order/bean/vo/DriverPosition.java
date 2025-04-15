package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

@Data
public class DriverPosition {
    private Long orderId;
    private String coreOrderId;
    private String start;
    private String stop;
}
