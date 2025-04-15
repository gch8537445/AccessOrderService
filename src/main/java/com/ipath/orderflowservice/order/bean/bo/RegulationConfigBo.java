package com.ipath.orderflowservice.order.bean.bo;

import lombok.Data;

import java.util.List;

@Data
public class RegulationConfigBo {
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 缓存失效配置
     */
    private RegulationConfigInvalidationBo  invalidation;
}
