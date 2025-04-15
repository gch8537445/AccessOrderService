package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SelectPayedOrdersCount {
    private Long userId;
    private Date beginTime;
    private Date endTime;
}
