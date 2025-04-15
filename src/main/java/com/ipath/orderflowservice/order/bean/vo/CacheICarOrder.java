package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

// 缓存到redis中的需要监视的订单信息
@Data
public class CacheICarOrder {
    private Long orderId;
    private String coreOrderId;
    private Long userId;
    private Short state;
    private Boolean needMonitor;
    private Boolean isUpgrade;
    private Long companyId;
}
