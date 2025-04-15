package com.ipath.orderflowservice.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CoreOrderDetailOrder {
    private Short status;
    /** 服务类型：1: 实时，2：预约，6：接机 */
    private Short serviceType;
    /** 订单所在城市id */
    private Long cityId;
    /** 终点城市id */
    private Long endCityId;
    private String passengerPhone;              // 乘客手机号
    private CoreOrderDetailOrderLocation startLocation;
    private CoreOrderDetailOrderLocation endLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date orderTime;
}
