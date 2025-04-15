package com.ipath.orderflowservice.order.bean;

import lombok.Data;

@Data
public class CoreOrderDetail {

    private CoreOrderDetailOrder order;
    private CoreOrderDetailDriver driver;

}
