package com.ipath.orderflowservice.order.dao.vo;

import lombok.Data;


@Data
public class OrderErrorState {
    private Long id;

    private String coreOrderId;

    private String partnerOrderId;

    private Short state;

    private Long userId;

    private Long companyId;

    private Short serviceType;

    private String orderDetail;


}
