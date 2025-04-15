package com.ipath.orderflowservice.feignclient.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**配置服务消息对象 */
@Data
public class ConfigurationMsgDto {
    /**
     * 事件类型
     * 1-下单成功  2-司机已接单  3-司机到达 5-行程结束  6-订单完成  7-订单取消  8-修改目的地  9-改派
     */
    private Short eventType;
    /**
     * 前一状态
     */
    private Short previousOrderState;

    /** 订单id */
    private Long orderId;

     /**预估价 */
     private BigDecimal estimatePrice;

    /** 场景id */
    private Long sceneId;

    /** order source id */
    private Long orderSourceId;

    /**公司id */
    private Long companyId;

    /**用户id */
    private Long userId;

    /**起点名称 */
    private String pickupLocationName;

    /**起点地址 */
    private String pickupLocation;

     /**终点名称 */
    private String destLocationName;

    /**终点地址 */
    private String destLocation;

    /**出发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;
   
    /**用户手机号 */
    private String userPhone;
    
    /**用户虚拟号 */
    private String userPhoneVirtual;

    /**乘客手机号 */
    private String passengerPhone;

    /**乘客虚拟号 */
    private String passengerPhoneVirtual;

    /**自定义信息：手机号 */
    private String customPhone;
    
    /**服务类型 1-实时 2-预约 6-接机 7-接站 20-包车  */
    private Short serviceType;
    
    /**司机姓名 */
    private String driverName;

    /**司机电话 */
    private String driverPhone;

    /**司机虚拟电话（乘车人用） */
    private String driverPhoneVirtual;

    /**司机虚拟电话（叫车人用） */
    private String phoneForUser;

    /**车型 */
    private String vehicleModel;

    /**车号 */
    private String vehicleNo;

    /**车辆颜色 */
    private String vehicleColor;

    /**司机level */
    private String driverLevel;

    /**运力平台名称 */
    private String sourceName;

    /**行驶里程（公里） */
    private BigDecimal distance;

    /**行程开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelBeginTime;

    /**行程结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelEndTime;

    /**企业承担金额 */
    private BigDecimal companyBearAmount;

    /**用户承担金额 */
    private BigDecimal userBearAmount;
}
