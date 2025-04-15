package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyUserConfig implements Serializable {
    private Long id;

    private Long companyId;

    private Long userId;

    private String type;

    private Object value;

    private String remarks;

    private static final long serialVersionUID = 1L;

}