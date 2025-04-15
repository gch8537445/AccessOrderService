package com.ipath.orderflowservice.feignclient.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ipath.orderflowservice.order.bean.param.KeyValue;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.Data;

@Data
public class RequestCheckOrderDto {
    /**
     * 验证类型
     * 1: 预估前验证;2:下单前验证;3:选择平台验证
     */
    private Short checkType;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 企业账户id
     */
    private Long accountId;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 场景发布id
     */
    private Long scenePublishId;

    /**
     * 起点lat
     */
    private String departLat;

    /**
     * 起点lng
     */
    private String departLng;

    /**
     * 终点lat
     */
    private String destLat;

    /**
     * 终点lng
     */
    private String destLng;

    /**
     * 服务类型
     */
    private Short serviceType;

    /**
     * 起点城市code
     */
    private String departCityCode;

    /**
     * 起点城市name
     */
    private String departCityName;

    /**
     * 终点城市code
     */
    private String destCityCode;

    /**
     * 终点城市name
     */
    private String destCityName;

    /**
     * 出发地名称
     */
    private String pickupLocationName;

    /**
     * 出发地详细地址
     */
    private String pickupLocation;

    /**
     * 目的地名称
     */
    private String destLocationName;

    /**
     * 目的地详细地址
     */
    private String destLocation;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;                // 出发时间(yyyy-MM-dd HH:mm:ss)

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 航班起飞时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date flightDepartTime;

    /**
     * 航班到达机场code
     */
    private String flightArrivalAirportCode;

    /**
     * 路程距离（高德地图查询到的最大距离）
     */
    private Integer distance;

    /**
     * 选择的车型数组
     */
    private List<SelectedCar> cars;

    /**
     * 本次预估金额
     */
    private BigDecimal newEstimateAmount;

    /**
     * 原有预估金额
     */
    private BigDecimal oldEstimateAmount;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 行前审批单主键
     */
    private Long preDepartApplyId;

    /**
     * 行前审批单主键
     */
    private String preDepartApplyCode;

    /**
     * 有效开始时间
     */
    private String validDateFrom;

    /**
     * 有效结束时间
     */
    private String validDateTo;



    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 自定义限制条件集合
     * key:para code
     * value:limit amount
     */
    private List<KeyValue> customLimits;

    private Boolean confirm;

    /**
     * 是否验证地址，临时加。
     */
    private Boolean isValidateAddress;

    // 20240604 法本下单时所属项目和所属部门验证添加
    /**
     * 自定义用车信息
     */
    private String customInfo;

    /**
     * 超出管控额度是否允许下单： true-允许 false-不允许。
     */
    private Boolean allowExcess;
}
