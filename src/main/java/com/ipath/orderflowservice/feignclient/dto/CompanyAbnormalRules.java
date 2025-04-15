package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class CompanyAbnormalRules {
    private Long id;
    private String nameCn;
    private String nameEn;
    /**
     * 通知用户
     * 0-不通知
     * 1-通知
     * 2-通知并需要用户确认
     */
    private Short notifyUser;
    /**
     * 提醒至审批人
     */
    private JSONObject remindApproverInfo;
    private List<CompanyAbnormalItems> abnoramlRuleItems;
    private List<Long> sceneIds;
}
