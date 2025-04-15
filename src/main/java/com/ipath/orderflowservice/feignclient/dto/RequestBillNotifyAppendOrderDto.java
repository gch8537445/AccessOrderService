package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 追加车型时 通知bill-core-service的消息体
 */
@Data
public class RequestBillNotifyAppendOrderDto extends RequestBillNotifyBaseDto{
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
}
