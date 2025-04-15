package com.ipath.orderflowservice.order.bean.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 双选司机-确认接口
 * @author: qy
 * @create: 2025-03-21 10:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectTakeOrderParam {

    private Long orderId;

    private Long coreOrderId;

    private Long transOrderId;

    private Long userId;

    private Long companyId;

    private Boolean isNotifySettle;
}
