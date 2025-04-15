package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserBase implements Serializable {
    private Long id;

    private Long companyId;

    private String nameCn;

    private String nameEn;

    private String staffCode;

    private String socialAppUserId;

    private String deviceId;

    private String phone;

    private String email;

    private Short gender;

    private String jobTitle;

    private String workPlace;

    private String avatar;

    private Integer jobState;

    private Date leaveDate;

    private String lineManagerSocialAppUserId;

    private Short activityState;

    private Date createTime;

    private Date updateTime;

    private Boolean delete;

    private String emergencyPhone;

    private Boolean canUseCar;

    private Short useCarMode;

    private Short language;

    private Long lineManagerId;

    private Integer socialAppType;

    private String lineManagerName;

    private Boolean isManagerChanged;

    private Boolean acceptAgreement;

    private String remark;

    private Boolean acceptBusinessAgreement;

    private Long accountId;

    private String mobileMenu;

    private BigDecimal personalQuota;

    private BigDecimal remainQuota;

    private String backupField1;

    private String backupField2;

    private String backupField3;

    private String backupField4;

    private String backupField5;

    private String backupField6;

    private String backupField7;

    private String backupField8;

    private String backupField9;

    private String backupField10;

    private String userId;

    private String userCode;

    private String managerCode;

    private Boolean lowCarbon;

    private Boolean isLetter;

    private String customInfo;

    private Boolean isPrivacy;

    //客户免密支付协议ID
    private String wxEntrustpayId;
    //客户免密支付签约状态。0：已签约1：未签约  9：签约进行中
    private Integer wxEntrustpayState;

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

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getSocialAppUserId() {
        return socialAppUserId;
    }

    public void setSocialAppUserId(String socialAppUserId) {
        this.socialAppUserId = socialAppUserId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getJobState() {
        return jobState;
    }

    public void setJobState(Integer jobState) {
        this.jobState = jobState;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLineManagerSocialAppUserId() {
        return lineManagerSocialAppUserId;
    }

    public void setLineManagerSocialAppUserId(String lineManagerSocialAppUserId) {
        this.lineManagerSocialAppUserId = lineManagerSocialAppUserId;
    }

    public Short getActivityState() {
        return activityState;
    }

    public void setActivityState(Short activityState) {
        this.activityState = activityState;
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

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public Boolean getCanUseCar() {
        return canUseCar;
    }

    public void setCanUseCar(Boolean canUseCar) {
        this.canUseCar = canUseCar;
    }

    public Short getUseCarMode() {
        return useCarMode;
    }

    public void setUseCarMode(Short useCarMode) {
        this.useCarMode = useCarMode;
    }

    public Short getLanguage() {
        return language;
    }

    public void setLanguage(Short language) {
        this.language = language;
    }

    public Long getLineManagerId() {
        return lineManagerId;
    }

    public void setLineManagerId(Long lineManagerId) {
        this.lineManagerId = lineManagerId;
    }

    public Integer getSocialAppType() {
        return socialAppType;
    }

    public void setSocialAppType(Integer socialAppType) {
        this.socialAppType = socialAppType;
    }

    public String getLineManagerName() {
        return lineManagerName;
    }

    public void setLineManagerName(String lineManagerName) {
        this.lineManagerName = lineManagerName;
    }

    public Boolean getIsManagerChanged() {
        return isManagerChanged;
    }

    public void setIsManagerChanged(Boolean isManagerChanged) {
        this.isManagerChanged = isManagerChanged;
    }

    public Boolean getAcceptAgreement() {
        return acceptAgreement;
    }

    public void setAcceptAgreement(Boolean acceptAgreement) {
        this.acceptAgreement = acceptAgreement;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getAcceptBusinessAgreement() {
        return acceptBusinessAgreement;
    }

    public void setAcceptBusinessAgreement(Boolean acceptBusinessAgreement) {
        this.acceptBusinessAgreement = acceptBusinessAgreement;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getMobileMenu() {
        return mobileMenu;
    }

    public void setMobileMenu(String mobileMenu) {
        this.mobileMenu = mobileMenu;
    }

    public BigDecimal getPersonalQuota() {
        return personalQuota;
    }

    public void setPersonalQuota(BigDecimal personalQuota) {
        this.personalQuota = personalQuota;
    }

    public BigDecimal getRemainQuota() {
        return remainQuota;
    }

    public void setRemainQuota(BigDecimal remainQuota) {
        this.remainQuota = remainQuota;
    }

    public String getBackupField1() {
        return backupField1;
    }

    public void setBackupField1(String backupField1) {
        this.backupField1 = backupField1;
    }

    public String getBackupField2() {
        return backupField2;
    }

    public void setBackupField2(String backupField2) {
        this.backupField2 = backupField2;
    }

    public String getBackupField3() {
        return backupField3;
    }

    public void setBackupField3(String backupField3) {
        this.backupField3 = backupField3;
    }

    public String getBackupField4() {
        return backupField4;
    }

    public void setBackupField4(String backupField4) {
        this.backupField4 = backupField4;
    }

    public String getBackupField5() {
        return backupField5;
    }

    public void setBackupField5(String backupField5) {
        this.backupField5 = backupField5;
    }

    public String getBackupField6() {
        return backupField6;
    }

    public void setBackupField6(String backupField6) {
        this.backupField6 = backupField6;
    }

    public String getBackupField7() {
        return backupField7;
    }

    public void setBackupField7(String backupField7) {
        this.backupField7 = backupField7;
    }

    public String getBackupField8() {
        return backupField8;
    }

    public void setBackupField8(String backupField8) {
        this.backupField8 = backupField8;
    }

    public String getBackupField9() {
        return backupField9;
    }

    public void setBackupField9(String backupField9) {
        this.backupField9 = backupField9;
    }

    public String getBackupField10() {
        return backupField10;
    }

    public void setBackupField10(String backupField10) {
        this.backupField10 = backupField10;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    public Boolean getLowCarbon() {
        return lowCarbon;
    }

    public void setLowCarbon(Boolean lowCarbon) {
        this.lowCarbon = lowCarbon;
    }

    public Boolean getIsLetter() {
        return isLetter;
    }

    public void setIsLetter(Boolean isLetter) {
        this.isLetter = isLetter;
    }

    public String getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(String customInfo) {
        this.customInfo = customInfo;
    }

    public Boolean getIsPrivacy() {
        return isPrivacy;
    }

    public void setIsPrivacy(Boolean isPrivacy) {
        this.isPrivacy = isPrivacy;
    }

    public Boolean getManagerChanged() {
        return isManagerChanged;
    }

    public void setManagerChanged(Boolean managerChanged) {
        isManagerChanged = managerChanged;
    }

    public Boolean getLetter() {
        return isLetter;
    }

    public void setLetter(Boolean letter) {
        isLetter = letter;
    }

    public Boolean getPrivacy() {
        return isPrivacy;
    }

    public void setPrivacy(Boolean privacy) {
        isPrivacy = privacy;
    }

    public String getWxEntrustpayId() {
        return wxEntrustpayId;
    }

    public void setWxEntrustpayId(String wxEntrustpayId) {
        this.wxEntrustpayId = wxEntrustpayId;
    }

    public Integer getWxEntrustpayState() {
        return wxEntrustpayState;
    }

    public void setWxEntrustpayState(Integer wxEntrustpayState) {
        this.wxEntrustpayState = wxEntrustpayState;
    }
}