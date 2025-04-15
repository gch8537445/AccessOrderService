package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderListPaidVo {
    private String transactionNo;      // 支付流水号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private String payTime;              // 订单创建时间
    private Short payType;
    private Short payway;
    private Integer count;             // 支付订单个数
    private BigDecimal totalAmount;    // 支付订单总额
    private BigDecimal refundMoney; //退款金额
    private List<PaidOrderVo> orders; // 支付订单列表
}
