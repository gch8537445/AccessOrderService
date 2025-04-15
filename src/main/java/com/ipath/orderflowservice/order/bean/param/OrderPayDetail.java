package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单支付详情
 */
@Data
public class OrderPayDetail {
    /**
     * 订单id
     */
    private long orderId;

    /**
     * 支付类型
     * 1：企业审批拒绝支付 2：个人支付 3：个人大额预付费支付-前置 4：个人大额预付费支付-后置
     */
    private int payType;


    /**
     * 支付金额
     */
    public BigDecimal money;
}
