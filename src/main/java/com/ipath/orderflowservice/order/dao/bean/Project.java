package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Project implements Serializable {
    private Long id;

    private Long companyId;

    private String nameCn;

    private String code;

    private String financialCode;

    private Date effectiveStart;

    private Date effectiveEnd;

    private String remarks;

    private Integer state;

    private Boolean isDelete;

    private Integer activityState;

    private Long createor;

    private Date createdTime;

    private Long updater;

    private Date updatedTime;

    private String nameEn;

    private String backupField1;

    private String backupField2;

    private String backupField3;

    private String backupField4;

    private String backupField5;

    private String backupField6;

    private String backupField7;

    private String backupField8;

    private String backupField9;

    private String backupField10;

    private String customInfo;

    private static final long serialVersionUID = 1L;


}