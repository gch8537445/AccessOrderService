package com.ipath.orderflowservice.order.dao.bean;


import lombok.Data;

import java.util.Date;

@Data
public class MeetingPersonal {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userid;

    /**
     * 会议id
     */
    private Long meetingId;

    /**
     * 类型 0: 自动添加  1: 手动添加
     */
    private Integer type;

    /**
     * 会议是否可用:为Closed、TECO时不可用
     */
    private String ioStatus;

    /**
     * 创建时间
     */

    private Date createTime;


}