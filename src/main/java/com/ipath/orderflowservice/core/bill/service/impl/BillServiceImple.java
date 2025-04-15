package com.ipath.orderflowservice.core.bill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.bill.service.BillService;
import com.ipath.orderflowservice.feignclient.BillFeign;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.feignclient.dto.RequestBookingNotifyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestCompanyAccountUserDto;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.service.CacheService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预约管家 Service
 */
@Service
@Slf4j
public class BillServiceImple implements BillService {

    @Autowired
    private LogService logService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    @Autowired
    private BillFeign billFeign;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取公司默认账户
     * 
     * @param companyId
     * @return
     */
    public Long getDefaultAccountId(Long companyId) {
        Long accountId = 0L;
        if (redisUtil.hashHasKey(CacheConsts.REDIS_KEY_Default_ACCOUNT_PREFIX, companyId.toString())) {
            accountId = (Long) redisUtil.hashGet(CacheConsts.REDIS_KEY_Default_ACCOUNT_PREFIX, companyId.toString());
        }
        if (accountId == 0L) {
            long startTime = System.currentTimeMillis();
            Log activityLog = logService.getLog(companyId, null, null, InterfaceEnum.ORDER_BILL_GET_DEFAULT_ACCOUNT,
                    null);
            try {
                BaseResponse baseResponse = billFeign.getCompanyDefaultAccount(companyId);

                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);

                if (baseResponse.getCode() != 0) {
                    log.error("获取默认账户出现异常【BillService->insertAccountUser.bill.GetCompanyDefaultAccount,companyId:{}】",
                            companyId, new Exception(baseResponse.getMessage()));
                    logService.saveErrorLogAsync(activityLog, baseResponse.getMessage());
                } else {
                    JSONObject prepayObject = JSONUtil.parseObj(baseResponse.getData());
                    if (prepayObject.containsKey("id")) {
                        accountId = prepayObject.get("id", Long.class);
                        redisUtil.hashPut(CacheConsts.REDIS_KEY_Default_ACCOUNT_PREFIX, companyId.toString(),
                                accountId);
                    }

                    logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
                }
            } catch (Exception ex) {
                long endTime = System.currentTimeMillis();
                activityLog.setResMillsecond(endTime - startTime);
                logService.saveErrorLogAsync(activityLog, ex);

                log.error("获取默认账户出现异常【BillService->getDefaultAccountId,companyId:{}】", companyId, ex);
            }
        }

        if (redisUtil.hashHasKey(CacheConsts.REDIS_KEY_Default_ACCOUNT_PREFIX, companyId.toString())) {
            accountId = (Long) redisUtil.hashGet(CacheConsts.REDIS_KEY_Default_ACCOUNT_PREFIX, companyId.toString());
        }

        return accountId;
    }

    public void insertAccountUser(Long companyId, Long userId, Long accountId) {
        if (null == accountId || accountId.compareTo(0L) <= 0)
            return;

        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(companyId, userId, null, InterfaceEnum.ORDER_BILL_SAVE_ACCOUNT_USER,
                null);

        try {
            List<RequestCompanyAccountUserDto> accountList = new ArrayList<>();

            RequestCompanyAccountUserDto r = new RequestCompanyAccountUserDto();
            r.setAccountId(accountId);
            r.setEntityId(userId);
            r.setMode(1);
            accountList.add(r);

            activityLog.setBody(JSONUtil.toJsonStr(r));

            BaseResponse baseResponse = billFeign.saveAccountUser(accountList);

            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);

            if (baseResponse.getCode() != 0) {
                log.error("保存账户人员出现异常【BillService->insertAccountUser.bill.CompanyAccount.SaveAccountUser,companyId:{}】",
                        companyId, new Exception(baseResponse.getMessage()));
                logService.saveErrorLogAsync(activityLog, baseResponse.getMessage());
            } else {
                accountList.clear();
                logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(baseResponse));
            }
        } catch (Exception ex) {
            log.error("保存账户人员出现异常【BillService->insertAccountUser,companyId:{}】", companyId, ex);
            logService.saveErrorLogAsync(activityLog, ex);
        }
    }
}
