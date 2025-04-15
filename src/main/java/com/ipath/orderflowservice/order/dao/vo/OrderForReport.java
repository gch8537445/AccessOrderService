package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderForReport{
    private Short serviceType;
    private Long accountId;
    private String departCityName; 
    private String destCityName;
    private Boolean isPrepay;
    private Boolean isUpgrade;
    private Long projectId;
    private String projectName;
    private String useCarReason;
    private Long sceneId;
    private Long scenePublishId;
    private String sceneCode;
    private String sceneName;
    private String customInfo;
    private String name;
    private String phone;
    private String emergencyPhone;
    private String passengerName;
    private BigDecimal estimatePrice;
    private Integer estimateDistance;
    private Integer estimateTime;
    private Integer takeDistance;
    private Integer takeTime;
    private String partnerOrderId;
    private Boolean isUserPay;
    private String isNotUserPayReason;
    private String passengerPhoneVirtual;
    private String userPhoneVirtual;
    private BigDecimal ipathEstimatePrice;
    private BigDecimal platformEstimatePrice;

}
