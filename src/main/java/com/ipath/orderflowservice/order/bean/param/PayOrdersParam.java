package com.ipath.orderflowservice.order.bean.param;

import java.util.List;

import lombok.Data;

@Data
public class PayOrdersParam {
    private List<OrderAmount> orders;
}
