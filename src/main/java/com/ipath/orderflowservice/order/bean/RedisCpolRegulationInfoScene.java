package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 场景 bean
 *
 */
@Data
public class RedisCpolRegulationInfoScene {
    /**
     * 场景code
     */
    private String sceneCode;
    /**
     * 场景名称
     */
    private String sceneName;



}
