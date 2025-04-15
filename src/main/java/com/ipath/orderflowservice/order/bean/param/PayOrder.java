package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayOrder {
    private Long id;//订单主键
    private BigDecimal amount;//金额
    private Short payType;//1企业部分2个人部分3初始预付费部分4结算预付费部分 大额预付费传3
    private Long companyAccountId;//公司账户主键
}
