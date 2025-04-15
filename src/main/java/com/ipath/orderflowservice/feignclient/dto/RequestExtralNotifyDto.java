package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 通知extral-core-service基类
 */
@Data
public class RequestExtralNotifyDto {
    /**
     * 事件类型
     * 1,下单成功  2,司机已接单  3,司机到达 5,行程结束  6,订单完成  7,订单取消
     */
    private Short eventType;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 增值服务id列表
     */
    private List<Long> extraServiceIds;

    public Long userId;
    public Long companyId;
    public BigDecimal distance;
    public String pickupLocationName;
    /**
     * 终点名称
     */
    private String destLocationName;
    /**
     * 运力平台名称
     */
    private String sourceName;
    /**
     * 中台订单id
     */
    private String coreOrderId;
    /**
     * 行程开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelBeginTime;/**
     * 车型
     */
    private String vehicleModel;
    /**
     * 车号
     */
    private String vehicleNo;
    /**
     * 乘客姓名
     */
    private String passengerName;
    /**
     * 乘客手机号
     */
    private String passengerPhone;
    /**
     * 紧急联系电话
     */
    private String emergencyPhone;
    /**
     * 用户名称
     */
    private String userName;
}
