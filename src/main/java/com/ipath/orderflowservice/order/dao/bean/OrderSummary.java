package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderSummary implements Serializable {
    private Long id;

    private Long coreOrderId;

    private String envId;

    private String platformOrderId;

    private String transOrderId;

    private String subTransOrderId;

    private Long companyId;

    private String companyName;

    private Short orderType;

    private Short actualCarSourceCode;

    private String actualCarSourceName;

    private Short actualCarLevel;

    private String actualCarLevelName;

    private String cityCode;

    private String cityName;

    private String mappingCityCode;

    private String mappingCityName;

    private String destCityCode;

    private String destCityName;

    private String pickupLocation;

    private String pickupLocationName;

    private String pickupLat;

    private String pickupLng;

    private String destLocation;

    private String destLocationName;

    private String destLat;

    private String destLng;

    private Boolean isChangeDest;

    private String changeDestTime;

    private String changeDestLocation;

    private String changeDestLat;

    private String changeDestLng;

    private String actualPickupLocation;

    private String actualPickupLocationName;

    private String actualPickupLat;

    private String actualPickupLng;

    private String actualDestLocation;

    private String actualDestLocationName;

    private String actualDestLat;

    private String actualDestLng;

    private Long userId;

    private String userName;

    private String userPhone;

    private String userEmergencyPhone;

    private String passenger;

    private String passengerPhone;

    private BigDecimal estimatePrice;

    private BigDecimal totalAmount;

    private BigDecimal extralAmount;

    private BigDecimal couponAmount;

    private BigDecimal prepayAmount;

    private BigDecimal prepayRefundAmount;

    private BigDecimal allowInvoiceAmount;

    private BigDecimal personalPayableAmount;

    private BigDecimal personalPayingAmount;

    private BigDecimal personalPaidAmount;

    private BigDecimal companyAmount;

    private BigDecimal personalAmount;

    private BigDecimal actualAmount;

    private BigDecimal otherRefundAmount;

    private String otherRefundReason;

    private Long otherRefundAdminId;

    private String otherRefundAdminName;

    private Date otherRefundTime;

    private String feeDetail;

    private Date orderTime;

    private Date driverTakingTime;

    private Date driverArrivalTime;

    private Date travelBeginTime;

    private Date travelEndTime;

    private Date settleTime;

    private BigDecimal travelDuration;

    private BigDecimal travelDistance;

    private Integer takeOrderDurationS;

    private BigDecimal takeOrderDurationM;

    private Date payTime;

    private Short personalPayStatus;

    private Short status;

    private Date cancelTime;

    private Short cancelType;

    private String cancelReason;

    private Long departmentId;

    private String departmentName;

    private Long sceneId;

    private String sceneName;

    private Long scenePublishId;

    private Long projectId;

    private String projectName;

    private Long accountId;

    private String accountName;

    private Short payType;

    private String useCarReason;

    private String driverName;

    private String driverLevel;

    private String driverPhone;

    private String driverPhoneVirtual;

    private String vehicleModel;

    private String vehicleColor;

    private String vehicleNo;

    private Boolean isEnergyVehicle;

    private Boolean inBill;

    private Date billTime;

    private Boolean isInvoice;

    private Date invoiceTime;

    private String invoiceNo;

    private String customInfo;

    private Boolean isUpgrade;

    private Boolean isPrepay;

    private Long approverId;

    private String approver;

    private Date approvalTime;

    private Short approvalStatus;

    private BigDecimal personalRate;

    private BigDecimal personalServiceFee;

    private Long createor;

    private Date createdTime;

    private Long updater;

    private Date updatedTime;

    private String comment;

    private Boolean isAutoApproval;

    private String prepayRefundReason;

    private Date prepayRefundTime;

    private String invoiceCustomInfo;

    private String departmentManager;

    private String transNo;

    private BigDecimal cfgFeeAmount;

    private String sceneCode;

    private Integer estimateDistance;

    private Boolean isAbnormal;

    private String ipathRemark;

    private Boolean isProxy;

    private Boolean isRelay;

    private String partnerOrderId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoreOrderId() {
        return coreOrderId;
    }

    public void setCoreOrderId(Long coreOrderId) {
        this.coreOrderId = coreOrderId;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public String getPlatformOrderId() {
        return platformOrderId;
    }

    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }

    public String getTransOrderId() {
        return transOrderId;
    }

    public void setTransOrderId(String transOrderId) {
        this.transOrderId = transOrderId;
    }

    public String getSubTransOrderId() {
        return subTransOrderId;
    }

    public void setSubTransOrderId(String subTransOrderId) {
        this.subTransOrderId = subTransOrderId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Short getOrderType() {
        return orderType;
    }

    public void setOrderType(Short orderType) {
        this.orderType = orderType;
    }

    public Short getActualCarSourceCode() {
        return actualCarSourceCode;
    }

    public void setActualCarSourceCode(Short actualCarSourceCode) {
        this.actualCarSourceCode = actualCarSourceCode;
    }

    public String getActualCarSourceName() {
        return actualCarSourceName;
    }

    public void setActualCarSourceName(String actualCarSourceName) {
        this.actualCarSourceName = actualCarSourceName;
    }

    public Short getActualCarLevel() {
        return actualCarLevel;
    }

    public void setActualCarLevel(Short actualCarLevel) {
        this.actualCarLevel = actualCarLevel;
    }

    public String getActualCarLevelName() {
        return actualCarLevelName;
    }

    public void setActualCarLevelName(String actualCarLevelName) {
        this.actualCarLevelName = actualCarLevelName;
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

    public String getMappingCityCode() {
        return mappingCityCode;
    }

    public void setMappingCityCode(String mappingCityCode) {
        this.mappingCityCode = mappingCityCode;
    }

    public String getMappingCityName() {
        return mappingCityName;
    }

    public void setMappingCityName(String mappingCityName) {
        this.mappingCityName = mappingCityName;
    }

    public String getDestCityCode() {
        return destCityCode;
    }

    public void setDestCityCode(String destCityCode) {
        this.destCityCode = destCityCode;
    }

    public String getDestCityName() {
        return destCityName;
    }

    public void setDestCityName(String destCityName) {
        this.destCityName = destCityName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupLocationName() {
        return pickupLocationName;
    }

    public void setPickupLocationName(String pickupLocationName) {
        this.pickupLocationName = pickupLocationName;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getDestLocation() {
        return destLocation;
    }

    public void setDestLocation(String destLocation) {
        this.destLocation = destLocation;
    }

    public String getDestLocationName() {
        return destLocationName;
    }

    public void setDestLocationName(String destLocationName) {
        this.destLocationName = destLocationName;
    }

    public String getDestLat() {
        return destLat;
    }

    public void setDestLat(String destLat) {
        this.destLat = destLat;
    }

    public String getDestLng() {
        return destLng;
    }

    public void setDestLng(String destLng) {
        this.destLng = destLng;
    }

    public Boolean getIsChangeDest() {
        return isChangeDest;
    }

    public void setIsChangeDest(Boolean isChangeDest) {
        this.isChangeDest = isChangeDest;
    }

    public String getChangeDestTime() {
        return changeDestTime;
    }

    public void setChangeDestTime(String changeDestTime) {
        this.changeDestTime = changeDestTime;
    }

    public String getChangeDestLocation() {
        return changeDestLocation;
    }

    public void setChangeDestLocation(String changeDestLocation) {
        this.changeDestLocation = changeDestLocation;
    }

    public String getChangeDestLat() {
        return changeDestLat;
    }

    public void setChangeDestLat(String changeDestLat) {
        this.changeDestLat = changeDestLat;
    }

    public String getChangeDestLng() {
        return changeDestLng;
    }

    public void setChangeDestLng(String changeDestLng) {
        this.changeDestLng = changeDestLng;
    }

    public String getActualPickupLocation() {
        return actualPickupLocation;
    }

    public void setActualPickupLocation(String actualPickupLocation) {
        this.actualPickupLocation = actualPickupLocation;
    }

    public String getActualPickupLocationName() {
        return actualPickupLocationName;
    }

    public void setActualPickupLocationName(String actualPickupLocationName) {
        this.actualPickupLocationName = actualPickupLocationName;
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

    public String getActualDestLocation() {
        return actualDestLocation;
    }

    public void setActualDestLocation(String actualDestLocation) {
        this.actualDestLocation = actualDestLocation;
    }

    public String getActualDestLocationName() {
        return actualDestLocationName;
    }

    public void setActualDestLocationName(String actualDestLocationName) {
        this.actualDestLocationName = actualDestLocationName;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmergencyPhone() {
        return userEmergencyPhone;
    }

    public void setUserEmergencyPhone(String userEmergencyPhone) {
        this.userEmergencyPhone = userEmergencyPhone;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public BigDecimal getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(BigDecimal estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getExtralAmount() {
        return extralAmount;
    }

    public void setExtralAmount(BigDecimal extralAmount) {
        this.extralAmount = extralAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getPrepayAmount() {
        return prepayAmount;
    }

    public void setPrepayAmount(BigDecimal prepayAmount) {
        this.prepayAmount = prepayAmount;
    }

    public BigDecimal getPrepayRefundAmount() {
        return prepayRefundAmount;
    }

    public void setPrepayRefundAmount(BigDecimal prepayRefundAmount) {
        this.prepayRefundAmount = prepayRefundAmount;
    }

    public BigDecimal getAllowInvoiceAmount() {
        return allowInvoiceAmount;
    }

    public void setAllowInvoiceAmount(BigDecimal allowInvoiceAmount) {
        this.allowInvoiceAmount = allowInvoiceAmount;
    }

    public BigDecimal getPersonalPayableAmount() {
        return personalPayableAmount;
    }

    public void setPersonalPayableAmount(BigDecimal personalPayableAmount) {
        this.personalPayableAmount = personalPayableAmount;
    }

    public BigDecimal getPersonalPayingAmount() {
        return personalPayingAmount;
    }

    public void setPersonalPayingAmount(BigDecimal personalPayingAmount) {
        this.personalPayingAmount = personalPayingAmount;
    }

    public BigDecimal getPersonalPaidAmount() {
        return personalPaidAmount;
    }

    public void setPersonalPaidAmount(BigDecimal personalPaidAmount) {
        this.personalPaidAmount = personalPaidAmount;
    }

    public BigDecimal getCompanyAmount() {
        return companyAmount;
    }

    public void setCompanyAmount(BigDecimal companyAmount) {
        this.companyAmount = companyAmount;
    }

    public BigDecimal getPersonalAmount() {
        return personalAmount;
    }

    public void setPersonalAmount(BigDecimal personalAmount) {
        this.personalAmount = personalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getOtherRefundAmount() {
        return otherRefundAmount;
    }

    public void setOtherRefundAmount(BigDecimal otherRefundAmount) {
        this.otherRefundAmount = otherRefundAmount;
    }

    public String getOtherRefundReason() {
        return otherRefundReason;
    }

    public void setOtherRefundReason(String otherRefundReason) {
        this.otherRefundReason = otherRefundReason;
    }

    public Long getOtherRefundAdminId() {
        return otherRefundAdminId;
    }

    public void setOtherRefundAdminId(Long otherRefundAdminId) {
        this.otherRefundAdminId = otherRefundAdminId;
    }

    public String getOtherRefundAdminName() {
        return otherRefundAdminName;
    }

    public void setOtherRefundAdminName(String otherRefundAdminName) {
        this.otherRefundAdminName = otherRefundAdminName;
    }

    public Date getOtherRefundTime() {
        return otherRefundTime;
    }

    public void setOtherRefundTime(Date otherRefundTime) {
        this.otherRefundTime = otherRefundTime;
    }

    public String getFeeDetail() {
        return feeDetail;
    }

    public void setFeeDetail(String feeDetail) {
        this.feeDetail = feeDetail;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getDriverTakingTime() {
        return driverTakingTime;
    }

    public void setDriverTakingTime(Date driverTakingTime) {
        this.driverTakingTime = driverTakingTime;
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

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public BigDecimal getTravelDuration() {
        return travelDuration;
    }

    public void setTravelDuration(BigDecimal travelDuration) {
        this.travelDuration = travelDuration;
    }

    public BigDecimal getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(BigDecimal travelDistance) {
        this.travelDistance = travelDistance;
    }

    public Boolean getChangeDest() {
        return isChangeDest;
    }

    public void setChangeDest(Boolean changeDest) {
        isChangeDest = changeDest;
    }

    public Integer getTakeOrderDurationS() {
        return takeOrderDurationS;
    }

    public void setTakeOrderDurationS(Integer takeOrderDurationS) {
        this.takeOrderDurationS = takeOrderDurationS;
    }

    public Boolean getEnergyVehicle() {
        return isEnergyVehicle;
    }

    public void setEnergyVehicle(Boolean energyVehicle) {
        isEnergyVehicle = energyVehicle;
    }

    public Boolean getInvoice() {
        return isInvoice;
    }

    public void setInvoice(Boolean invoice) {
        isInvoice = invoice;
    }

    public Boolean getUpgrade() {
        return isUpgrade;
    }

    public void setUpgrade(Boolean upgrade) {
        isUpgrade = upgrade;
    }

    public Boolean getPrepay() {
        return isPrepay;
    }

    public void setPrepay(Boolean prepay) {
        isPrepay = prepay;
    }

    public Boolean getAutoApproval() {
        return isAutoApproval;
    }

    public void setAutoApproval(Boolean autoApproval) {
        isAutoApproval = autoApproval;
    }

    public Boolean getAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        isAbnormal = abnormal;
    }

    public Boolean getProxy() {
        return isProxy;
    }

    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    public Boolean getRelay() {
        return isRelay;
    }

    public void setRelay(Boolean relay) {
        isRelay = relay;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getTakeOrderDurationM() {
        return takeOrderDurationM;
    }

    public void setTakeOrderDurationM(BigDecimal takeOrderDurationM) {
        this.takeOrderDurationM = takeOrderDurationM;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Short getPersonalPayStatus() {
        return personalPayStatus;
    }

    public void setPersonalPayStatus(Short personalPayStatus) {
        this.personalPayStatus = personalPayStatus;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Short getCancelType() {
        return cancelType;
    }

    public void setCancelType(Short cancelType) {
        this.cancelType = cancelType;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public Long getScenePublishId() {
        return scenePublishId;
    }

    public void setScenePublishId(Long scenePublishId) {
        this.scenePublishId = scenePublishId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Short getPayType() {
        return payType;
    }

    public void setPayType(Short payType) {
        this.payType = payType;
    }

    public String getUseCarReason() {
        return useCarReason;
    }

    public void setUseCarReason(String useCarReason) {
        this.useCarReason = useCarReason;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLevel() {
        return driverLevel;
    }

    public void setDriverLevel(String driverLevel) {
        this.driverLevel = driverLevel;
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

    public Boolean getIsEnergyVehicle() {
        return isEnergyVehicle;
    }

    public void setIsEnergyVehicle(Boolean isEnergyVehicle) {
        this.isEnergyVehicle = isEnergyVehicle;
    }

    public Boolean getInBill() {
        return inBill;
    }

    public void setInBill(Boolean inBill) {
        this.inBill = inBill;
    }

    public Date getBillTime() {
        return billTime;
    }

    public void setBillTime(Date billTime) {
        this.billTime = billTime;
    }

    public Boolean getIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(Boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    public Date getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(Date invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(String customInfo) {
        this.customInfo = customInfo;
    }

    public Boolean getIsUpgrade() {
        return isUpgrade;
    }

    public void setIsUpgrade(Boolean isUpgrade) {
        this.isUpgrade = isUpgrade;
    }

    public Boolean getIsPrepay() {
        return isPrepay;
    }

    public void setIsPrepay(Boolean isPrepay) {
        this.isPrepay = isPrepay;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Short getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Short approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public BigDecimal getPersonalRate() {
        return personalRate;
    }

    public void setPersonalRate(BigDecimal personalRate) {
        this.personalRate = personalRate;
    }

    public BigDecimal getPersonalServiceFee() {
        return personalServiceFee;
    }

    public void setPersonalServiceFee(BigDecimal personalServiceFee) {
        this.personalServiceFee = personalServiceFee;
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

    public Long getUpdater() {
        return updater;
    }

    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsAutoApproval() {
        return isAutoApproval;
    }

    public void setIsAutoApproval(Boolean isAutoApproval) {
        this.isAutoApproval = isAutoApproval;
    }

    public String getPrepayRefundReason() {
        return prepayRefundReason;
    }

    public void setPrepayRefundReason(String prepayRefundReason) {
        this.prepayRefundReason = prepayRefundReason;
    }

    public Date getPrepayRefundTime() {
        return prepayRefundTime;
    }

    public void setPrepayRefundTime(Date prepayRefundTime) {
        this.prepayRefundTime = prepayRefundTime;
    }

    public String getInvoiceCustomInfo() {
        return invoiceCustomInfo;
    }

    public void setInvoiceCustomInfo(String invoiceCustomInfo) {
        this.invoiceCustomInfo = invoiceCustomInfo;
    }

    public String getDepartmentManager() {
        return departmentManager;
    }

    public void setDepartmentManager(String departmentManager) {
        this.departmentManager = departmentManager;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public BigDecimal getCfgFeeAmount() {
        return cfgFeeAmount;
    }

    public void setCfgFeeAmount(BigDecimal cfgFeeAmount) {
        this.cfgFeeAmount = cfgFeeAmount;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public Integer getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(Integer estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public String getIpathRemark() {
        return ipathRemark;
    }

    public void setIpathRemark(String ipathRemark) {
        this.ipathRemark = ipathRemark;
    }

    public Boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(Boolean isProxy) {
        this.isProxy = isProxy;
    }

    public Boolean getIsRelay() {
        return isRelay;
    }

    public void setIsRelay(Boolean isRelay) {
        this.isRelay = isRelay;
    }

    public String getPartnerOrderId() {
        return partnerOrderId;
    }

    public void setPartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
    }
}