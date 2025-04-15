package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserOrder implements Serializable {
    private Long id;

    private Long userId;

    private Long recentOrderId;

    private Object recentOrderParam;

    private Integer totalOrderCount;

    private BigDecimal totalOrderAmount;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

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

    public Long getRecentOrderId() {
        return recentOrderId;
    }

    public void setRecentOrderId(Long recentOrderId) {
        this.recentOrderId = recentOrderId;
    }

    public Object getRecentOrderParam() {
        return recentOrderParam;
    }

    public void setRecentOrderParam(Object recentOrderParam) {
        this.recentOrderParam = recentOrderParam;
    }

    public Integer getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Integer totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}