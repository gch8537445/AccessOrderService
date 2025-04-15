package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestNotifyReportForCompletionDto {
    
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 中台订单id
     */
    private String coreOrderId;
    
    /**
     * 用户appid
     */
    private String socialAppUserId;

    /**
     * 自定义信息
     */
    private String customInfo;

    /**
     * 是否是异常订单
     */
    private Boolean isAbnormal;

    /**
     * 备注字段1
     * 微众 异常订单
     */
    private String backupField01;

    /**
     * 备注字段2
     * 微众 异常订单原因
     */
    private String backupField02;
}
