package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

@Data
public class RiskTag {
    private Integer riskCode;  //风险场景码
    private String riskDesp;  //风险场景描述
}
