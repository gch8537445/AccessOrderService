package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlightDetailVo implements Serializable {

    private String time;                    // 航班时间: 出发城市 - 到达城市  起飞时间 - 到达时间
    private String flightNumber;            // 航班号
    private String departTime;              // 起飞时间
    private String arrivalTime;             // 到达时间
    private String departAirportCode;       // 出发机场代码
    private String arrivalAirportCode;      // 到达机场代码
    private String tip;                     // 提示文字
    private String planDepartTime;          // 计划起飞时间
    private String planArrivalTime;         // 计划到达时间
    private String arrivalAirportCoord;     // 到达机场坐标
    private String arrivalCityName;         // 到达城市名称
    private String arrivalCityCode;         // 到达城市代码
    private String arrivalAirportName;      // 到达机场名称
    private String arrivalAirportLocation;  // 到达机场地址
}
