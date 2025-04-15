package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpgradeParam {
    private int carSourceId;
    private BigDecimal estimatePrice;
}
