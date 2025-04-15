package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

@Data
public class CacheCompanyInfo{
    private String companyCode;
    private String companyName;
    private String source;
    private String displayId;
    private String tenantKey;
    private String appId;
    private Integer socialAppType;
    private String socialAppId;
    private Boolean accountEnabled;
    private String corpId;
}
