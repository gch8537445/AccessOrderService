package com.ipath.orderflowservice.order.dao.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserCouponListNewVo {
    private Long id;
    private BigDecimal threshold;
    private BigDecimal parValue;
    private Integer source;
    private String title;
    private String effectivePeriod;
    private AllowCitiesVo allowCities;
    private AllowCarsVo allowCars;
    private Integer state;
    private Boolean isValid;   // 可用且在有效期内
    private Boolean isExpired; // 未用且已过期则为true，否则为false
}
