package com.ipath.orderflowservice.order.dao.param;

import lombok.Data;

import java.util.Date;

@Data
public class QueryPendingApproval {
    private Long companyId;
    private Date endDate;
}
