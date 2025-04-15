package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RequestSettlementDto {
    private Short eventType;
    private Long companyId;
    private Long accountId;
    private Long userId;
    private Long orderId;
    private Long sceneId;
    private Long couponId;
    private int carLevel;
    private List<SelectedCar> carLevels;
    private List<Long> extraServiceIds;
    private Long scenePublishId;
    private BigDecimal estimatePrice;
}
