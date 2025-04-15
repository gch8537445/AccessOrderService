package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ApprovalRecord {
    /**
     * 审批排序
     */
    private Long sortId;
    /**
     * 审批人id
     */
    private String userId;
    /**
     * 审批状态
     * 1-通过
     * 2-拒绝
     */
    private Short approvalStatus;
    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date approvalTime;
    /**
     * 审批备注
     */
    private String comment;
}
