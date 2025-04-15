package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetPrePayAmountParam {
    private Long userId;
    private Long orderId;
    private Long estimateId;
    private BigDecimal amount;
    private int carLevel;
    private Boolean isValidationAccount;
    private List<SelectedCar> cars;// 选择的车型数组
}
