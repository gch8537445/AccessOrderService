package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class RequestReportNotifyTravelEndDto extends RequestReportNotifyBaseDto{
    /**
     * 公司主键
     */
    private Long companyId;
    /**
     * 行程结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date travelEndTime;
    /**
     * 行驶时间
     */
    private BigDecimal travelDuration;
    /**
     * 行驶里程
     */
    private BigDecimal travelDistance;
    /**
     * 实际起始地名称
     */
    private String actualPickupLocationName;
    /**
     * 实际起始地地址
     */
    private String actualPickupLocation;
    /**
     * 实际起始地lat
     */
    private String actualPickupLat;
    /**
     * 实际起始地lng
     */
    private String actualPickupLng;
    /**
     * 实际目的地名称
     */
    private String actualDestLocationName;
    /**
     * 实际目的地地址
     */
    private String actualDestLocation;
    /**
     * 实际目的地lat
     */
    private String actualDestLat;
    /**
     * 实际目的地lng
     */
    private String actualDestLng;
    /**
     * 费用详情
     */
    private String feeDetail;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 服务费金额
     */
    private BigDecimal extralAmount;
    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;
    /**
     * 预付金额
     */
    private BigDecimal prepayAmount;
    /**
     * 预付退款金额
     */
    private BigDecimal prepayRefundAmount;
    /**
     * 企业承担金额
     */
    private BigDecimal companyAmount;
    /**
     * 个人承担金额
     */
    private BigDecimal personalAmount;
    /**
     * 实际金额
     */
    private BigDecimal actualAmount;
    /**
     * 允许开票金额
     */
    private BigDecimal allowInvoiceAmount;
    /**
     * 个人待支付金额
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
     * 配置费用金额
     */
    private BigDecimal cfgFeeAmount;

    /**
     * 自定义信息
     */
    private String customInfo;

    /**
     * 结算时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date settleTime;

    /**
     * 是否包含结算信息
     */
    private Boolean isSettle;

    /**
     * 是否是异常订单
     */
    private Boolean isAbnormal;
}
