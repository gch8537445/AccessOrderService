package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ServiceFeeModel {
    /**
     * 增值服务id列表
     */
    private List<Long> extrFee;
    private List<TaxFeeModel> taxFee;
}
