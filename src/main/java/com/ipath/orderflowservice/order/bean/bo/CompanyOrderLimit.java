package com.ipath.orderflowservice.order.bean.bo;

import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import lombok.Data;

import java.util.List;

/**
 * 企业下单限制开关与配置
 */
@Data
public class CompanyOrderLimit {
    private String type;
    private List<Long> companyList;
    private List<OrderLimitConfig> companyConfigList;
}
