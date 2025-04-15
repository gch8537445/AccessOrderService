package com.ipath.orderflowservice.order.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description: 金杜projectVO
 * @author: qy
 * @create: 2024-03-22 09:56
 **/
@Data
@ApiModel(value = "金杜成本中心信息")
public class JinduCostCenterVo {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "成本中心code")
    private String code;

    @ApiModelProperty(value = "企业code")
    private String companyCode;

    @ApiModelProperty(value = "1: 非商标组, 2: 商标组")
    private Integer state;

}
