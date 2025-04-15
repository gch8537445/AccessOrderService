package com.ipath.orderflowservice.order.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 会议信息
 * @author: qy
 **/
@Data
@ApiModel(value = "会议信息")
public class MeetingVo {

    @ApiModelProperty(value = "会议id",position = 0)
    private Long id;

    @ApiModelProperty(value = "会议名称",position = 1)
    private String eventName;

    @ApiModelProperty(value = "会议编号",position = 2)
    private String eventCode;

    @ApiModelProperty(value = "IOTitle",position = 3)
    private String ioTitle;

    @ApiModelProperty(value = "IOCode",position = 4)
    private String ioCode;

    @ApiModelProperty(value = "EmployeeName",position = 5)
    private String employeeName;

    @ApiModelProperty(value = "个人会议类型 0: 常用会议 1: 个人会议")
    private Integer type;

}
