package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrePayParam {
    private String orderId;
    private BigDecimal amount;
    private Long userId;
    private Long companyId;
    private Boolean appendCarTypePrePay;//增加车型大额预付标识
    //private Boolean isMiniApp;
    //新增字段
    private Integer paymentType;
    private String redirectUri;
}
