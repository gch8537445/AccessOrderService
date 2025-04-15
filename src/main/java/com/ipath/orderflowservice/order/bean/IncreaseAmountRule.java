package com.ipath.orderflowservice.order.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 加价金额
 * @author: qy
 * @create: 2024-04-09 19:48
 **/
@Data
@ApiModel(value = "加价金额")
public class IncreaseAmountRule {

    @ApiModelProperty(value = "类型 1:按金额(分) 2: 按比例(小数)")
    private Integer type;

    @ApiModelProperty(value = "加价金额(分,小数)")
    private BigDecimal amount;
}
