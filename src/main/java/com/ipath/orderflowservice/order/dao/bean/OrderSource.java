package com.ipath.orderflowservice.order.dao.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderSource implements Serializable {
    private Long id;

    private Long userId;

    private Long orderId;

    private String sourceCode;

    private String sourceNameCn;

    private String sourceNameEn;

    private String coreOrderId;

    private BigDecimal estimatePrice;

    private BigDecimal amount;

    private Object feeDetail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date driverOrderTakingTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date driverArrivalTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelBeginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date driverCancelTime;

    private Boolean isChangeDest;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date updateTime;

    private String driverName;

    private String driverPhone;

    private String driverPhoneVirtual;

    private String vehicleModel;

    private String vehicleColor;

    private String vehicleNo;

    private String driverLevel;

    private String driverAvatar;

    private BigDecimal travelDistance;

    private Short state;

    private String actualDestLocation;

    private String actualPickupLocation;

    private Integer carLevel;

    /**
     * 预估距离 (米)
     */
    private Integer estimateDistance;

    /**
     * 预估时间 (秒)
     */
    private Integer estimateTime;

    private String actualDestLat;

    private String actualDestLng;

    private String actualPickupLat;

    private String actualPickupLng;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date orderTime;

    private BigDecimal duration;

    private BigDecimal userBearAmount;

    private BigDecimal companyBearAmount;

    private Integer takeDistance;

    private Integer takeTime;

    /**
     * subCarType
     */
    private String rawCarTypeCode;

    /**
     * labelCode
     */
    private String spCarTypeCode;

    private BigDecimal ipathEstimatePrice;

    private BigDecimal platformEstimatePrice;

    private static final long serialVersionUID = 1L;

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceNameCn() {
        return sourceNameCn;
    }

    public void setSourceNameCn(String sourceNameCn) {
        this.sourceNameCn = sourceNameCn;
    }

    public String getSourceNameEn() {
        return sourceNameEn;
    }

    public void setSourceNameEn(String sourceNameEn) {
        this.sourceNameEn = sourceNameEn;
    }

    public String getCoreOrderId() {
        return coreOrderId;
    }

    public void setCoreOrderId(String coreOrderId) {
        this.coreOrderId = coreOrderId;
    }

    public BigDecimal getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(BigDecimal estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Object getFeeDetail() {
        return feeDetail;
    }

    public void setFeeDetail(Object feeDetail) {
        this.feeDetail = feeDetail;
    }

    public Date getDriverOrderTakingTime() {
        return driverOrderTakingTime;
    }

    public void setDriverOrderTakingTime(Date driverOrderTakingTime) {
        this.driverOrderTakingTime = driverOrderTakingTime;
    }

    public Date getDriverArrivalTime() {
        return driverArrivalTime;
    }

    public void setDriverArrivalTime(Date driverArrivalTime) {
        this.driverArrivalTime = driverArrivalTime;
    }

    public Date getTravelBeginTime() {
        return travelBeginTime;
    }

    public void setTravelBeginTime(Date travelBeginTime) {
        this.travelBeginTime = travelBeginTime;
    }

    public Date getTravelEndTime() {
        return travelEndTime;
    }

    public void setTravelEndTime(Date travelEndTime) {
        this.travelEndTime = travelEndTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getDriverCancelTime() {
        return driverCancelTime;
    }

    public void setDriverCancelTime(Date driverCancelTime) {
        this.driverCancelTime = driverCancelTime;
    }

    public Boolean getIsChangeDest() {
        return isChangeDest;
    }

    public void setIsChangeDest(Boolean isChangeDest) {
        this.isChangeDest = isChangeDest;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverPhoneVirtual() {
        return driverPhoneVirtual;
    }

    public void setDriverPhoneVirtual(String driverPhoneVirtual) {
        this.driverPhoneVirtual = driverPhoneVirtual;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDriverLevel() {
        return driverLevel;
    }

    public void setDriverLevel(String driverLevel) {
        this.driverLevel = driverLevel;
    }

    public String getDriverAvatar() {
        return driverAvatar;
    }

    public void setDriverAvatar(String driverAvatar) {
        this.driverAvatar = driverAvatar;
    }

    public BigDecimal getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(BigDecimal travelDistance) {
        this.travelDistance = travelDistance;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getActualDestLocation() {
        return actualDestLocation;
    }

    public void setActualDestLocation(String actualDestLocation) {
        this.actualDestLocation = actualDestLocation;
    }

    public String getActualPickupLocation() {
        return actualPickupLocation;
    }

    public void setActualPickupLocation(String actualPickupLocation) {
        this.actualPickupLocation = actualPickupLocation;
    }

    public Integer getCarLevel() {
        return carLevel;
    }

    public void setCarLevel(Integer carLevel) {
        this.carLevel = carLevel;
    }

    public Integer getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(Integer estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public Integer getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(Integer estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getActualDestLat() {
        return actualDestLat;
    }

    public void setActualDestLat(String actualDestLat) {
        this.actualDestLat = actualDestLat;
    }

    public String getActualDestLng() {
        return actualDestLng;
    }

    public void setActualDestLng(String actualDestLng) {
        this.actualDestLng = actualDestLng;
    }

    public String getActualPickupLat() {
        return actualPickupLat;
    }

    public void setActualPickupLat(String actualPickupLat) {
        this.actualPickupLat = actualPickupLat;
    }

    public String getActualPickupLng() {
        return actualPickupLng;
    }

    public void setActualPickupLng(String actualPickupLng) {
        this.actualPickupLng = actualPickupLng;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getUserBearAmount() {
        return userBearAmount;
    }

    public void setUserBearAmount(BigDecimal userBearAmount) {
        this.userBearAmount = userBearAmount;
    }

    public BigDecimal getCompanyBearAmount() {
        return companyBearAmount;
    }

    public void setCompanyBearAmount(BigDecimal companyBearAmount) {
        this.companyBearAmount = companyBearAmount;
    }

    public Integer getTaskDistance() {
        return takeDistance;
    }

    public void setTaskDistance(Integer takeDistance) {
        this.takeDistance = takeDistance;
    }

    public Integer getTaskTime() {
        return takeTime;
    }

    public void setTaskTime(Integer takeTime) {
        this.takeTime = takeTime;
    }

    public String getRawCarTypeCode() {
        return rawCarTypeCode;
    }

    public void setRawCarTypeCode(String rawCarTypeCode) {
        this.rawCarTypeCode = rawCarTypeCode == null ? null : rawCarTypeCode.trim();
    }

    public String getSpCarTypeCode() {
        return spCarTypeCode;
    }

    public void setSpCarTypeCode(String spCarTypeCode) {
        this.spCarTypeCode = spCarTypeCode == null ? null : spCarTypeCode.trim();
    }

    public BigDecimal getIpathEstimatePrice() {
        return ipathEstimatePrice;
    }

    public void setIpathEstimatePrice(BigDecimal ipathEstimatePrice) {
        this.ipathEstimatePrice = ipathEstimatePrice;
    }

    public BigDecimal getPlatformEstimatePrice() {
        return platformEstimatePrice;
    }

    public void setPlatformEstimatePrice(BigDecimal platformEstimatePrice) {
        this.platformEstimatePrice = platformEstimatePrice;
    }
}