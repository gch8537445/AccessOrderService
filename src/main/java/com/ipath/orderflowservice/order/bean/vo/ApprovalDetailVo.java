package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ApprovalDetailVo {
    private String approverNameCn;
    private Short approverNameEn;
    private Short approveState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date time;
    private String reason;
}
