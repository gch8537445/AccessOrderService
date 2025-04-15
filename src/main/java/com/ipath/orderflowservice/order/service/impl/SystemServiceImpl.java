package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.SystemFeign;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.vo.ComplaintDetailVo;
import com.ipath.orderflowservice.order.bean.vo.ValueTimeVo;
import com.ipath.orderflowservice.order.dao.CompanyUserConfigMapper;
import com.ipath.orderflowservice.order.dao.ProjectMapper;
import com.ipath.orderflowservice.order.dao.bean.Project;
import com.ipath.orderflowservice.order.service.SendMsgService;
import com.ipath.orderflowservice.order.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.execute.Execute.EXEC_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Value("${spring.profiles.active}")
    private String active;
    @Value("${sendMsg.mail.to}")
    private String to;
    @Value("${spring.profiles.active} + 环境system-service服务异常")
    private String subject;

    @Resource
    private CompanyUserConfigMapper companyUserConfigMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SnowFlakeUtil snowFlakeUtil;

    @Resource
    private SystemFeign systemFeign;

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    @Autowired
    private SendMsgService sendMsgService;

    @Autowired
    private LogService logService;

    /**
     * 查询升舱配置
     *
     * @param upgradeDto
     * @return
     */
    @Override
    public BaseResponse getUpgrade(UpgradeDto upgradeDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(upgradeDto.getCompanyId(), upgradeDto.getUserId(),
                null,
                InterfaceEnum.ORDER_SYSTEM_GET_UPGRADE);
        activityLog.setBody(JSONUtil.toJsonStr(upgradeDto));

        try {
            baseResponse = systemFeign.getUpgrade(upgradeDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (baseResponse.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
            } else {
                logService.saveErrorLogAsync(activityLog, new Exception(baseResponse.getMessage()));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
        }
        return baseResponse;
    }

    /**
     * 查询用户是否可以升舱
     *
     * @param upgradeDto
     * @return
     */
    @Override
    public BaseResponse getUserUpgrade(UpgradeDto upgradeDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.getUserIsPossibleUpgrade(upgradeDto);

        } catch (Exception e) {
            sendMsgService.sendMail(subject,
                    "时间：" + DateUtil.now() + " ,接口：查询用户是否可以升舱：/api/v2/system/FreeUpgrade/IsPossibleUpgrade 异常："
                            + e.toString(),
                    to, null);
            baseResponse.setCode(-1);
            baseResponse.setMessage(e.getMessage());
            log.error("getUpgrade ===> 异常：{}", e);
        }
        return baseResponse;
    }

    /**
     * 使用状态记录
     *
     * @param requestUsageStateDto
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse updatePreDepartApplyState(RequestUsageStateDto requestUsageStateDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(requestUsageStateDto.getCompanyId(), null,
                requestUsageStateDto.getOrderId(),
                InterfaceEnum.ORDER_SYSTEM_UPDATE_PRE_DEPART_APPLY_STATE);

        try {
            baseResponse = systemFeign.updatePreDepartApplyState(requestUsageStateDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (baseResponse.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
            } else {
                logService.saveErrorLogAsync(activityLog, new Exception(baseResponse.getMessage()));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            baseResponse.setCode(-1);
            baseResponse.setMessage(e.getMessage());
            log.error("updatePreDepartApplyState===>orderId:{} 异常:{}", requestUsageStateDto.getOrderId(), e);
        }
        return baseResponse;
    }

    /**
     * @param updateUsageCountDto
     * @return
     */
    @Override
    public BaseResponse updateUsageCount(UpdateUsageCountDto updateUsageCountDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(updateUsageCountDto.getCompanyId(), updateUsageCountDto.getUserId(),
                updateUsageCountDto.getOrderId(),
                InterfaceEnum.ORDER_SYSTEM_UPDATE_USAGE_COUNT);
        try {
            baseResponse = systemFeign.updateUsageCount(updateUsageCountDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (baseResponse.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
            } else {
                logService.saveErrorLogAsync(activityLog, new Exception(baseResponse.getMessage()));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            baseResponse.setCode(-1);
            baseResponse.setMessage(e.getMessage());
            log.error("updateUsageCount===>orderId:{} 异常:{}", updateUsageCountDto.getOrderId(), e);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse addUsageRecord(UsageRecordDto usageRecordDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(usageRecordDto.getCompanyId(), usageRecordDto.getUserId(),
                usageRecordDto.getOrderId(),
                InterfaceEnum.ORDER_SYSTEM_ADD_USAGE_RECORD);
        try {
            baseResponse = systemFeign.addUsageRecord(usageRecordDto);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (baseResponse.getCode() == 0) {
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
            } else {
                logService.saveErrorLogAsync(activityLog, new Exception(baseResponse.getMessage()));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            log.error("addUsageRecord===>orderId:{} 异常:{}", usageRecordDto.getOrderId(), e);
        }
        return baseResponse;
    }

    /**
     * 获取当前用户行前待审批的数量
     *
     * @param requestUserDto
     * @return
     */
    @Override
    public BaseResponse queryPreApprovalCount(RequestUserDto requestUserDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.queryPreApprovalCount(requestUserDto);
        } catch (Exception e) {
            sendMsgService.sendMail(subject, "时间：" + DateUtil.now()
                    + " ,接口：获取当前用户行前待审批的数量接口：/api/v2/system/preApproval/queryApprovalPendingCount 异常：" + e.toString(),
                    to, null);
            throw e;
        }
        return baseResponse;
    }

    /**
     * 获取项目
     *
     * @param projectDto
     * @return
     */
    @Override
    public BaseResponse queryProject(RequestProjectDto projectDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.queryProject(projectDto);
        } catch (Exception e) {
            sendMsgService.sendMail(subject,
                    "时间：" + DateUtil.now() + " ,接口：获取项目：/api/v2/system/Project/GetProjectDetails 异常：" + e.toString(),
                    to, null);
            throw e;
        }
        return baseResponse;
    }

    @Override
    public BaseResponse queryMainDeptManager(RequestDepartmentDto deptDto) throws Exception {
        return null;
    }

    /**
     * 获取部门名称
     *
     * @param deptDto
     * @return
     */
    @Override
    public BaseResponse queryDepartmentBaseByUserId(RequestDepartmentDto deptDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.queryDepartmentBaseByUserId(deptDto);
        } catch (Exception e) {
            sendMsgService.sendMail(
                    subject, "时间：" + DateUtil.now()
                            + " ,接口：获取部门名称：/api/v2/system/Department/GetDepartmentBaseByUserId 异常：" + e.toString(),
                    to, null);
            throw e;
        }
        return baseResponse;
    }

    /**
     * 审批恢复场景额度
     *
     * @param restoreSceneQuotaDto
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse restoreSceneQuota(RestoreSceneQuotaDto restoreSceneQuotaDto) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.restoreSceneQuota(restoreSceneQuotaDto);
        } catch (Exception e) {
            sendMsgService.sendMail(subject,
                    "时间：" + DateUtil.now() + " ,接口：审批恢复场景额度：/api/v2/system/Scene/RestoreSceneQuota 异常：" + e.toString(),
                    to, null);
            throw e;
        }
        return baseResponse;
    }

    /**
     * 获取合规预警配置
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getCompanyAbnormalRules(JSONObject jsonObject) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.getCompanyAbnormalRules(jsonObject);
        } catch (Exception e) {
            sendMsgService.sendMail(
                    subject, "时间：" + DateUtil.now()
                            + " ,接口：获取合规预警配置：/api/v2/system/AbnormalRule/GetCompanyAbnormalRules 异常：" + e.toString(),
                    to, null);
            throw e;
        }
        return baseResponse;
    }

    /**
     * 通知行前审批结果
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse notifyPreApprovalResult(JSONObject jsonObject) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.notifyPreApprovalResult(jsonObject);
        } catch (Exception e) {
            sendMsgService.sendMail(subject, "时间：" + DateUtil.now()
                    + " ,接口：通知行前审批结果：/api/v2/system/preApproval/receiveApprove 异常：" + e.toString(), to, null);
            throw e;
        }
        return baseResponse;
    }

    /**
     * 获取企业配置
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getCompanyCommonCfg(JSONObject jsonObject) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = systemFeign.getCompanyCommonCfg(jsonObject);
        } catch (Exception e) {
            sendMsgService.sendMail(subject, "时间：" + DateUtil.now()
                    + " ,接口：获取企业配置：/api/v2/system/Company/GetCompanyCommonCfg 异常：" + e.toString(), to, null);
            baseResponse.setCode(-1);
            baseResponse.setMessage(e.getMessage());
            log.error("getCompanyCommonCfg ===> 异常：{}", e);
        }
        return baseResponse;
    }

    /**
     * 获取企业配置
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    public ComplaintDetailVo getOrderComplaintDetail(Long orderId) {
        ComplaintDetailVo complaintDetailVo = null;
        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            JSONObject paramObject = new JSONObject();
            paramObject.set("orderId", orderId);
            remoteCallDto.setContent(paramObject.toString());
            remoteCallDto.setPath("/api/v2/reportcore/ReportCore/GetOrderComplaintDetail");
            BaseResponse response = remoteCallFeign.call(remoteCallDto);
            if (response.getCode() == 0 && null != response.getData()) {
                Object data = response.getData();
                JSONObject dataJsonObject = new JSONObject(data);
                if (dataJsonObject.containsKey("complaintForCSC")) {
                    JSONObject complaintJsonObject = dataJsonObject.getJSONObject("complaintForCSC");
                    // if(complaintJsonObject.getInt("level", 0)==1) return null;
                    complaintDetailVo = new ComplaintDetailVo();
                    complaintDetailVo.setState(complaintJsonObject.getInt("state", 0));
                    complaintDetailVo.setFeedback(complaintJsonObject.getStr("feedback"));
                    complaintDetailVo.setFeedbackTime(complaintJsonObject.getDate("feedbackTime"));
                    complaintDetailVo.setStartProcessTime(complaintJsonObject.getDate("startProcessTime"));
                    complaintDetailVo.setReplyTime(complaintJsonObject.getDate("replayTime"));
                    complaintDetailVo.setReply(complaintJsonObject.getStr("replay"));

                    String complaintLabels = complaintJsonObject.getStr("complaintLabels");
                    if (StrUtil.isNotBlank(complaintLabels)) {
                        JSONObject complaintLabelJsonObject = new JSONObject(complaintLabels);
                        JSONArray jsonArray = complaintLabelJsonObject.getJSONArray("complaintLabels");
                        if (null != jsonArray) {
                            List<String> labelList = new ArrayList();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                                String label = eachJsonObject.getStr("label");
                                labelList.add(label);
                            }

                            complaintDetailVo.setComplaintLabels(labelList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("getOrderComplaintDetail ===> 异常：{}", e);
        }
        return complaintDetailVo;
    }

    @Override
    public JSONArray getProjects(Project project, Long userId) {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("id", snowFlakeUtil.getNextId());
        jsonObject.set("code", "无项目");
        jsonObject.set("name", "无项目");
        jsonArray.add(jsonObject);

        if (StrUtil.isEmpty(project.getCode())) {
            List<ValueTimeVo> historyProjectList = null;
            try {
                historyProjectList = getHistoryCustomList(userId.toString(), "project");
            } catch (Exception e) {
                log.info("获取历史选择项目出现错误:{}", userId);
            }
            if (CollectionUtil.isNotEmpty(historyProjectList)) {
                historyProjectList = historyProjectList.stream()
                        .sorted(Comparator.comparing(ValueTimeVo::getTime).reversed()).collect(Collectors.toList());

                historyProjectList.forEach(t -> {
                    Long id = snowFlakeUtil.getNextId();
                    JSONObject eachJsonObject = new JSONObject();
                    eachJsonObject.set("id", id);
                    eachJsonObject.set("code", t.getValue());
                    eachJsonObject.set("name", t.getValue());
                    jsonArray.add(eachJsonObject);
                });
            }
        } else {
            List<Project> projects = projectMapper.selectProjects(project);
            if (!projects.isEmpty()) {
                for (Project pj : projects) {
                    JSONObject eachJsonObject = new JSONObject();
                    eachJsonObject.set("id", pj.getId());
                    eachJsonObject.set("code", pj.getCode());
                    eachJsonObject.set("name", pj.getNameCn());

                    jsonArray.add(eachJsonObject);
                }
            }
        }

        return jsonArray;
    }

    @Override
    public List<ValueTimeVo> getHistoryCustomList(String userId, String customType) throws Exception {
        String key = CacheConsts.REDIS_KEY_CUSTOM_INFO_PREFIX + ":userid:" + userId + ":type:" + customType;
        long count = redisUtil.listGetSize(key);
        List<Object> objectList = redisUtil.listGetRange(key, 0, count);

        List<ValueTimeVo> valueLabelList = new ArrayList<>();
        if (null != objectList && objectList.size() > 0) {
            for (Object object : objectList) {
                ValueTimeVo valueTimeVo = JSONUtil.toBean(object.toString(), ValueTimeVo.class);
                valueLabelList.add(valueTimeVo);
            }
        }
        return valueLabelList;
    }

}