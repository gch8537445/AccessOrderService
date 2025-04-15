package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @description: 公司订单数量
 * @author: qy
 **/
@Data
public class CompanyOrderCountVo {

    private Long companyId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer state;

    private Integer count;
}
