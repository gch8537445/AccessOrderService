package com.ipath.orderflowservice.order.bean.bo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * 企业下单参数校验配置详情
 */
@Data
public class CompanyOrderCheckConfigInfoBo {
    /**
     * 企业列表
     */
    private List<Long> companyList;

    /**
     * 企业配置
     */
    private Map<Long, JSONObject> configMap;

}
