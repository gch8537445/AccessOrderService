package com.ipath.orderflowservice.ka.configuration.service;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestCheckOrderDto;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;

/**
 * ka端配置服务 Service
 */
public interface ConfigurationService {

    /**
     * 查询场景详情
     * @return
     */
    BaseResponse getSceneInfo(CreateOrderParam orderParam,String traceId) throws Exception;

    /**
     * 场景严重
     * @param requestCheckOrderDto
     * @return
     * @throws Exception
     */
    BaseResponse checkOrder(RequestCheckOrderDto requestCheckOrderDto,String traceId) throws Exception;
}
