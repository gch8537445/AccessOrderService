package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CompanyLocations implements Serializable {
    private Long id;

    private Long companyId;

    private String cityCode;

    private String cityName;

    private String address;

    private String name;

    private BigDecimal gcj02Lat;

    private BigDecimal gcj02Lng;

    private BigDecimal gpsLat;

    private BigDecimal gpsLng;

    private Long createor;

    private Date createdTime;

    private Long accountId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getGcj02Lat() {
        return gcj02Lat;
    }

    public void setGcj02Lat(BigDecimal gcj02Lat) {
        this.gcj02Lat = gcj02Lat;
    }

    public BigDecimal getGcj02Lng() {
        return gcj02Lng;
    }

    public void setGcj02Lng(BigDecimal gcj02Lng) {
        this.gcj02Lng = gcj02Lng;
    }

    public BigDecimal getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(BigDecimal gpsLat) {
        this.gpsLat = gpsLat;
    }

    public BigDecimal getGpsLng() {
        return gpsLng;
    }

    public void setGpsLng(BigDecimal gpsLng) {
        this.gpsLng = gpsLng;
    }

    public Long getCreateor() {
        return createor;
    }

    public void setCreateor(Long createor) {
        this.createor = createor;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}