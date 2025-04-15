package com.ipath.orderflowservice.order.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "用车管控", description = "redis存储的下单限制", parent = RedisCpol.class)
@Data
public class RedisCpolRegulationInfoVo {

    @ApiModelProperty(name = "地点：前端要求要这个参数。")
    private RedisCpolRegulationInfoDefAddress defAddress;

    @ApiModelProperty(name = "预约时间 2023-11-15 18:50:00")
    private String departTime;

    private String mobile;
}
