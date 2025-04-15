package com.ipath.orderflowservice.order.bean;

import lombok.Data;

import java.util.List;

/**
 * redis 存储的下单限制 用车管控 地点管控 详细 bean
 *
 */
@Data
public class RedisCpolRegulationInfoAddressInfo {
    /**
     * 是否是交通枢纽，如果是true则忽略此项下面的参数
     */
    private boolean trafficHub;
    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 经纬度坐标
     */
    private List<RedisCpolRegulationInfoAddressInfoCoordinate> coordinate;



}
