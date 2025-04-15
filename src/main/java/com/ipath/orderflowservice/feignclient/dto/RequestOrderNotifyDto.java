package com.ipath.orderflowservice.feignclient.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ipath.orderflowservice.order.bean.vo.CacheEstimateResult;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import java.util.List;

@Data
public class RequestOrderNotifyDto {
    /**
     * 事件类型
     * 1,下单成功  2,司机已接单  3,司机到达 5,行程结束  6,订单完成  7,订单取消  8,修改目的地  9,改派
     */
    private Short eventType;
    /**
     * 前一状态
     */
    private Short previousOrderState;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 中台订单id
     */
    private String coreOrderId;
    /**
     * 账户id
     */
    private Long accountId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * order sourceId
     */
    private Long orderSourceId;
    /**
     * 场景id
     */
    private Long sceneId;
    /**
     * 场景发布id
     */
    private Long scenePublishId;

    /**
     * 运力平台code
     */
    private Integer carSource;
    /**
     * 运力平台名称
     */
    private String sourceName;
    /**
     * 车型
     */
    private Integer carLevel;
    /**
     * 起点名称
     */
    private String pickupLocationName;
    /**
     * 起点地址
     */
    private String pickupLocation;
    /**
     * 起点lat
     */
    private String pickupLat;
    /**
     * 起点lng
     */
    private String pickupLng;
    /**
     * 终点名称
     */
    private String destLocationName;
    /**
     * 终点地址
     */
    private String destLocation;
    /**
     * 终点lat
     */
    private String destLat;
    /**
     * 终点lng
     */
    private String destLng;
    /**
     * 出发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;
    /**
     * 用户手机号
     */
    private String userPhone;
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
     * 订单所在城市code
     */
    private String cityCode;
    /**
     * 订单所在城市名称
     */
    private String cityName;
    /**
     * 服务类型
     * 1：实时 2：预约 5：接站 6：接机
     */
    private Short serviceType;
    /**
     * 使用的优惠券id列表
     */
    private List<Long> couponIds;
    /**
     * 升舱描述
     */
    private UpgradeModel upgradeCar;
    /**
     * 预付标识
     * true：预付 false：非预付
     */
    private Boolean isPrePay;
    /**
     * 预付金额
     */
    private BigDecimal prePayPayableAmount;
    /**
     * 缓存预估结果
     */
    private List<CacheEstimateResult> cacheEstimateResults;
    /**
     * 预估id
     */
    private String estimateId;
    /**
     * 预估金额
     */
    private BigDecimal estimatePrice;
    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;
    /**
     * 航班号
     */
    private String flightNumber;
    /**
     * 航班时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date flightDepartTime;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机电话
     */
    private String driverPhone;
    /**
     * 司机虚拟电话（乘车人用）
     */
    private String driverPhoneVirtual;
    /**
     * 司机虚拟电话（叫车人用）
     */
    private String phoneForUser;
    /**
     * 车型
     */
    private String vehicleModel;
    /**
     * 车号
     */
    private String vehicleNo;
    /**
     * 车辆颜色
     */
    private String vehicleColor;
    /**
     * 司机level
     */
    private String driverLevel;
    /**
     * 行程开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelBeginTime;
    /**
     * 行程结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelEndTime;
    /**
     * 增值服务id列表
     */
    private List<Long> extraServiceIds;
    /**
     * 审批状态
     */
    private Short approveState;
    /**
     * 行驶里程（公里）
     */
    private BigDecimal distance;
    /**
     * 费用明细
     */
    private String transFeeDetail;
    /**
     * 运力总费用
     */
    private BigDecimal transTotalFee;
    /**
     * 订单实付金额
     */
    private BigDecimal price;
    /**
     * 企业承担金额
     */
    private BigDecimal companyBearAmount;
    /**
     * 用户承担金额
     */
    private BigDecimal userBearAmount;
    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;
    /**
     * 增值服务金额
     */
    private BigDecimal extralAmount;
    /**
     * 允许开票金额
     */
    private BigDecimal allowInvoiceAmount;
    /**
     * 正在支付金额
     */
    private BigDecimal personalPayingAmount;
    /**
     * 个人待支付金额
     */
    private BigDecimal personalPayableAmount;
    /**
     * 个人已付金额
     */
    private BigDecimal personalPaidAmount;
    /**
     * 个人支付状态
     */
    private Short personalPayStatus;

    /**
     * 配置费用
     */
    private BigDecimal cfgFeeAmount;

    /**
     * 自定义信息：手机号
     */
    private String customPhone;

    /**
     * 升舱是否成功：手机
     */
    private Boolean isUpgrade;

    /**
     * 自定义信息
     */
    private String customInfo;

    /**
     * 服务费信息
     */
    private String serviceFeeInfo;

    private Boolean isSettle;

    /**
     * 是否是异常订单
     */
    private Boolean isAbnormal;
    /**
     * 行前审批单主键
     */
    private Long preDepartApplyId;

    /**
     * 取消方
     */
    private String canceller;

    /**
     * 是否是运力托管
     */
    private Boolean isProxy;

    /**
     * 是否是接力单
     */
    private Boolean isRelay;

    private BigDecimal estimateDistance;
    private BigDecimal estimateTime;

    private String passengerPhoneVirtual;
    private String userPhoneVirtual;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date baseCreateTime; //订单创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date driverTakingTime; //司机接单时间
}
