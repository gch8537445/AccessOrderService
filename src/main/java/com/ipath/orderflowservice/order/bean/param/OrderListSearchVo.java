package com.ipath.orderflowservice.order.bean.param;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class OrderListSearchVo {
    private Integer pageNum;
    private Integer pageSize;
    private Long userId;
    private Long orderId;                   // 订单号  内部订单号
    private String coreOrderId;           // 流水号  中台子订单的订单号
    private String socialAppUserId;         // userid  是企业微信、飞书的通讯录的userid
    private String userPhone;               // 个人设置电话   个人电话    
    private String userName;                // 用车人  下单人的姓名
    private String departCityName;          // 城市
    private String pickupLocationName;      // 起始地
    private String destLocationName;        // 目的地
    private Short travelState;              // 行程状态   1 等待司机接单, 2 等待司机, 3 司机到达, 4 行程中, 5 服务结束，待支付, 6 订单完成，已支付, 7 已取消
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;                // 订单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelBeginTime;           // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelEndTime;             // 结束时间
    private String carSourceName;           // 用车来源
    private Short payerType;                // 支付来源/支付途径   1 企业支付，2 个人支付，3 混合支付
    private Short userPayState;             // 个人支付状态
    private Short personalPayState;         // 拒绝个人支付    指审批拒绝后，个人支付的状态  1 未支付，2 已支付
    private Short carLevel;                 // 车型   1 普通型，2 舒适型，3 商务型，4 豪华型，9 优享型
    private String vehicleModel;            // 车辆品牌
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date driverArriveTime;          // 到达起始地时间
    private Short serviceType;              // 订车类型   实时 预约 接机
    private String vehicleNumber;           // 车牌号
    private Short applyState;               // 审批状态    1 提交审批，2 审批通过，3 审批驳回
    private Long approverSocialAppUserId;   // 审批人ID
    private String approverUserName;        // 审批人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date applyExamineTime;          // 批复时间
}
