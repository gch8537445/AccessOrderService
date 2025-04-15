package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RequestUsageStateDto {
    private Long orderId;//订单主键
    private Long preDepartApplyId; // 业务的id（订单号）
    private Long companyId;//公司主键
    private BigDecimal usageMoney;//使用金额
    private Date usageTime;//使用时间
    private int state;//状态 0：未使用 1：占用 2：已使用
}
