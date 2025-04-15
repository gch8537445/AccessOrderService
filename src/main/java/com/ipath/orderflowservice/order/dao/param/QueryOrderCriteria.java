package com.ipath.orderflowservice.order.dao.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryOrderCriteria {
    private Long userId;
    private String approverUserId;
    private Date beginDate;
    private Date endDate;
    private List<Short> stateList; // 状态集合
}
