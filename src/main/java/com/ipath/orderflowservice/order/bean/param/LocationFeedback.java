package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 位置反馈
 */
@Data
public class LocationFeedback implements Serializable{
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 类型
     * 1-出发地
     * 2-目的地
     */
    private Short type;

    /**
     * 是否正确
     */
    private Boolean isCorrect;
}
