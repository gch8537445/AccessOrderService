package com.ipath.orderflowservice.feignclient.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RequestAppendPlaceOrderForReportDto {
    private Long orderId;
    private Long companyId;
    private List<Long> couponIds;
    private Boolean isPrePay;
    private Boolean isUpgrade;
    private BigDecimal additionalEstimatePrice;
    private String customInfo;
}
