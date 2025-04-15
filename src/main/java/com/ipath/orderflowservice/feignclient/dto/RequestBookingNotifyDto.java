package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 通知booking-service dto
 */
@Data
public class RequestBookingNotifyDto {
    /**
     * 所属账户id
     */
    private Long accountId;

    /**
     * 车型
     */
    private Short carType;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 用车时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;

    /**
     * 起始地城市代码
     */
    private String departCityCode;

    /**
     * 起始地城市名称
     */
    private String departCityName;

    /**
     * 起始地纬度
     */
    private String departLat;

    /**
     * 起始地经度
     */
    private String departLng;

    /**
     * 起始地详细地址
     */
    private String departLocation;

    /**
     * 起始地名称
     */
    private String departLocationName;

    /**
     * 起始地poi
     */
    private String departPoi;

    /**
     * 目的地城市code
     */
    private String destCityCode;

    /**
     * 目的地城市名称
     */
    private String destCityName;

    /**
     * 目的地纬度
     */
    private String destLat;

    /**
     * 目的地经度
     */
    private String destLng;

    /**
     * 目的地详细地址
     */
    private String destLocation;

    /**
     * 目的地名称
     */
    private String destLocationName;

    /**
     * 目的地poi
     */
    private String destPoi;

    /**
     * 乘客手机号
     */
    private String passengerName;

    /**
     * 乘客手机号
     */
    private String passengerPhone;

    /**
     * 服务类型
     */
    private Short serviceType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    public Long orderId;

    /**
     * 中台订单id
     */
    public String coreOrderId;

    /**
     * 预约业务类型
     * 1：预约管家
     * 2：接站管家
     */
    private int businessType;

    /**
     * 客户发起叫车选择的所有平台、车型、预估价金额信息。有展示需求
     */
    private String estimateCarTypes;

    /**
     * 预估距离（公里）
     */
    private BigDecimal estimateDistance;

    /**
     * 下单人手机号
     */
    private String placeOrderPhone;

    private String  trainNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date trainEstArrTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date trainActArrTime;

    private String trainDep;

    private String  trainArr;
    private int trainState;
    // 航班号
    private String  flightNumber;
}
