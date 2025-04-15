package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.util.Date;

public class CostCenter implements Serializable {
    private Long id;

    private Long companyId;

    private String costcenterName;

    private String costcenterCode;

    private Boolean isDelete;

    private Integer activityState;

    private Long createor;

    private Date createdTime;

    private Long updater;

    private Date updatedTime;

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

    public String getCostcenterName() {
        return costcenterName;
    }

    public void setCostcenterName(String costcenterName) {
        this.costcenterName = costcenterName;
    }

    public String getCostcenterCode() {
        return costcenterCode;
    }

    public void setCostcenterCode(String costcenterCode) {
        this.costcenterCode = costcenterCode;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getActivityState() {
        return activityState;
    }

    public void setActivityState(Integer activityState) {
        this.activityState = activityState;
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
}