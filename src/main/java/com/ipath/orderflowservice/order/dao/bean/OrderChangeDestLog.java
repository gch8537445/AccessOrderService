package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.util.Date;

public class OrderChangeDestLog implements Serializable {
    private Long id;

    private Long orderSourceId;

    private String oldCityCode;

    private String oldCityName;

    private String oldLocation;

    private String oldLocationName;

    private String oldLat;

    private String oldLng;

    private String carLocation;

    private String carLocationName;

    private String carLat;

    private String carLng;

    private Date createTime;

    private Long orderId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(Long orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public String getOldCityCode() {
        return oldCityCode;
    }

    public void setOldCityCode(String oldCityCode) {
        this.oldCityCode = oldCityCode;
    }

    public String getOldCityName() {
        return oldCityName;
    }

    public void setOldCityName(String oldCityName) {
        this.oldCityName = oldCityName;
    }

    public String getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(String oldLocation) {
        this.oldLocation = oldLocation;
    }

    public String getOldLocationName() {
        return oldLocationName;
    }

    public void setOldLocationName(String oldLocationName) {
        this.oldLocationName = oldLocationName;
    }

    public String getOldLat() {
        return oldLat;
    }

    public void setOldLat(String oldLat) {
        this.oldLat = oldLat;
    }

    public String getOldLng() {
        return oldLng;
    }

    public void setOldLng(String oldLng) {
        this.oldLng = oldLng;
    }

    public String getCarLocation() {
        return carLocation;
    }

    public void setCarLocation(String carLocation) {
        this.carLocation = carLocation;
    }

    public String getCarLocationName() {
        return carLocationName;
    }

    public void setCarLocationName(String carLocationName) {
        this.carLocationName = carLocationName;
    }

    public String getCarLat() {
        return carLat;
    }

    public void setCarLat(String carLat) {
        this.carLat = carLat;
    }

    public String getCarLng() {
        return carLng;
    }

    public void setCarLng(String carLng) {
        this.carLng = carLng;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}