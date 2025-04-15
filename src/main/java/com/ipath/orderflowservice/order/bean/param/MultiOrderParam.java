package com.ipath.orderflowservice.order.bean.param;

import java.util.List;

import lombok.Data;

@Data
public class MultiOrderParam {
    /**
     * 是否是后台审核
     */
    private Boolean isAdminApproval;
    private Long userId;
    private Long companyId;
    private List<Long> orderIds;
    private String reason;
}
