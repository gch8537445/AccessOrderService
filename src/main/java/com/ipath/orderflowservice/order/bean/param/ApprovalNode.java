package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ApprovalNode {
    /**
     * 审批排序
     */
    private Long sortId;
    /**
     * 审批节点code
     */
    private String outCode;
    /**
     * 审批人id
     */
    private String userId;
}
