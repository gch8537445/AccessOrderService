package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 机场映射配置
 * @author: qy
 * @create: 2024-01-19 10:36
 **/
@Data
public class AirportCrossCityMapping {

    /**
     * 机场标题
     */
    private String title;

    /**
     * 机场所在城市code
     */
    private List<Integer> cityCode;

    /**
     * 跨城可用城市code
     */
    private List<Integer> mappingCityCode;

    /**
     * 机场坐标
     */
    private AirportPoint location;

    /**
     * 半径 (米)
     */
    private Double radius;



}
