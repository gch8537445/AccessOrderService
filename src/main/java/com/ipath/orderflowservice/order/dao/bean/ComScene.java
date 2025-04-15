package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ComScene implements Serializable {
    private Long id;

    private Long companyId;

    private Long managerId;

    private String code;

    private String nameCn;

    private String nameEn;

    private Integer state;

    private Boolean isDelete;

    private Long deleteUserId;

    private Date deleteTime;

    private Long createor;

    private Date createdTime;

    private Long updater;

    private Date updatedTime;

    private String desc;

    private Long departmentId;

    private Long wfReProcdefId;

    private Boolean isNeedApproval;

    private Short approvalType;

    private Long accountId;

    private BigDecimal sceneAmount;

    private BigDecimal sceneUsedAmount;
    private String customInfo;

    private String itemValue;

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

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Long getDeleteUserId() {
        return deleteUserId;
    }

    public void setDeleteUserId(Long deleteUserId) {
        this.deleteUserId = deleteUserId;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getWfReProcdefId() {
        return wfReProcdefId;
    }

    public void setWfReProcdefId(Long wfReProcdefId) {
        this.wfReProcdefId = wfReProcdefId;
    }

    public Boolean getIsNeedApproval() {
        return isNeedApproval;
    }

    public void setIsNeedApproval(Boolean isNeedApproval) {
        this.isNeedApproval = isNeedApproval;
    }

    public Short getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(Short approvalType) {
        this.approvalType = approvalType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getSceneAmount() {
        return sceneAmount;
    }

    public void setSceneAmount(BigDecimal sceneAmount) {
        this.sceneAmount = sceneAmount;
    }

    public BigDecimal getSceneUsedAmount() {
        return sceneUsedAmount;
    }

    public void setSceneUsedAmount(BigDecimal sceneUsedAmount) {
        this.sceneUsedAmount = sceneUsedAmount;
    }

    public String getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(String customInfo) {
        this.customInfo = customInfo;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
}