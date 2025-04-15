package com.ipath.orderflowservice.order.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.dao.*;
import com.ipath.orderflowservice.order.dao.bean.ComScenePara;
import com.ipath.orderflowservice.order.dao.bean.CompanySetting;
import com.ipath.orderflowservice.order.service.*;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.ipath.orderflowservice.config.OrderControllerExceptionHandler.OrderExceptionCache;


@Slf4j
@Component
public class CronTask {
    @Value("${spring.profiles.active}")
    private String active;
    @Value("${sendMsg.mail.to}")
    private String to;
    @Value("${sendMsg.mail.enabled}")
    private boolean mailEnabled;

    @Autowired
    private SendMsgService msgService;

    @Autowired
    private UserBaseMapper userBaseMapper;
    @Autowired
    private ComSceneMapper sceneMapper;
    @Autowired
    private ComSceneUsedMapper sceneUsedMapper;
    @Autowired
    private ComSceneParaMapper sceneParaMapper;
    @Autowired
    private CompanySettingMapper companySettingMapper;
    
    @Value("${mobileUrl}")
    private String MOBILE_URL;

    /**
     * 获取异常日志 发送邮件
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendMsgException() {
        // log.info("schedule.sendMsgException. start ==> {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        try {
            Set<String> set = OrderExceptionCache.entrySet();
            if (set.size() > 0) {
                String serverName = "accessOrderService";
                String subject = active + "环境,出现异常请查看";
                String msg = serverName + "======>" + JSONUtil.toJsonStr(set);
                msgService.sendMail(subject, msg, to, null);
                OrderExceptionCache.clear();
                log.info("schedule.sendMsgException. end ==> {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            } else {
                // log.info("schedule.sendMsgException. end ==> 无异常{}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }

        } catch (Exception e) {
            log.error("schedule.sendMsgException.errro ==> {}", e.getMessage());
        }
    }

    /**
     * 定时任务-额度恢复
     * 每月1号凌晨0点5分
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    @SchedulerLock(name = "recoveryAmount", lockAtMostFor = "15s", lockAtLeastFor = "15s")
    public void recoveryAmount() {
        log.info("schedule.recoverAmount. start ==> {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        //此处的设置均在ka端，直接从数据库查询符合条件的数据
        HashMap<Long, Boolean> recoveryMap = new HashMap<Long, Boolean>();

        List<Long> sceneAmountRecoveryList = new ArrayList<>();
        List<Long> userAmountRecoveryList = new ArrayList<>();
        List<Long> personalAmountRecoveryList = new ArrayList<>();
        List<String> paraCodes = new ArrayList<>();
        paraCodes.add("3006");
        paraCodes.add("3020");
        List<ComScenePara> sceneParaList = sceneParaMapper.selectByParaCodes(paraCodes);
        if (sceneParaList != null && sceneParaList.size() > 0) {
            for (ComScenePara csp : sceneParaList) {
                if (csp.getParaValue() != null) {
                    JSONObject userObject = new JSONObject(csp.getParaValue());
                    if (userObject.getBool("enabled") && "3".equals(userObject.getStr("recovery_mode"))) {
                        if ("3006".equals(csp.getParaCode())) {//用户额度限制
                            userAmountRecoveryList.add(csp.getSceneId());
                        } else {//场景额度限制
                            sceneAmountRecoveryList.add(csp.getSceneId());
                        }
                    }
                }
            }
        }

        List<CompanySetting> companySettingList = companySettingMapper.selectCompanySettingByName("company_personal_quota");
        if (companySettingList != null && companySettingList.size() > 0) {
            for (CompanySetting companySetting : companySettingList) {
                if (companySetting.getValue() != null) {
                    JSONObject obj = new JSONObject(companySetting.getValue());
                    if (obj.containsKey("RecoveryMode") && 3 == obj.getInt("RecoveryMode")) {
                        personalAmountRecoveryList.add(companySetting.getCompanyId());
                    }
                }
            }
        }

        if (sceneAmountRecoveryList.size() > 0) {
            sceneMapper.updateByPrimaryKeys(sceneAmountRecoveryList);
        }

        if (userAmountRecoveryList.size() > 0) {
            sceneUsedMapper.updateByPrimaryKeys(userAmountRecoveryList);
        }

        if (personalAmountRecoveryList.size() > 0) {
            userBaseMapper.updateByPrimaryKeys(personalAmountRecoveryList);
        }

        log.info("schedule.recoverAmount. end ==> {}", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}
