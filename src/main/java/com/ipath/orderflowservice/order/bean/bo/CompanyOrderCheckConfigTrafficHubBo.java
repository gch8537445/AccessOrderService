package com.ipath.orderflowservice.order.bean.bo;

import lombok.Data;

import java.util.List;

/**
 * 企业下单参数校验配置
 */
@Data
public class CompanyOrderCheckConfigTrafficHubBo {
    /**
     * 交通枢纽
     */
    private List<Long> companyList;

    /**
     * 限制企业配置
     */
    private List<CompanyOrderCheckConfigTrafficHubInfoBo> configList;


}
