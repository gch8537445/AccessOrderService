package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

@Data
public class RequestCompanyAccountUserDto {
    private Long accountId;
    private Long entityId;//userId;
    private Integer mode = 1;
}
