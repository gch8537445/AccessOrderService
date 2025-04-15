package com.ipath.orderflowservice.order.bean.param;

import com.ipath.orderflowservice.order.bean.RedisCpolRegulationInfoAddressInfoCoordinate;
import lombok.Data;

import java.util.List;

/**
 * 可用地点
 */
@Data
public class Address {
    private String mode;
    private String type;
    private String beginCity;
    private String beginName;
    private List<RedisCpolRegulationInfoAddressInfoCoordinate> beginCoordinate;
    private String beginLat;
    private String beginLon;
    private String beginDistance;
    private boolean beginAny;
    private String endCity;
    private String endName;
    private List<RedisCpolRegulationInfoAddressInfoCoordinate> endCoordinate;
    private String endLat;
    private String endLon;
    private boolean endAny;
    private String endDistance;
    private boolean beginTrafficHub;
    private boolean endTrafficHub;
}
