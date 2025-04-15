package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

@Data
public class CompanyAbnormalItems {
    private String itemCode;
    private String itemName;
    private String itemType;
    private String itemValue;
}
