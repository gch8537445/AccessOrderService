package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Fee {
    private BigDecimal totalAmount;
    private List<FeeItem> fee;
}
