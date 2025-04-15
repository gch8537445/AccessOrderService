package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @author: qy
 * @create: 2024-10-24 10:22
 **/
@Data
public class TemplateSimpleVo {


    private Long id;
    /**
     * 订单金额区间 元
     * 例如 10-23.6
     */
    private String amountRange;

    /**
     * 里程区间 公里
     * 例如 3-8.3
     */
    private String distanceRange;

    /**
     * 时长区间 分钟
     * 例如 5
     */
    private String durationRange;

    /**
     * 出参 调整金额 正负值
     */
    private BigDecimal adjustAmount;
}
