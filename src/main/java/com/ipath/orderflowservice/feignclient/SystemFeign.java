package com.ipath.orderflowservice.feignclient;

import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "system-service")
public interface SystemFeign {
    /**
     * 获取行前审批设置
     */
    @RequestMapping(value = "/api/v2/system/Company/GetPrePaySetting", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getPrePaySetting(@RequestBody RequestCompanyDto requestCompanyDto);

    @RequestMapping(value = "/api/v2/system/FreeUpgrade/GetUserUpgrade", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getUpgrade(@RequestBody UpgradeDto upgradeDto);

    /**
     * 查询用户是否拥有升舱资质
     * 企业开启升舱, 企业当月升舱次数为用完, 用户拥有升舱资格, 用户升舱次数未用完
     * @param upgradeDto   userId   companyId
     * @return
     */
    @RequestMapping(value = "/api/v2/system/FreeUpgrade/IsPossibleUpgrade", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getUserIsPossibleUpgrade(@RequestBody UpgradeDto upgradeDto);

    /**
     * 使用状态记录
     */
    @RequestMapping(value = "/api/v2/system/preApproval/UpdatePreDepartApplyState", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse updatePreDepartApplyState(@RequestBody RequestUsageStateDto requestUsageStateDto);

    @RequestMapping(value = "/api/v2/system/FreeUpgrade/UpdateUsageCount", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse updateUsageCount(@RequestBody UpdateUsageCountDto updateUsageCountDto);

    @RequestMapping(value = "/api/v2/system/FreeUpgrade/AddUsageRecord", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse addUsageRecord(@RequestBody UsageRecordDto usageRecordDto);

    /**
     * 获取当前用户行前待审批的数量
     */
    @RequestMapping(value = "/api/v2/system/preApproval/queryApprovalPendingCount", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse queryPreApprovalCount(@RequestBody RequestUserDto requestUserDto);

    /**
     * 获取项目
     */
    @RequestMapping(value = "/api/v2/system/Project/GetProjectDetails", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse queryProject(@RequestBody RequestProjectDto projectDto);

    /**
     * 获取主部门负责人
     */
    @RequestMapping(value = "/api/v2/system/Department/GetDepartmentByUserId", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse queryMainDeptManager(@RequestBody RequestDepartmentDto deptDto);

    /**
     * 获取部门名称
     */
    @RequestMapping(value = "/api/v2/system/Department/GetDepartmentBaseByUserId", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse queryDepartmentBaseByUserId(@RequestBody RequestDepartmentDto deptDto);

    /**
     * 审批恢复场景额度
     */
    @RequestMapping(value = "/api/v2/system/Scene/RestoreSceneQuota", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse restoreSceneQuota(@RequestBody RestoreSceneQuotaDto restoreSceneQuotaDto);

    /**
     * 获取合规预警配置
     */
    @RequestMapping(value = "/api/v2/system/AbnormalRule/GetCompanyAbnormalRules", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getCompanyAbnormalRules(@RequestBody JSONObject jsonObject);

    /**
     * 通知行前审批结果
     */
    @RequestMapping(value = "/api/v2/system/preApproval/receiveApprove", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse notifyPreApprovalResult(@RequestBody JSONObject jsonObject);

    /**
     * 获取企业配置
     */
    @RequestMapping(value = "/api/v2/system/Company/GetCompanyCommonCfg", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getCompanyCommonCfg(@RequestBody JSONObject jsonObject);
}
