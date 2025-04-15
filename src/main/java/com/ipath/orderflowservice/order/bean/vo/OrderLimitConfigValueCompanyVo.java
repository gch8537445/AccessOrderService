package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderLimitConfigValueCompanyVo implements Serializable {
    private Long companyId;
    private String value;
}