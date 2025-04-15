package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 公司审批设置
 */
@Data
public class ApprovalSettingParam implements Serializable{
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 审批设置
     */
    private Object approvalSettingList;
}
