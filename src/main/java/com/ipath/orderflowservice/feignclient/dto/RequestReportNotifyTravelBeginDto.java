package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.DriverInfoNotifyReport;
import com.ipath.orderflowservice.order.bean.param.VehicleInfo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RequestReportNotifyTravelBeginDto extends RequestReportNotifyBaseDto{
    /**
     * 环境id
     */
    private String envId;
    /**
     * 交易单id
     */
    private String transOrderId;
    /**
     * 子交易单id
     */
    private String subTransOrderId;
    /**
     * 运力平台订单id
     */
    private String platformOrderId;
    /**
     * 公司主键
     */
    private Long companyId;
    /**
     * 司机接单时间
     * 根据此值和orderTime计算ke_order_duration_s和ke_order_duration_m值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date driverTakingTime;
    /**
     * 运力平台
     */
    private Integer carSource;
    /**
     * 车型等级
     */
    private Integer carLevel;
    /**
     * 是否是升舱订单
     */
    private Boolean isUpgrade;
    /**
     * 司机信息
     */
    private DriverInfoNotifyReport driver;
    /**
     * 车辆信息
     */
    private VehicleInfo vehicle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelBeginTime;
}
