package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestoreSceneQuotaDto {
    private Long sceneId;
    private Long userId;
    private BigDecimal recoveryAmount;
}
