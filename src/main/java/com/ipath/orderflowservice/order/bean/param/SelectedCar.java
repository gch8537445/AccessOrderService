package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;





@Data
public class SelectedCar {
    private Integer carLevel;
    private Integer carSourceId;
    private BigDecimal estimatePrice;
    private BigDecimal actualEstimatePrice;
    private String dynamicCode;
    private String carLevelName;
    private Long couponId;          //优惠券主键
    private BigDecimal couponAmount;//优惠券金额
    private Integer upgradeValue;
    private String newCarLevelName;
    private BigDecimal estimateTime;
    private BigDecimal estimateDistance;
    private String ipathCode;
    private Integer delay;//vip 免费升舱配置延迟时间
    private String labelCode;
    private String subCarType;
    private Boolean isFixed;    // 默认false

    //自定义
    private Boolean selfPayingUpgradeCarType;
    private String selfPayingRate;
    private Long selfPayingAmount;

}
