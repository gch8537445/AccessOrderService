package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.Date;

/**
 * 投诉
 */
@Data
public class RequestBillNotifyComplaintDto{
    private Long id;

    private Long companyId;

    private Long orderId;

    private Integer typeId;

    private String feedback;

    private Short feedbackTypeId;

    private String reply;

    private Integer state;

    private Long acceptorId;

    private Long closeUserId;

    private Short score;

    private Integer replyTypeId;

    private String customerServiceRemark;

    private Integer level;

    private Date acceptTime;

    private Date closeTime;

    private Date createTime;

    private Date updateTime;

    private Object complaintLabels;

    private Integer degree;

    private Integer source;

    private String reason;

    private String remarks;
}
