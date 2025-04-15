package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 地点管控 bean
 *
 */
@Data
public class RedisCpolRegulationInfoAddress {

    /**
     * 验证origin和dest的逻辑 
     * and--两者都通过才可以
     * or--任意一个通过即可
     * 默认为and
     */
    private String type;

    /**
     * 起点和终点任意一点包含交通枢纽，另一端再固定的城市中即可。如果两端都是交通枢纽也允许
     */
    private RedisCpolRegulationInfoAddressInfo any;
    /**
     * 起始点
     */
    private RedisCpolRegulationInfoAddressInfo origin;
    /**
     * 终点
     */
    private RedisCpolRegulationInfoAddressInfo dest;

}
