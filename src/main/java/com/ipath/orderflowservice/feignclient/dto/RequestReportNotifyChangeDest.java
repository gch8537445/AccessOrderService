package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RequestReportNotifyChangeDest extends RequestReportNotifyBaseDto{
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date changeDestTime;
    private String changeDestLocation;
    private String changeDestLat;
    private String changeDestLng;
    private String actualDestLocation;
    private String actualDestLocationName;
    private String actualDestLat;
    private String actualDestLng;
    private Boolean isChangeDest;
    private Long companyId;

    /**
     * 预估距离 (米)
     */
    private Integer estimateDistance;

    /**
     * 预估时间 (秒)
     */
    private Integer estimateTime;


    private BigDecimal estimatePrice;

}
