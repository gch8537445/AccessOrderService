package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;
import java.util.List;

@Data
public class OrderDetails {
    private CacheOrder order;
    private DriverInfo driver;
    private Fee fee;
}
