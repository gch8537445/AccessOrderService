package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RequestReportNotifyCancelOrderDto extends RequestReportNotifyBaseDto{
    /**
     * 公司主键
     */
    private Long companyId;
    private Integer cancelType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date cancelTime;
    private String cancelReason;
}
