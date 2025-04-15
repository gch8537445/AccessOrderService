package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ApprovalHistoryVo {
    private Short lastApproveState;
    private List<ApprovalDetailVo> list;
}
