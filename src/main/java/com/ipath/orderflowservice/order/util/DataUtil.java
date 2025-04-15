package com.ipath.orderflowservice.order.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.CarSource;
import com.ipath.orderflowservice.order.dao.OrderLimitConfigMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import com.ipath.orderflowservice.order.service.SystemService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DataUtil {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderLimitConfigMapper orderLimitConfigMapper;
    @Autowired
    private SystemService systemService;

    @Value("${envConfig.request.remoteCall.isGray}")
    private boolean IS_GRAY;

    @Value("${envConfig.request.remoteCall.key}")
    private String PRE_ENV_KEY;

    @Value("${envConfig.request.remoteCall.value}")
    private String PRE_ENV_VALUE;

    public JSONObject getEstimateDiscount(Long companyId) {
        JSONObject estimateDiscountJsonObject = null;
        if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LOADED)) {
            OrderLimitConfig queryOrderLimitConfig = new OrderLimitConfig();
            queryOrderLimitConfig.setType("estimatePriceDiscountRule");
            List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(queryOrderLimitConfig);
            if (null != orderLimitConfigs && orderLimitConfigs.size() > 0) {
                for (OrderLimitConfig orderLimitConfig : orderLimitConfigs) {
                    redisUtil.hashPut(CacheConsts.REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LIMIT,
                            String.valueOf(orderLimitConfig.getCompanyId()), orderLimitConfig.getValue());
                }
            }

            redisUtil.set(CacheConsts.REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LOADED, true);
        }

        if (redisUtil.hashHasKey(CacheConsts.REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LIMIT, String.valueOf(companyId))) {
            estimateDiscountJsonObject = JSONUtil.parseObj(redisUtil
                    .hashGet(CacheConsts.REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LIMIT, String.valueOf(companyId)));
        }

        return estimateDiscountJsonObject;
    }

    /**
     * 获取合规预警配置
     *
     * @param companyId
     * @throws Exception
     */
    public void getCompanyAbnormalRules(Long companyId) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("companyId", companyId);

        try {
            BaseResponse baseResponse = systemService.getCompanyAbnormalRules(jsonObject);
            if (baseResponse.getCode() != 0) {
                log.error(
                        "获取合规预警配置，调用sysytem服务接口【DataUtil->getCompanyAbnormalRules.system.AbnormalRule.GetCompanyAbnormalRules,companyId:{}】",
                        companyId, new Exception(baseResponse.getMessage()));
            }
            JSONArray jsonArray = new JSONArray(baseResponse.getData());
            if (!redisUtil.set(CacheConsts.REDIS_KEY_ABNORMAL_PREFIX + companyId.toString(),
                    JSONUtil.toJsonStr(jsonArray),
                    CacheConsts.ONE_HOUR_CACHE_EXPIRE_TIME)) {
                log.error("获取合规预警配置，写入缓存出现异常【DataUtil->getCompanyAbnormalRules,companyId:{}】", companyId);
            }
        } catch (Exception ex) {
            log.error("获取合规预警配置【DataUtil->getCompanyAbnormalRules,companyId:{}】", companyId, ex);
        }
    }

    /**
     * 设置RemoteCallDto头信息
     * 
     * @return
     */
    public void setRemoteCallDtoHeaders(RemoteCallDto remoteCallDto) {
        if (IS_GRAY) {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put(PRE_ENV_KEY, Arrays.asList(PRE_ENV_VALUE));
            remoteCallDto.setHeaders(headers);
        }
    }
}
