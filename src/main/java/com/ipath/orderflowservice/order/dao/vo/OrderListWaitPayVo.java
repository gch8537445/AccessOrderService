package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderListWaitPayVo {
    private Long id;                    // 订单 id
    private BigDecimal totalMoney;     // 订单总额
    private BigDecimal individualMoney;   // 应付金额
    private BigDecimal enterpriseMoney;  // 公司报销额度
    private BigDecimal prepaymentUpMoney;   // 预付费前置金额
    private BigDecimal prepaymentDownMoney;   // 预付费后置金额
    private Short approveState;         // 审核状态：0: 未处理，1：已通过，2：已拒绝
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;            // 订单创建时间
    private String cityName;            // 出发城市名称
    private String pickupLocationName;  // 起点名称
    private String destLocationName;    // 终点名称
    private Short payType;              //支付方式
    private String customInfo;
}
