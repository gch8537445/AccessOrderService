package com.ipath.orderflowservice.feignclient.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 场景车型报销政策
 */
@Data
public class ReimModel {
    Integer carLevel;    // 车型
    Short reimModel;     // 报销模式 1:按金额 2:按比例
    BigDecimal value;    // 报销比例或金额
}
