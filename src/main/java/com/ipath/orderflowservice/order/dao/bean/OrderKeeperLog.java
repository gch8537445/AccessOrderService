package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.util.Date;

public class OrderKeeperLog implements Serializable {
    private Long id;

    private Long orderKeeperId;

    private Integer state;

    private Long newOrderChannelId;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderKeeperId() {
        return orderKeeperId;
    }

    public void setOrderKeeperId(Long orderKeeperId) {
        this.orderKeeperId = orderKeeperId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getNewOrderChannelId() {
        return newOrderChannelId;
    }

    public void setNewOrderChannelId(Long newOrderChannelId) {
        this.newOrderChannelId = newOrderChannelId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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