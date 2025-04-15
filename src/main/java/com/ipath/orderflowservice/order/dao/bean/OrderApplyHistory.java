package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderApplyHistory implements Serializable {
    private Long id;

    private Long orderId;

    private Short state;

    private String reason;

    private Boolean isAutoApprove;

    private Long approverUserId;

    private Date createTime;

    private String useCarReason;

    private BigDecimal amount;

    private Long userId;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getIsAutoApprove() {
        return isAutoApprove;
    }

    public void setIsAutoApprove(Boolean isAutoApprove) {
        this.isAutoApprove = isAutoApprove;
    }

    public Long getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(Long approverUserId) {
        this.approverUserId = approverUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUseCarReason() {
        return useCarReason;
    }

    public void setUseCarReason(String useCarReason) {
        this.useCarReason = useCarReason;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}