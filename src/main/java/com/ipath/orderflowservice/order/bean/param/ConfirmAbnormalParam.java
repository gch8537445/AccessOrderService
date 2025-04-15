package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class ConfirmAbnormalParam {
    private Long userId;
    private Long orderId;
    private String comment;
}
