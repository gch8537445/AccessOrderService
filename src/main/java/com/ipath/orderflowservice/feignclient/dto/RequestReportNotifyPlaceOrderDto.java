package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class RequestReportNotifyPlaceOrderDto extends RequestReportNotifyBaseDto{
    /**
     * 订单类型
     */
    private Short orderType;

    /**
     * 公司主键
     */
    private Long companyId;
    /**
     * 中台订单id
     */
    private Long coreOrderId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 姓名
     */
    private String nameCn;
    /**
     * 用车时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date orderTime;
    /**
     * 实际下单城市id
     */
    private String cityCode;
    /**
     * 实际下单城市名称
     */
    private String cityName;
    /**
     * 映射下单城市
     */
    private String mappingCityCode;
    /**
     * 映射下单城市名称
     */
    private String mappingCityName;
    /**
     * 目的地城市id
     */
    private String destCityCode;
    /**
     * 目的地城市名称
     */
    private String destCityName;

    /**
     * 乘车人姓名
     */
    private String passenger;

    /**
     * 乘车人电话
     */
    private String passengerPhone;

    /**
     * 起点地址
     */
    private String pickupLocation;

    /**
     * 起点名称
     */
    private String pickupLocationName;

    /**
     * 起点lat
     */
    private String pickupLat;
    /**
     * 起点lng
     */
    private String pickupLng;
    /**
     * 终点地址
     */
    private String destLocation;
    /**
     * 终点名称
     */
    private String destLocationName;
    /**
     * 终点lat
     */
    private String destLat;
    /**
     * 终点lng
     */
    private String destLng;
    /**
     * 下单人电话
     */
    private String userPhone;
    /**
     * 下单人紧急联系电话
     */
    private String userEmergencyPhone;
    /**
     * 预估价格
     */
    private BigDecimal estimatePrice;
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 主部门负责人
     */
    private String departmentManager;
    /**
     * 场景id
     */
    private Long sceneId;
    /**
     * 场景Code
     */
    private String sceneCode;
    /**
     * 场景名称
     */
    private String sceneName;
    /**
     * 场景发布id
     */
    private Long scenePublishId;

    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 企业账户id
     */
    private Long accountId;
    /**
     * 创建人主键
     */
    private Long createor;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date createdTime;
    /**
     * 用车原因
     */
    private String useCarReason;
    /**
     * 是否是大额预付订单
     */
    private Boolean isPrepay;
    /**
     * 预付金额
     */
    private BigDecimal prepayAmount;
    /**
     * 大额预付支付渠道
     */
    private String payType;
    /**
     * 自定义信息
     */
    private String customInfo;
    /**
     * 合作方订单id
     */
    private String partnerOrderId;
    private Integer estimateDistance;
    private Integer estimateTime;
}
