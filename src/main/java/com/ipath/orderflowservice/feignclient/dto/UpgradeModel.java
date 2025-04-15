package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpgradeModel {
    private boolean isUpgradeCar;    //是否升舱成功
    private BigDecimal estimatePrice;     //升舱车型的预估价
    private BigDecimal companySettingMaxMoney;  // 最大抵扣金额
}
