package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付中心的支付回调
 */
@Data
public class OrderPayNotifyParam {
    /**
     * 支付中心交易号
     */
    private String transNo;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 订单id数组
     */
    private List<Long> orderIds;

    /**
     * 支付详情
     */
    private List<OrderPayDetail> orderPayInfo;

    /**
     * 支付渠道
     */
    private String payWay;

    /**
     * 状态
     *  新版：待支付 = 0,已支付 = 1,支付失败 = 2,支付退款 = 3
     */
    private Short state;

    private String sign;



}
