package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderFeeVo {
    /**
     * 订单id
     */
    private Long orderId;
    private BigDecimal orderAmount;
    private BigDecimal carAmount;
    private BigDecimal serviceFee;
    private BigDecimal markupFee;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date requestTime;
    private String notifyUrl;
    private BigDecimal onceAmount;
}
