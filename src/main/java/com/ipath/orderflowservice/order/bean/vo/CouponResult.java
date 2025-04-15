package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * 获取个人优惠券结果
 */
@Data
public class CouponResult {
        private String id; // 主键
        private BigDecimal threshold;       // 满足此金额可用
        private BigDecimal parValue;        // 卡券抵扣面值
        private Short source;               // 来源 1：公司 2：个人
        private Date validFrom;             // 有效期开始
        private Date validTo;               // 有效期截止
        private boolean isValid;            // 是否可用
        private String carMode;            // 车型mode[contain,exclude]
        private Long validSeconds;         //有效期结束，距离当前时间的秒数
        private List<String> allowCityList; //允许使用的城市
        private List<String> carLevelList;  //允许使用的车型
        private List<String> carSourceList;  //允许使用的平台

}
