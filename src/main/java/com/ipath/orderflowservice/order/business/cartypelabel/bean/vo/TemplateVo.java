package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TemplateVo {
    private Long id;

    private Short typeId;

    private Long companyId;

    private String userId;

    private String carSource;

    private String carLevel;

    private String serviceType;

    private Short approvalLevel;

    private Short complaintRange;

    private Short orderRange;

    private Short complaintCnt;

    private Short orderCnt;

    private String cityCode;

    private String orderAmount;

    private String distance;

    private String duration;

    private String inItem1;

    private String inItem2;

    private String inItem3;

    private String inItem4;

    private String inItem5;

    private String inItem6;

    private String inItem7;

    private String inItem8;

    private String inItem9;

    private String inItem10;

    private String outItem1;

    private String outItem2;

    private String outItem3;

    private String outItem4;

    private String outItem5;

    private String outItem6;

    private String outItem7;

    private String outItem8;

    private String outItem9;

    private String outItem10;

    private Date createdTime;

    private String createdUserId;

    private Date updatedTime;

    private String updatedUserId;

    private Short deleted;

    private Date deletedTime;

    private String deletedUserId;

}