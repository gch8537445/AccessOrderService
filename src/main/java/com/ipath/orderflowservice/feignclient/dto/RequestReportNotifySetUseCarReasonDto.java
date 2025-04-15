package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RequestReportNotifySetUseCarReasonDto extends RequestReportNotifyBaseDto{

    private String userCarReason;
}
