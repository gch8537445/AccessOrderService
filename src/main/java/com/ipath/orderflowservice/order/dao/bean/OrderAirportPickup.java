package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.util.Date;

public class OrderAirportPickup implements Serializable {
    private Long id;

    private Long orderId;

    private Integer state;

    private Integer cancelReason;

    private String conciergeName;

    private String conciergeCode;

    private String conciergePhone;

    private String conciergeAvatarUrl;

    private Date createTime;

    private Date updateTime;

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getConciergeName() {
        return conciergeName;
    }

    public void setConciergeName(String conciergeName) {
        this.conciergeName = conciergeName;
    }

    public String getConciergeCode() {
        return conciergeCode;
    }

    public void setConciergeCode(String conciergeCode) {
        this.conciergeCode = conciergeCode;
    }

    public String getConciergePhone() {
        return conciergePhone;
    }

    public void setConciergePhone(String conciergePhone) {
        this.conciergePhone = conciergePhone;
    }

    public String getConciergeAvatarUrl() {
        return conciergeAvatarUrl;
    }

    public void setConciergeAvatarUrl(String conciergeAvatarUrl) {
        this.conciergeAvatarUrl = conciergeAvatarUrl;
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
}