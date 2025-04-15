package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class ReceivePersonalPayResultParam implements Serializable {
    /**
     * 交易单号
     */
    private String transNo;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     *审批记录
     */
    private List<OrderPay> orderIds;
}
