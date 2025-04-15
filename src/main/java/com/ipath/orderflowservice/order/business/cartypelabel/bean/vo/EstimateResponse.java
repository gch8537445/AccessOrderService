package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 预估规则
 * @author: qy
 * @create: 2024-10-15 15:01
 **/
@Data
public class EstimateResponse {

    private Long templateId;

    /**
     * 	标签code
     */
    private String labelCode;

    /**
     * 是否默认选中
     */
    private String selected;

    /**
     * 预估价调整金额 Adjustment
     */
    private BigDecimal adjustmentAmount;

    private List<CarTypeLabelVo> carTypeLabelList;


}
