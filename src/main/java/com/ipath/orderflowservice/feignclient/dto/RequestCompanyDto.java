package com.ipath.orderflowservice.feignclient.dto;
import lombok.Data;

@Data
public class RequestCompanyDto {
    private Long companyId;
    private String companyCode;
    private Long userId;
}
