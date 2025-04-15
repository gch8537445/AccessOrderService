package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

@Data
public class RefundItem {
    private Integer enterpriseRefundAmount;  //企业退款金额(分)
    private Integer personRefundAmount;  //个人退款金额(分)
    private String refundTime;  //"2020-02-02 23:33:33" 形式的时间戳 退款时间
}
