package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class OrderIdParam {
    private Long orderId;
    private Long userId;
    private Short type;  // 订单详情页面: type = 2 待审批, type = 3 已审批
    private Boolean isReadonly;//目前是根据此字段查询用户的语言，后续可能会支持别的


}
