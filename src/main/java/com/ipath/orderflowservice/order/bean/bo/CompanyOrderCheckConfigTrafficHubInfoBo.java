package com.ipath.orderflowservice.order.bean.bo;

import lombok.Data;

import java.util.List;

/**
 * 企业下单参数校验配置
 */
@Data
public class CompanyOrderCheckConfigTrafficHubInfoBo {

    private Long companyId;
    private String errorMsg;
    private List<String> checkSceneNames;

}
