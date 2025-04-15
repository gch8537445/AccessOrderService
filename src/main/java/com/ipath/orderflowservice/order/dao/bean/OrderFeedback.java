package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;
import java.util.Date;

public class OrderFeedback implements Serializable {
    private Long id;

    private Long orderId;

    private String feedback;

    private String evaluateLabels;

    private Integer score;

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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEvaluateLabels() {
        return evaluateLabels;
    }

    public void setEvaluateLabels(String evaluateLabels) {
        this.evaluateLabels = evaluateLabels;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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