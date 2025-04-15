package com.ipath.orderflowservice.order.bean.bo;

import lombok.Data;


/**
 * 企业下单参数校验配置
 */
@Data
public class CompanyOrderCheckConfigBo {
    /**
     * 交通枢纽
     */
    private CompanyOrderCheckConfigTrafficHubBo trafficHub;


    /**
     * 行前+行后
     */
    private CompanyOrderCheckConfigInfoBo sceneApprovalTypePreAndAf;



}
