package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestOrderPayEventDto {
    private Long companyId;

    private Long orderId ;

    private Boolean isReject;

    private Long sceneId;

    private Long userId;

    private BigDecimal recoveryAmount;
}
