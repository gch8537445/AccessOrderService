package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestUserDto {
    private Long userId;
    private Long companyId;
}
