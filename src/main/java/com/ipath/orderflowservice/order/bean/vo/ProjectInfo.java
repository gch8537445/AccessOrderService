package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectInfo {
    private Long companyId;
    private Long id;
    private String nameCn;
    private String nameEn;
    private String code;
    private String financialCode;
    private Date effectiveStart;
    private Date effectiveEnd;
}
