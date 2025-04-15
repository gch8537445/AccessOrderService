package com.ipath.orderflowservice.order.bean;

import lombok.Data;

/**
 * redis 存储的下单限制 用车管控 次数限制 bean
 *
 */
@Data
public class RedisCpolRegulationInfoNumber {
    /**
     * 申请单周期内打车次数
     */
    private Long limitedPeriodNumber;
    /**
     * 月度打车次数
     */
    private Long limitedMonthNumber;
}
