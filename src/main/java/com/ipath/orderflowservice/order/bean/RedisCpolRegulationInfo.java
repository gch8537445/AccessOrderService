package com.ipath.orderflowservice.order.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel(value = "用车管控", description = "redis存储的下单限制", parent = RedisCpol.class)
@Data
public class RedisCpolRegulationInfo {

    @ApiModelProperty(name = "申请单号。", example = "990776")
    private String code;

    @ApiModelProperty(name = "申请单有效期。")
    private RedisCpolRegulationInfoValidDate validDate;

    @ApiModelProperty(name = "是否允许跨城 true-允许跨城 false-不允许跨城。注：对于包含交通枢纽的无效。")
    private Boolean crossRegional;

    @ApiModelProperty(name = "合作方单号。", example = "168962342342112313123")
    private String partnerOrderId;

    @ApiModelProperty(name = "本单服务费，单位为分。", example = "99")
    private String serviceFee;

    @ApiModelProperty(name = "金额管控。")
    private RedisCpolRegulationInfoAmount amount;

    @ApiModelProperty(name = "次数限制。")
    private RedisCpolRegulationInfoNumber number;

    @ApiModelProperty(name = "地点：前端要求要这个参数。")
    private RedisCpolRegulationInfoDefAddress defAddress;

    @ApiModelProperty(name = "地点管控。")
    private RedisCpolRegulationInfoAddress address;

    @ApiModelProperty(name = "场景。")
    private List<RedisCpolRegulationInfoScene> scene;

    @ApiModelProperty(name = "用车类型(1：普通，2：舒适，3：商务，4：豪华）", example = "[1,2,3,4]")
    private List<String> rideTypes;

    @ApiModelProperty(name = "服务类型(1: 实时，2：预约，6：接机）", example = "[1,2,6]")
    private List<Short> serverTypes;

    @ApiModelProperty(name = "超出管控额度是否允许下单  允许-true 不允许-false。", example = "true")
    private Boolean allowExcess;

    @ApiModelProperty(name = "默认选中服务类型(1：实时，2：预约，6：接机）", example = "1,2,6")
    private String defaultServiceType;

    @ApiModelProperty(name = "预约时间 2023-11-15 18:50:00")
    private String departTime;

    @ApiModelProperty(name = "加价金额规则")
    private IncreaseAmountRule increaseAmountRule;

}
