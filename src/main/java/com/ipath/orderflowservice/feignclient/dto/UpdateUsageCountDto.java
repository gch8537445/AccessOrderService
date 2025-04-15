package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.UpgradeParam;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpdateUsageCountDto {
    private Long companyId;
    private Long accountId;
    private Long userId;
    private boolean useUpgrade;//使用传true,取消传false
    private Long orderId;
    private String recordDesc;
    private Date orderTime;
}
