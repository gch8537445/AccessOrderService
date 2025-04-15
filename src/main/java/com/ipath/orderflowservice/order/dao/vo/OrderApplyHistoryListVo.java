package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderApplyHistoryListVo {
    @JsonIgnore
    private Long approverUserId;
    private String nameCn;              // 审批人姓名中文
    private String nameEn;              // 审批人姓名英文
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;            // 审批时间（审批历史创建时间）
    private String reason;              // 审批原因/理由
    private Short state;                // 审核状态：1: 未处理，2：已通过，4：已拒绝
    private String customInfo;
    private Boolean isAbnormal;
    private List<String> abnormalRules;
}
