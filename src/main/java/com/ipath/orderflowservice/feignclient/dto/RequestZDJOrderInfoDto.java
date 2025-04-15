package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class RequestZDJOrderInfoDto {
    private String appId;  //应用ID
    private String sign;  //签名
    private Long eid;  //企业ID
    private String timestamp;  //请求发生时的时间戳，单位ms
    private String amapOrderId;  //高德订单号
    private String userId;  //企业用户ID
    private String mobile;  //企业用户手机号
    private String departmentId;  //企业用户部门ID
    private String departmentName;  //企业用户部门名称
    private String requestTime;  //约车时间（如：2018-12-12 20:00:00）
    private String departTime;  //出行时间（如：2018-12-12 20:00:00）
    private String getOnTime;  //实际的上车时间（司机开始行程时间）
    private String getOffTime;  //实际的下车时间（司机结束行程时间）
    private String startName;  //起点名称
    private String startAdcode;  //起点城市编码(地级市)
    private String startLng;  //起点经度（如：39.984948）
    private String startLat;  //起点纬度（如：116.467588）
    private String startCity;  //起点城市
    private String endName; //终点名称
    private String endAdcode;  //终点城市编码(地级市)
    private String endLng;  //终点经度（如：40.040786）
    private String endLat;  //终点纬度（如：116.475628）
    private String endCity;  //终点城市
    private String driverPhone; //司机电话
    private String driverName;  //司机姓名
    private String licensePlate;  //车牌号
    private String rideType;  //运力类型( 1：经济，2：品质专车，3：商务，4：豪华，5：出租车，6：优享，11：优惠推荐，13：企业同行，14：企业拼车)
    private String rideTypeName;  //运力显示文案
    private String cpName;  //CP名称（例如滴滴、首汽约车等）
    private String estimatePrice;  //预估价（分)
    private String estimateMileage;  //预估里程（米）
    private String estimateTime;  //预估时间（秒）
    private String duration;  //总时长（分）
    private String mileage;  //总里程（米）
    private String totalFee;  //总费用（分）
    private String originalFeeDetail;  //费用明细（取消费、起步费、里程费、时长费、停车费、过桥费、附加费、加价红包、信息费、其他费用、预约服务费、高速服务费、企业服务费等等）
    private String feeDetail;  //费用明细（时长费,里程费,附加费,附加费用明细,其他费用,其他费用明细）
    private Short showStatus;  //0-派单中，1-完成待支付，2-取消待支付，3-关闭，4-订单完成(支付完成)，5-待出发，6-待上车，7-行程中
    private String enterpriseCpStatus;  //订单子状态
    private Integer orderServiceType;  //业务类型： 1-实时单，2-预约单，3-接送机,6-企业拼车
    private String statusUpdateTime;  //订单状态更新时间（如：2018-12-12 20:00:00）
    private String remark;  //企业获取token时传递的备注信息
    private String carType;  //司机车型
    private String carColor;  //车辆颜色
    private List<RiskTag> riskTags;  //所有风险标记数据
    private List<RefundItem> refundItems;  //所有退款数据
    private Integer totalEnterpriseRefundAmount;  //企业退款总金额(分)
    private Integer totalPersonRefundAmount;  //个人退款总金额(分)
    private Integer enterpriseAmount;  //企业支付金额(分)
    private Integer personAmount;  //个人支付金额(分)
    private Short subGdServiceId;  //服务子类型, 默认null, null或0为普通单, 1代叫单
    private String privacyNumber;  //叫车人 隐私号（叫车人与司机绑定的隐私号）
    private String passengerPrivacyNumber;  //代叫单 乘车人隐私号（乘车人与司机绑定的隐私号）
    private String passengerPhone;  //代叫单 乘车人手机号
    private String privateTag;  //1 因私订单  0非因私订单
    private String applyRecord;  //申请单详情
    private String showPayChannel;  //个人支付(3)、企业支付(1)、混合支付(2)
    private String sceneType; //因公出行场景（0-个人用车、1-加班、2-定点通勤、3-外勤用车、4-出差、5-接送机）
    private String extInfo; //员工扩展属性 JSON【长度:1000字符】
    private String attachInfo;  //员工扩展信息，由附加信息接口传入
    private String startBillLocationName;  //实际上车地点
    private String startBillTime;  //实际上车时间
    private String endBillLocationName; //实际下车地点
    private String endBillTime;  //实际下车时间
    private String realStartLat; //实际上车纬度
    private String realStartLng; //实际上车经度
    private String realEndLat; //实际下车纬度
    private String realEndLng; //实际下车经度
    private String driverAcceptTime; //司机接单时间
    private String payTime; //订单支付时间
    private String initOrderServiceType; //传入的orderServiceType
    private String regulationContent;  //下单时刻的制度内容,json字符串
    private String regulationId; //打车时使用的制度id
    private String regulationName; //打车时使用的制度名称
    private String sensitiveOrderReason; //敏感订单员工确认原因的原因选项和填写的备注
    private String flightNo; //航班号 (接送机返回)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private String flightDate; //接送机-航班出发日期 (接送机返回)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private String actualDepartTime; //接机场景，行程的真实出发时间，航班延误、提前到达情况下更新 (接送机返回)
    private String depAirCode; //出发机场三字码 (接送机返回)
    private String arrAirCode; //到达机场三字码 (接送机返回)
    private Integer pickUpDelayTime; //航班到达后，延迟N毫秒后用车 (接送机返回)
}
