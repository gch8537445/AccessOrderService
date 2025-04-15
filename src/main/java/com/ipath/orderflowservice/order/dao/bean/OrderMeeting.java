package com.ipath.orderflowservice.order.dao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMeeting {
    private Long id;

    private Long orderId;

    private Long meetingId;

    private Date createTime;


}