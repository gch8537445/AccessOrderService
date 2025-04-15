package com.ipath.orderflowservice.order.bean.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 会议参数
 * @author: qy
 **/
@Data
@ApiModel(value = "会议查询参数")
public class MeetingParam {

    @ApiModelProperty(value = "查询参数")
    private String param;

}
