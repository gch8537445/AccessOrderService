package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

// 司机信息
@Data
public class DriverInfo {
    private String name;
    private String phone;
    private String phoneVirtual;
    private String vehicleModel;
    private String vehicleColor;
    private String vehicleNo;
    private String level;
    private String avatar;
}
