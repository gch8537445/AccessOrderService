package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeItem {
    private String nameCn;
    private String nameEn;
    private BigDecimal amount;
    private String type;
}
