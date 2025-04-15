package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestSubmitApplyDto {
    private Long userId;
    private Long companyId;
    private Long processDefineId;
    private Long businessId; // 业务的id（订单号）
    private Long sceneId;
}
