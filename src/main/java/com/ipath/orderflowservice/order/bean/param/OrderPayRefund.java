package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单支付详情
 */
@Data
public class OrderPayRefund {
    /**
     * 支付中心交易号
     */
    private String transNo;

    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 退款金额
     */
    private BigDecimal payRefundAmount;

}
