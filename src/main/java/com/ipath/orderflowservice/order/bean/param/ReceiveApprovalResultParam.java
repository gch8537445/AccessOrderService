package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class ReceiveApprovalResultParam implements Serializable {
    /**
     * 申请单号
     */
    private Long approveId;
    /**
     * 审批状态
     * 0-未审批
     * 1-通过
     * 2-拒绝
     */
    private Long approvalStatus;
    /**
     *审批类型
     * 1-行前
     * 2-行后
     */
    private Long approvalType;
    /**
     *审批记录
     */
    private List<ApprovalRecord> approvalRecords;
}
