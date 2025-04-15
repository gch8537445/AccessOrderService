package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.util.Date;

/**
 * @description: 场景节假日
 * @author: qy
 **/
@Data
public class ComSceneHoliday {

    /**
     * 主键
     */
    private Long id;

    /**
     * 公司主键
     */
    private Long companyId;

    /**
     * 场景版本主键
     */
    private Long sceneId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 日期
     */
    private Date day;

    /**
     * 日期类别 1:节假日; 2:工作日
     */
    private Integer dayType;

    /**
     * 中文名
     */
    private String nameCn;

    /**
     * 英文名
     */
    private String nameEn;

    private Long createor;

    private Date createdTime;

    private Long updater;

    private Date updatedTime;
}
