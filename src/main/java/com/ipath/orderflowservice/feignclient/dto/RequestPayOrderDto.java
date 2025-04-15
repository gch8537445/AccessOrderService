package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.PayOrder;
import lombok.Data;

import java.util.List;

@Data
public class RequestPayOrderDto {
    private Long companyId;
    private Long userId; // 业务的id（订单号）
    private Short payState;
    private String start;
    private String end;
    private int pageNum;
    private int pageSize;
}
