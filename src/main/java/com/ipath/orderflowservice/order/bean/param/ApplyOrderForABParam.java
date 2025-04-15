package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.util.List;

@Data
public class ApplyOrderForABParam {
    private Long workflowId;
    private List<Long> orderIds;
}
