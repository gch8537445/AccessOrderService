package com.ipath.orderflowservice.order.bean.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: qy
 **/
@Data
@ApiModel(value = "会议创建参数")
public class MeetingCreateParam {

    @ApiModelProperty(value = "会议id")
    private Long meetingId;
}
