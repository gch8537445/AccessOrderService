package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyCallbackConfig implements Serializable {
    private Long id;

    private Long companyId;

    private String companyCode;

    private Boolean needBack;

    private String callbackPath;

    private String callbackType;

    private String appId;

    private String additionalValue;

    private String columnMapping;

    private static final long serialVersionUID = 1L;

    private String serviceName;

}