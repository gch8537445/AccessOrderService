package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class ExtraService {
    private Long id;
    private String code;
    private Boolean isNotifySettle;
}
