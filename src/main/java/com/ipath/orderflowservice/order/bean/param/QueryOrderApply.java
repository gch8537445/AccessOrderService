package com.ipath.orderflowservice.order.bean.param;

import com.ipath.dao.param.QueryOrderCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: qy
 * @create: 2024-11-07 19:07
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryOrderApply extends QueryOrderCriteria {
    private String beApprovedUserName; // 被审批人的名称
}
