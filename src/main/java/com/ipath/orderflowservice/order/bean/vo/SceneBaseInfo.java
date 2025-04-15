package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

@Data
public class SceneBaseInfo {
    private BigDecimal maxAmountPerOrder;  // 每单限额
    private BigDecimal availableAmount;    // 余额
    private Boolean allowChangeEndpoint;   // 是否允许更改目的地
    private Boolean isNeedApprove;         // 是否需要审批
    private Long wfReProcdefId;            // 审批流程id
    private List<Integer> defaultLevel;     //默认车型
    private List<Integer> requiredLevel;    //不可取消车型
    private JSONArray recommendCarType; // 建议车型
    private String recommendCarTypeTip;     // 建议车型的提示
    private JSONObject suggestLevel;
    private Short approvalType; //1-行前 2行后 3行前+行后
    private String customInfo;
    private String sceneCode;
}
