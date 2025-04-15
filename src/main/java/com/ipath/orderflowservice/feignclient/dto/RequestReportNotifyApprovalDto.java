package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.DriverInfoNotifyReport;
import com.ipath.orderflowservice.order.bean.param.VehicleInfo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class RequestReportNotifyApprovalDto{
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 审批人id
     */
    private Long approverId;
    /**
     * 审批人name
     */
    private String approver;
    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date approvalTime;
    /**
     * 审批状态
     * 1：审批中
     * 2：审批通过
     * 3：拒绝
     */
    private Short approvalStatus;
    /**
     * 用车原因
     */
    private String useCarReason;
    /**
     * comment
     */
    private String Comment;
    /**
     * 自动审批标识
     * true:是
     * false:否
     */
    private Boolean isAutoApproval;
}
