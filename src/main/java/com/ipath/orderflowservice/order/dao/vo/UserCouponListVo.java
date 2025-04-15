package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserCouponListVo {
    private Long id;
    private BigDecimal threshold;
    private BigDecimal parValue;
    private Integer source;
    private String title;
    private Date beginDate;
    private Date endDate;
    private String effectivePeriod;
    private String allowCities;
    private String allowCarLevels;
    private String allowCarSource;
    private Integer state;
    private Boolean isValid;   // 可用且在有效期内
    private Boolean isExpired; // 未用且已过期则为true，否则为false
    private Boolean allowCitysAvailable;
    private Boolean allowCarAvailable;
}
