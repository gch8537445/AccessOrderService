package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AmgenPo implements Serializable {
    private Long id;

    private Long companyId;

    private String poCode;

    private Date validFrom;

    private Date validTo;

    private BigDecimal poBudget;

    private BigDecimal poRemainQuota;

    private Integer sendEmail;

    private Boolean isDelete;

    private Long deleteUserId;

    private Date deleteTime;

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

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public BigDecimal getPoBudget() {
        return poBudget;
    }

    public void setPoBudget(BigDecimal poBudget) {
        this.poBudget = poBudget;
    }

    public BigDecimal getPoRemainQuota() {
        return poRemainQuota;
    }

    public void setPoRemainQuota(BigDecimal poRemainQuota) {
        this.poRemainQuota = poRemainQuota;
    }

    public Integer getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Integer sendEmail) {
        this.sendEmail = sendEmail;
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
}