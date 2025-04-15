package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class RequestReportNotifySettleOrderDto extends RequestReportNotifyBaseDto{
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelEndTime;
    private Integer travelDuration;
    private BigDecimal travelDistance;
    private BigDecimal totalAmount;
    private String feeDetail;
    private BigDecimal extralAmount;
    private BigDecimal couponAmount;
    private BigDecimal prepayAmount;
    private BigDecimal prepayRefundAmount;
    private BigDecimal companyAmount;
    private BigDecimal personalAmount;
    private BigDecimal actualAmount;
}
