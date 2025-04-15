package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;
import cn.hutool.json.JSONObject;

// 创建订单时的附加参数，暂时只有一个属性，以后可能会有多个属性
@Data
public class OrderExtraParameter {
    private String userCarReason;

    private String costCenterCode;

    private String caseCode;

    private String approver;

    /*
    自定义信息
     */
    private JSONObject customInfo;
}
