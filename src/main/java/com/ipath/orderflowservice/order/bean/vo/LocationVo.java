package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LocationVo implements Serializable {
    private String locationName;// 地点名称
    private String location;// 地点地址
    private String cityName;// 城市名称
    private String cityCode;// 城市代码
    private String lng;// 地点坐标经读
    private String lat;// 地点坐标纬度
    private String poi;
}
