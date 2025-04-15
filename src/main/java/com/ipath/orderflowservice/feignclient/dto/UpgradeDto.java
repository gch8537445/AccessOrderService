package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.UpgradeParam;
import lombok.Data;

import java.util.List;

@Data
public class UpgradeDto {
    private Long userId;
    private Long companyId;
    private int upgradeCarLevel;//用户选择的最高carlevel（1:普通,1:舒适,1:商务,4:豪华）
    private List<UpgradeParam> cars;
}
