package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestReportNotifyBaseDto {

    /**
     * 订单主键
     */
    private Long id;

    /**
     * 订单状态
     */
    private Short status;

    /**
     * 订单子状态
     */
    private Short subStatus;
}
