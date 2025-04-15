package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserCoupon implements Serializable {
    private Long id;

    private Long companyCouponId;

    private Long userId;

    private BigDecimal parValue;

    private String title;

    private String description;

    private Date beginDate;

    private Date endDate;

    private String allowCities;

    private BigDecimal threshold;

    private String allowCarLevels;

    private String allowCarSource;

    private Integer source;

    private Integer state;

    private Date createTime;

    private Date updateTime;

    private Boolean allowCitysAvailable;

    private Integer allowCitysQty;

    private Boolean allowCarAvailable;

    private Integer allowCarQty;

    private String allowCitiesText;

    private String allowCarLevelsText;

    private String allowCarSourceText;

    private Long createor;

    private Long updater;

    private Long orderId;

    private Long companyId;

    private String couponNum;

    private Date usedDate;

    private Short usedMoney;

    private String couponSign;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyCouponId() {
        return companyCouponId;
    }

    public void setCompanyCouponId(Long companyCouponId) {
        this.companyCouponId = companyCouponId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAllowCities() {
        return allowCities;
    }

    public void setAllowCities(String allowCities) {
        this.allowCities = allowCities;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public String getAllowCarLevels() {
        return allowCarLevels;
    }

    public void setAllowCarLevels(String allowCarLevels) {
        this.allowCarLevels = allowCarLevels;
    }

    public String getAllowCarSource() {
        return allowCarSource;
    }

    public void setAllowCarSource(String allowCarSource) {
        this.allowCarSource = allowCarSource;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getAllowCitysAvailable() {
        return allowCitysAvailable;
    }

    public void setAllowCitysAvailable(Boolean allowCitysAvailable) {
        this.allowCitysAvailable = allowCitysAvailable;
    }

    public Integer getAllowCitysQty() {
        return allowCitysQty;
    }

    public void setAllowCitysQty(Integer allowCitysQty) {
        this.allowCitysQty = allowCitysQty;
    }

    public Boolean getAllowCarAvailable() {
        return allowCarAvailable;
    }

    public void setAllowCarAvailable(Boolean allowCarAvailable) {
        this.allowCarAvailable = allowCarAvailable;
    }

    public Integer getAllowCarQty() {
        return allowCarQty;
    }

    public void setAllowCarQty(Integer allowCarQty) {
        this.allowCarQty = allowCarQty;
    }

    public String getAllowCitiesText() {
        return allowCitiesText;
    }

    public void setAllowCitiesText(String allowCitiesText) {
        this.allowCitiesText = allowCitiesText;
    }

    public String getAllowCarLevelsText() {
        return allowCarLevelsText;
    }

    public void setAllowCarLevelsText(String allowCarLevelsText) {
        this.allowCarLevelsText = allowCarLevelsText;
    }

    public String getAllowCarSourceText() {
        return allowCarSourceText;
    }

    public void setAllowCarSourceText(String allowCarSourceText) {
        this.allowCarSourceText = allowCarSourceText;
    }

    public Long getCreateor() {
        return createor;
    }

    public void setCreateor(Long createor) {
        this.createor = createor;
    }

    public Long getUpdater() {
        return updater;
    }

    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(String couponNum) {
        this.couponNum = couponNum;
    }

    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Date usedDate) {
        this.usedDate = usedDate;
    }

    public Short getUsedMoney() {
        return usedMoney;
    }

    public void setUsedMoney(Short usedMoney) {
        this.usedMoney = usedMoney;
    }

    public String getCouponSign() {
        return couponSign;
    }

    public void setCouponSign(String couponSign) {
        this.couponSign = couponSign;
    }
}