package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于缓存和下发的车型估价结果
 */
@Data
public class EstimateCar implements Serializable {
    private BigDecimal estimatePrice; // 扣减优惠券后的预估价
    private BigDecimal actualEstimatePrice;//运力平台预估价
    private String dynamicCode;     // 运力平台动态码,下单时用到,需要原样回传给运力平台
    private String carSource;       // 运力平台名称
    private String carSourceImg;    // 运力平台logo地址
    private Integer carSourceId;    // 运力平台代码
    private Integer carLevel;       // 车等级
    private String carLevelName;    // 车等级名称
    private String couponId;          //优惠券主键
    private BigDecimal couponAmount;//优惠券金额
    private BigDecimal estimateTime; //预估时间
    private BigDecimal estimateDistance; //预估距离
    private String ipathCode;           //途径运力code
    private String subCarType;
    private String labelCode;
    private String labelName;
    private Boolean isFixed;    // 默认false
    private Boolean checked; // 是否选中
    private Boolean cancel; //是否为可以取消勾选运力

    //自定义
    private Boolean selfPayingUpgradeCarType;
    private String selfPayingRate;
    private Long selfPayingAmount;
}
