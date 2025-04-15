package com.ipath.orderflowservice.order.dao.vo;

import lombok.Data;

/**
 * 企业用车限制和场景参数mapping vo
 */
@Data
public class CompanyLimitMappingVo {
    /**
     * 限制类型
     * 1：单次额度限制
     * 2：当天总额度限制
     */
    private Short type;

    /**
     * 参数code
     */
    private String paraCode;
}
