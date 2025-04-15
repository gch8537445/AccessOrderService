package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 税费信息
 */
@Data
public class TaxFeeModel {
    /**
     * fee name
     */
    private String feeName;
    /**
     * fee mode
     */
    private Integer feeMode;
    /**
     * fee value
     */
    private BigDecimal value;
    /**
     * fee names
     */
    private List<String> feeNames;
    /**
     * merge fee detail
     */
    private Boolean mergeFeeDetail;
    /**
     * display fee name
     */
    private String displayFeeName;
    /**
     * merge estimate
     */
    private Boolean mergeEstimate;
}
