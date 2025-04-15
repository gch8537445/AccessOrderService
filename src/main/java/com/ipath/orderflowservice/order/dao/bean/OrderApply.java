package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderApply implements Serializable {
    private Long id;

    private Long orderId;

    private Short state;

    private String approverUserIds;

    private Date createTime;

    private Long userId;

    private BigDecimal amount;

    private Boolean warning;

    private String warningRemind;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getApproverUserIds() {
        return approverUserIds;
    }

    public void setApproverUserIds(String approverUserIds) {
        this.approverUserIds = approverUserIds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    public String getWarningRemind() {
        return warningRemind;
    }

    public void setWarningRemind(String warningRemind) {
        this.warningRemind = warningRemind;
    }
}