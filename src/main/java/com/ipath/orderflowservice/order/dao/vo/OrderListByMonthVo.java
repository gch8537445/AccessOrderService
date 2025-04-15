package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderListByMonthVo {
    private String orderId;          // 订单 id
    private BigDecimal totalAmount;  // 消费金额
    private Short serviceType;       // 服务类型：1: 实时，2：预约，6：接机
    private Short approvalState;     // 审核状态：0: 未处理，1：已通过，2：已拒绝
    private Short invoiceState;      // 发票状态
    private Short orderState;        // 订单状态：1：等待司机接单，2：等待司机，3：司机到达，4：行程中，5：服务结束，待支付，6：订单完成，7：已取消
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;         // 订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date departTime;         // 用车时间
    private String departCityName;       // 用车城市
    private String pickupLocationName;   // 起点名称
    private String destLocationName;     // 终点名称
    private BigDecimal frozenAmount;     // 冻结金额

    // 以下3个字段用于计算invoiceState
    private BigDecimal userAllowInvoiceAmount; // 待开票金额
    private BigDecimal userPaidAmount;         // 用户已支付金额
    private BigDecimal userBearAmount;         // 用户分担金额
}
