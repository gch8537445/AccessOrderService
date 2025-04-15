package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 地点管控 bean
 *
 */
@Data
public class RedisCpolRegulationInfoDefAddressPoint {


    /**
     * 名称
     */
    private String name;

    /**
     * 经度
     */
    private String lon;
    /**
     * 纬度
     */
    private String lat;

}
