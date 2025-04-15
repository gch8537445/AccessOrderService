package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

/**
 * 用户按月查询自己订单列表
 */
@Data
public class QueryOrderListByMonth {
    private Integer pageNum;
    private Integer pageSize;
    private Long userId;
    private Long companyId;
    private String dateDuration; // 格式： "2021-11"
    //新增查询条件，审批状态
    private Integer approvalState;
}
