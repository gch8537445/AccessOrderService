package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 中台返回的预估结果数据结构
 */
@Data
public class EstimatePriceResult {
    private BigDecimal estimatePrice; // 运力平台报价
    private String dynamicCode;       // 运力平台动态码,下单时用到,需要原样回传给运力平台
    private Integer carSource;        // 运力平台代码
    private String carSourceName;     // 运力平台名称
    private String carSourceImg;      // 运力平台logo地址
    private Integer carType;          // 车等级
    private String carTypeName;       // 车等级名称
    private BigDecimal estimateDistance;          // 预估里程
    private BigDecimal estimateTravelTime;          // 预估时间
    private String ipathCode;           //途径定义运力code
    private String subCarType;
    private Boolean isFixed;    // 默认false


    //自定义
    private Boolean selfPayingUpgradeCarType;
    private String selfPayingRate;
    private Long selfPayingAmount;
}
