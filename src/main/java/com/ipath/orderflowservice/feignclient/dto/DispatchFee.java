package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DispatchFee {
    private boolean dispatchResponse;    //是否调度
    private BigDecimal dispatchAmount;     //调度费用
}
