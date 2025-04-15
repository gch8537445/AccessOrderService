package com.ipath.orderflowservice.order.bean.param;

import com.ipath.orderflowservice.order.bean.vo.OrderDetails;
import lombok.Data;

@Data
public class NotifyOrderStatusParam {
    private String orderid;
    private Short status;
    private String substatus;
    private String partnerorderid;
    private String oid;
    private OrderDetails orderDetail;
    private String coreOrderId;
}
