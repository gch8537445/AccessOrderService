package com.ipath.orderflowservice.order.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DriverPositionVo {
    private Long orderId;
    private String lng;
    private String lat;
    private String time;
}
