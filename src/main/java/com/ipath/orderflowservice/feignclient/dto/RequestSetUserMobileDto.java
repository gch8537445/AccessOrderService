package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestSetUserMobileDto {
    private Long userId;
    private String mobile;
}
