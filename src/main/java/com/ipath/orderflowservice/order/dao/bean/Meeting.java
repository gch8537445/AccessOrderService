package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Meeting {

    /**
     * 主键
     */
    private Long id;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 会议名
     */
    private String eventName;

    /**
     * 会议编号
     */
    private String eventCode;

    /**
     * ioTitle
     */
    private String ioTitle;

    /**
     * IOCode
     */
    private String ioCode;

    /**
     * 截止日期
     */
    private Date validTo;

    /**
     * 部门名称
     */
    private String departmentNameEn;

    /**
     * 区域名称
     */
    private String areaNameEn;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 事件状态
     */
    private String eventStatus;

    /**
     * io 状态
     */
    private String ioStatus;

    /**
     * 员工id
     */
    private String employeeCode;

    /**
     * 员工名称
     */
    private String employeeName;


}