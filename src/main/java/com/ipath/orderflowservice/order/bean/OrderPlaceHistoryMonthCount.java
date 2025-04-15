package com.ipath.orderflowservice.order.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderPlaceHistoryMonthCount implements Serializable {

    private Long id;

    private Long companyId;

    private Long userId;

    private String pickupLocation;

    private String pickupLocationName;

    private String departLat;

    private String departLng;

    private Integer count;


}