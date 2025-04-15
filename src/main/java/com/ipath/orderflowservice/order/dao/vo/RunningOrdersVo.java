package com.ipath.orderflowservice.order.dao.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
public class RunningOrdersVo {

    private Long accountId;
    private Long companyId;
    private Long partnerOrderId;
    private String platformOrderId;
    private Short status;
    private int serviceType;
    private int carSource;
    private int carType;
    private String carTypeName;
    private String passengerPhone;
    private String phoneLastFour;
    private String departCityCode;
    private String departCityName;
    private String departLat;
    private String departLng;
    private String pickupLocation;
    private String pickupLocationName;
    private String departTime;
    private String destCityCode;
    private String destCityName;
    private String destLat;
    private String destLng;
    private String destLocation;
    private String destLocationName;
    private String flightArrivalAirportCode;
    private String flightArrivalTime;
    private String flightDepartAirportCode;
    private String flightDepartTime;
    private String flightNumber;
    private Boolean isChangeDest;
    private BigDecimal travelTime;
    private BigDecimal travelDistance;
    private String driverPickOrderTime;
    private String driverArrivedTime;
    private String beginChargeTime;
    private String finishTime;
    private BigDecimal amount;
    private String feeDetail;
    private JSONObject driver;
    private JSONObject fee;

    private String driverAvatar;
    private String driverLevel;
    private String driverName;
    private String driverPhone;
    private String driverPhoneVirtual;
    private String vehicleColor;
    private String vehicleModel;
    private String vehicleNo;

    public void setDriver() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("driverAvatar", driverAvatar);
        jsonObject.put("driverLevel", driverLevel);
        jsonObject.put("driverName", driverName);
        jsonObject.put("driverPhone", driverPhone);
        jsonObject.put("driverPhoneVirtual", driverPhoneVirtual);
        jsonObject.put("vehicleColor", vehicleColor);
        jsonObject.put("vehicleModel", vehicleModel);
        jsonObject.put("vehicleNo", vehicleNo);

        this.driver = jsonObject;
    }

    public void setFee(String feeDetail) {
        if (StringUtils.isNotEmpty(feeDetail)) {
            feeDetail.replace("nameCn", "name");
            feeDetail.replace("fees", "priceDetail");
            JSONObject jsonObject = JSONObject.parseObject(feeDetail);
            jsonObject.put("totalPrice", this.amount);
            this.fee = jsonObject;
        }
    }
}
