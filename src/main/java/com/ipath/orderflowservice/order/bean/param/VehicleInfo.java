package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class VehicleInfo {
    private String vehicleModel;
    private String vehicleColor;
    private String vehicleNo;
    private Boolean isEnergyVehicle;
}
