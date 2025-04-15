package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.Date;

/**
 * @description: 个人会议信息
 * @author: qy
 **/
@Data
@ApiModel(value = "个人会议列表信息")
public class MeetingPersonalVo {

    /**
     * 主键
     */
    @ApiModelProperty(value = "个人会议id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "会议id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long meetingId;

    /**
     * 会议名
     */
    @ApiModelProperty(value = "会议名")
    private String eventName;

    /**
     * 会议编号
     */
    @ApiModelProperty(value = "会议名")
    private String eventCode;

    /**
     * ioTitle
     */
    @ApiModelProperty(value = "会议名")
    private String ioTitle;

    /**
     * IOCode
     */
    @ApiModelProperty(value = "IOCode")
    private String ioCode;

    /**
     * 截止日期
     */
    @ApiModelProperty(value = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validTo;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String departmentNameEn;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String areaNameEn;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /**
     * 事件状态
     */
    @ApiModelProperty(value = "事件状态")
    private String eventStatus;

    @ApiModelProperty(value = "个人会议类型 0: owner会议 1: 个人添加会议")
    private Integer type;

    @ApiModelProperty(value = "EmployeeName")
    private String employeeName;


}
