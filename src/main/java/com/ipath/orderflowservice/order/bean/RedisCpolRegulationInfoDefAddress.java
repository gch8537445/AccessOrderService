package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 地点管控 bean
 *
 */
@Data
public class RedisCpolRegulationInfoDefAddress {
    private Boolean isConfirm;
    private RedisCpolRegulationInfoDefAddressPoint depart;
    private RedisCpolRegulationInfoDefAddressPoint dest;

}
