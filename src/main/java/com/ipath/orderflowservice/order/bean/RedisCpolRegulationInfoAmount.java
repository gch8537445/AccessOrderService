package com.ipath.orderflowservice.order.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * redis 存储的下单限制 用车管控 金额管控 bean
 *
 */
@Data
public class RedisCpolRegulationInfoAmount {
    /**
     * 单次限额
     */
    private BigDecimal limitedAmount;
    /**
     * 日限额
     */
    private BigDecimal limitedDayAmount;
    /**
     * 周限额
     */
    private BigDecimal limitedWeekAmount;
    /**
     * 单月限额
     */
    private BigDecimal limitedMonthAmount;
    /**
     * 申请单总限额
     */
    private BigDecimal limitedTotalAmount;
    /**
     * 申请单内的城市限额
     */
    private List<RedisCpolRegulationInfoAmountCity> city;


}
