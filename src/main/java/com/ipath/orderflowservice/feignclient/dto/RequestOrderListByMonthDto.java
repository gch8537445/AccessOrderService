package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RequestOrderListByMonthDto {
    private Integer pageNum;
    private Integer pageSize;
    private Long userId;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    // private Date beginTime;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    // private Date endTime;
    private String dateDuration; // 格式： "2021-11"
    //新增查询条件，审批状态
    private Integer approvalState;
    private Long companyId;
}
