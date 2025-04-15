package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WorkflowParam implements Serializable{
    /**
     * 业务类型
     * 1-订单审批流程
     */
    private int businessType;

    /**
     * 业务id
     */
    private Long businessId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 流程定义id
     */
    private Long workflowId;

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 流程动作
     * 1-提交
     * 2-审批
     * 4-拒绝
     */
    private int action;

    /**
     * 重新提交
     */
    private Boolean resubmit;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * comment
     */
    private String comment;

    /**
     * 审批金额
     */
    private BigDecimal amount;

    /**
     * 
     * 自定义信息
     */
    private String customInfo;

    /**
     * 行前申请单id
     */
    private Long preDepartApplyId;
}
