package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

/**
 * 后台预约管家重新叫车参数
 */
@Data
public class ReorderParam {
    private String orderId;
    private Short serviceType;
    private String flightNumber;
    private Integer sourceCode;     // 运力平台代码
    private Integer carLevel;       // 车型等级
}
