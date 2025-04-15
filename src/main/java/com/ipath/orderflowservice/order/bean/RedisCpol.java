package com.ipath.orderflowservice.order.bean;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel(value = "登录用户管控信息", description = "redis存储的下单限制")
@Data
public class RedisCpol {

    @ApiModelProperty(value = "用户id", example = "1", required = true)
    private String id;

    @ApiModelProperty(value = "用户姓名", example = "张三", required = true)
    private String userName;

    @ApiModelProperty(value = "用户手机号", example = "18888888888", required = true)
    private String mobile;

    @ApiModelProperty(value = "用车类型 1-普通 2-舒适 3-商务 4-豪华", example = "[1,2,3]", required = true)
    private List<String> rideTypes;

    @ApiModelProperty(value = "用车管控", required = true)
    private RedisCpolRegulationInfo regulationInfo;


    @ApiModelProperty(name = "自定义字符串。")
    private String customStr;

    private JSONObject customInfo;


    private String controlGroupId;

    private String mtEntUserUniqueKey;

    private String sqtTk;

    private String tripartiteToken;

}
