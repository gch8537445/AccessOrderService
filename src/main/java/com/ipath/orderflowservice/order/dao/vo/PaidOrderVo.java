package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class PaidOrderVo {
    private Long id;                    // 订单 id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;            // 订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date userPayTime;           // 订单支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date updateTime;
    private BigDecimal userBearAmount;  // 个人支付金额
    private String pickupLocationName;  // 起点名称
    private String destLocationName;    // 终点名称
    private Short payType;
    private String customInfo;
}
