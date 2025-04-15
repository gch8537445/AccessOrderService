package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderPlaceHistoryMonth implements Serializable {
    private Long id;

    private Long companyId;

    private Long accountId;

    private Long sceneId;

    private Long projectId;

    private Long userId;

    private Short serviceType;

    private String carSources;

    private String departCityCode;

    private String departCityName;

    private String pickupLocation;

    private String pickupLocationName;

    private String departLat;

    private String departLng;

    private String destCityCode;

    private String destCityName;

    private String destLocation;

    private String destLocationName;

    private String destLat;

    private String destLng;

    private String passengerName;

    private String passengerPhone;

    private Date departTime;

    private String flightNumber;

    private String flightDepartTime;

    private String flightDelayTime;

    private String departAirportCode;

    private String arrivalAirportCode;

    private Boolean isSendPassengerSms;

    private Date createTime;

    private Date updateTime;

    private Short state;

    private String useCarReason;

    private Boolean isNeedApprove;

    private Long scenePublishId;

    private String sceneNameCn;

    private String sceneNameEn;

    private String remark;

    private Boolean allowChangeDest;

    private Long workFlowId;

    private String userPhone;

    private String departPoi;

    private String destPoi;

    private String cancelReason;

    private Date cancelTime;

    private String customInfo;

    private Long preDepartApplyId;

    private Boolean isUpgrade;

    private Boolean isPrepay;

    private String partnerOrderId;

    private Boolean isAbnormal;

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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Short getServiceType() {
        return serviceType;
    }

    public void setServiceType(Short serviceType) {
        this.serviceType = serviceType;
    }

    public String getCarSources() {
        return carSources;
    }

    public void setCarSources(String carSources) {
        this.carSources = carSources;
    }

    public String getDepartCityCode() {
        return departCityCode;
    }

    public void setDepartCityCode(String departCityCode) {
        this.departCityCode = departCityCode;
    }

    public String getDepartCityName() {
        return departCityName;
    }

    public void setDepartCityName(String departCityName) {
        this.departCityName = departCityName;
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

    public String getDepartLat() {
        return departLat;
    }

    public void setDepartLat(String departLat) {
        this.departLat = departLat;
    }

    public String getDepartLng() {
        return departLng;
    }

    public void setDepartLng(String departLng) {
        this.departLng = departLng;
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

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFlightDepartTime() {
        return flightDepartTime;
    }

    public void setFlightDepartTime(String flightDepartTime) {
        this.flightDepartTime = flightDepartTime;
    }

    public String getFlightDelayTime() {
        return flightDelayTime;
    }

    public void setFlightDelayTime(String flightDelayTime) {
        this.flightDelayTime = flightDelayTime;
    }

    public String getDepartAirportCode() {
        return departAirportCode;
    }

    public void setDepartAirportCode(String departAirportCode) {
        this.departAirportCode = departAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public Boolean getIsSendPassengerSms() {
        return isSendPassengerSms;
    }

    public void setIsSendPassengerSms(Boolean isSendPassengerSms) {
        this.isSendPassengerSms = isSendPassengerSms;
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

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getUseCarReason() {
        return useCarReason;
    }

    public void setUseCarReason(String useCarReason) {
        this.useCarReason = useCarReason;
    }

    public Boolean getIsNeedApprove() {
        return isNeedApprove;
    }

    public void setIsNeedApprove(Boolean isNeedApprove) {
        this.isNeedApprove = isNeedApprove;
    }

    public Long getScenePublishId() {
        return scenePublishId;
    }

    public void setScenePublishId(Long scenePublishId) {
        this.scenePublishId = scenePublishId;
    }

    public String getSceneNameCn() {
        return sceneNameCn;
    }

    public void setSceneNameCn(String sceneNameCn) {
        this.sceneNameCn = sceneNameCn;
    }

    public String getSceneNameEn() {
        return sceneNameEn;
    }

    public void setSceneNameEn(String sceneNameEn) {
        this.sceneNameEn = sceneNameEn;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getAllowChangeDest() {
        return allowChangeDest;
    }

    public void setAllowChangeDest(Boolean allowChangeDest) {
        this.allowChangeDest = allowChangeDest;
    }

    public Long getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(Long workFlowId) {
        this.workFlowId = workFlowId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getDepartPoi() {
        return departPoi;
    }

    public void setDepartPoi(String departPoi) {
        this.departPoi = departPoi;
    }

    public String getDestPoi() {
        return destPoi;
    }

    public void setDestPoi(String destPoi) {
        this.destPoi = destPoi;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(String customInfo) {
        this.customInfo = customInfo;
    }

    public Long getPreDepartApplyId() {
        return preDepartApplyId;
    }

    public void setPreDepartApplyId(Long preDepartApplyId) {
        this.preDepartApplyId = preDepartApplyId;
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

    public String getPartnerOrderId() {
        return partnerOrderId;
    }

    public void setPartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
    }

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }
}