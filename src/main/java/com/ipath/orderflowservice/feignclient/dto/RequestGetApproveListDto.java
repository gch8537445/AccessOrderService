package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

@Data
public class RequestGetApproveListDto {
    private Long procID;
    private Long businessID;
}
