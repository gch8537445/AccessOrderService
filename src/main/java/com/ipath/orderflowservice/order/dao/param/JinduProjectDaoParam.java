package com.ipath.orderflowservice.order.dao.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: qy
 * @create: 2024-03-25 13:43
 **/
@Data
public class JinduProjectDaoParam {

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 成本中心code
     */
    private String code;

    /**
     * 企业code
     */
    private String companyCode;

    /**
     * 1: 成本中心 2: 案件
     */
    private Integer status;

}
