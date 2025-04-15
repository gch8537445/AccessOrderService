package com.ipath.orderflowservice.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CoreOrderDetailDriver {

    private String driverName;
    private String driverPhoneVirtual;
    private String vehicleModel; // 型号
    private String vehicleColor;
    private String vehicleNo; // 车牌号
    private String driverLevel;
    private String driverAvatar;

}
