package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

/**
 * @description:
 * @author: qy
 **/
@Data
public class EventVo {

    /**
     * 预算编码
     */
    private String projectCode;


    /**
     * 会议编码
     */
    private String eventCode;

    /**
     * 会议名称
     */
    private String eventName;

    /**
     * 科目
     */
    private String departmentNameEn;




}
