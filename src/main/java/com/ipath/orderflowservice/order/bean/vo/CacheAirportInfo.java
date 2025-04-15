package com.ipath.orderflowservice.order.bean.vo;


import lombok.Data;


// 缓存到redis中的机场信息
@Data
public class CacheAirportInfo {
    private String coord;  // 机场坐标，从高德地图查询得到
    private String cityCode; // 城市code，从城市列表查询到
}
