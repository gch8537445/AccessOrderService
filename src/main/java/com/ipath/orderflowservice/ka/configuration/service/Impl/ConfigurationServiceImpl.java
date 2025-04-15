package com.ipath.orderflowservice.ka.configuration.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.orderflowservice.feignclient.ConfigurationFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestCheckOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSceneInfoDto;
import com.ipath.orderflowservice.ka.configuration.service.ConfigurationService;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ka端配置服务 ServiceImpl
 */
@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationFeign configurationFeign;
    @Autowired
    private LogService logService;

    @Override
    public BaseResponse getSceneInfo(CreateOrderParam orderParam, String traceId) throws Exception {
        BaseResponse response = null;
        long startTime = System.currentTimeMillis();

        Log activityLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                InterfaceEnum.ORDER_GETSCENEINFO, traceId);
        try {
            RequestSceneInfoDto requestSceneInfoDto = new RequestSceneInfoDto();
            BeanUtil.copyProperties(orderParam, requestSceneInfoDto, false);
            requestSceneInfoDto.setOrderTime(orderParam.getDepartTime());
            activityLog.setBody(JSONUtil.toJsonStr(requestSceneInfoDto));
            response = configurationFeign.getSceneInfo(requestSceneInfoDto);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }

        if (response.getCode() != 0) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, new Exception(response.getMessage()));
            throw new BusinessException(response.getMessage());
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));

        return response;
    }

    @Override
    public BaseResponse checkOrder(RequestCheckOrderDto requestCheckOrderDto, String traceId) throws Exception {
        BaseResponse response = null;
        long startTime = System.currentTimeMillis();
        Log activityLog = logService.getLog(requestCheckOrderDto.getCompanyId(), requestCheckOrderDto.getUserId(),
                requestCheckOrderDto.getOrderId(),
                InterfaceEnum.ORDER_CONFIGURATION_CHECK_ORDER, traceId);
        activityLog.setBody(JSONUtil.toJsonStr(requestCheckOrderDto));
        try {
            response = configurationFeign.checkOrder(requestCheckOrderDto);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }

        if (response.getCode() != 0) {
            long endTime = System.currentTimeMillis();
            activityLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(activityLog, new Exception(response.getMessage()));
        }

        long endTime = System.currentTimeMillis();
        activityLog.setResMillsecond(endTime - startTime);
        logService.saveLogAsync(activityLog, JSONUtil.toJsonStr(response));

        return response;
    }
}
