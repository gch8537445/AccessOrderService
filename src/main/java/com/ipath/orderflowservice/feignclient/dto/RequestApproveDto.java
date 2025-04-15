package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestApproveDto {
    /**
     * 是否是管理员审核
     */
    private Boolean isAdminApproval;
    private Long approverUserId;
    private Long businessId; // 业务的id（订单号）
    private Long sceneId;
    private String comment;
}
