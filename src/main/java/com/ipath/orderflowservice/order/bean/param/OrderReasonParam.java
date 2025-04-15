package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class OrderReasonParam {
    private Long companyId;
    private Long userId;
    private Long orderId;
    private String reason;
}
