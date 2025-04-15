package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class UsageRecordDto {
    private Long userId;
    private Long companyId;
    private Long accountId;
    private Long orderId;
    private String upgradLevel;//升舱车型
}
