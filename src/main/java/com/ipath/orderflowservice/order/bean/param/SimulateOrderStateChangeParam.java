package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SimulateOrderStateChangeParam {
    private Long orderId;
    private Integer carSource;
    private Integer carType;
    private String carTypeName;
    private Short status;
    //    private String driverName;
//    private String driverPhone;
//    private String  vehicleModel;
//    private String  vehicleColor;
//    private String  vehicleNo;
//    private String  driverLevel;
//    private String driverPhoneVirtual;
//    private String driverAvatar;
    private String lat;
    private String lng;
    private BigDecimal driveCarTime;
    private Boolean needApproval;
    private Long userId;
    private Long companyId;
}
