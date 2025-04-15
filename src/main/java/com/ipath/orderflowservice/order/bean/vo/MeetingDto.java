package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: 会议信息
 * @author: qy
 **/
@Data
public class MeetingDto extends MeetingVo {
    private Date orderTime;
}
