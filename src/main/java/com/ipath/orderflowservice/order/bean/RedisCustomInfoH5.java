package com.ipath.orderflowservice.order.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "H5客户登录时自定义信息", description = "redis存储的跳转时传入的自定义信息")
@Data
public class RedisCustomInfoH5 {
    @ApiModelProperty(value = "场景code", example = "xvn128aa", required = true)
    private String sceneCode;

    @ApiModelProperty(value = "上游订单号", example = "12345678", required = true)
    private String partnerOrderId;

    @ApiModelProperty(value = "成本中心code", example = "s12999", required = true)
    private String costCenterCode;

    @ApiModelProperty(value = "成本中心名称", example = "xx项目", required = true)
    private String costCenter;

    @ApiModelProperty(value = "场景限制文本", example = "", required = true)
    private String taxiLocationLimits;
}
