package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 地点管控 详细 经纬度bean
 *
 */
@Data
public class RedisCpolRegulationInfoAddressInfoCoordinate {
    /**
     * 纬度
     */
    private String lat;
    /**
     * 经度
     */
    private String lng;
    /**
     * 允许的误差
     */
    private String range;

}
