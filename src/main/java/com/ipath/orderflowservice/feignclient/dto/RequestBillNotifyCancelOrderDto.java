package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 取消订单 通知bill-core-service的消息体
 */
@Data
public class RequestBillNotifyCancelOrderDto extends RequestBillNotifyBaseDto{
    /**
     * 预估价格
     */
    private BigDecimal estimatePrice;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 预付标识
     * true：预付 false：非预付
     */
    private Boolean isPrePay;

    /**
     * 运力总费用
     */
    private BigDecimal transTotalFee;
}
