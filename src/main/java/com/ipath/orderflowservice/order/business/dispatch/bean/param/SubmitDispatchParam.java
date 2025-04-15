package com.ipath.orderflowservice.order.business.dispatch.bean.param;

import lombok.Data;

@Data
public class SubmitDispatchParam {

    private Long userId;
    private Long companyId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 提交选项
     * 0:我再等等  1：加价调度
     */
    private String submitType;
}
