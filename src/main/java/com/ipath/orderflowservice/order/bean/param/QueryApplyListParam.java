package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class QueryApplyListParam extends QueryOrderListByMonth {
    private Short type;  // 审批状态：1:未审批，2:已审批
    private String beApprovedUserName; // 被审批人的名称

}
