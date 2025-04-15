package com.ipath.orderflowservice.order.service;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.order.bean.vo.ComplaintDetailVo;
import com.ipath.orderflowservice.order.bean.vo.ValueTimeVo;
import com.ipath.orderflowservice.order.dao.bean.Project;

import java.util.List;

public interface SystemService {

    BaseResponse getUpgrade(UpgradeDto upgradeDto) throws Exception;
    BaseResponse getUserUpgrade(UpgradeDto upgradeDto) throws Exception;

    BaseResponse updatePreDepartApplyState(RequestUsageStateDto requestUsageStateDto) throws Exception;

    BaseResponse updateUsageCount(UpdateUsageCountDto updateUsageCountDto) throws Exception;

    BaseResponse addUsageRecord(UsageRecordDto usageRecordDto) throws Exception;

    BaseResponse queryPreApprovalCount(RequestUserDto requestUserDto) throws Exception;

    BaseResponse queryProject(RequestProjectDto projectDto) throws Exception;

    BaseResponse queryMainDeptManager(RequestDepartmentDto deptDto) throws Exception;

    BaseResponse queryDepartmentBaseByUserId(RequestDepartmentDto deptDto) throws Exception;

    BaseResponse restoreSceneQuota(RestoreSceneQuotaDto restoreSceneQuotaDto) throws Exception;

    BaseResponse getCompanyAbnormalRules(JSONObject jsonObject) throws Exception;

    BaseResponse notifyPreApprovalResult(JSONObject jsonObject) throws Exception;

    BaseResponse getCompanyCommonCfg(JSONObject jsonObject);

    ComplaintDetailVo getOrderComplaintDetail(Long orderId);

    JSONArray getProjects(Project project,Long userId);

    List<ValueTimeVo> getHistoryCustomList(String userId, String customType) throws Exception;


}
