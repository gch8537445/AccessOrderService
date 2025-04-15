package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CacheExtraService {
    private Long extraServiceId;
    private BigDecimal amount;
    private String nameCn;
    private String nameEn;
}
