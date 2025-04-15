package com.ipath.orderflowservice.order.service.impl;

import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.feignclient.CacheFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestCacheDto;
import com.ipath.orderflowservice.feignclient.dto.TaxFeeModel;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.CarSource;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.bean.vo.CacheUserInfo;
import com.ipath.orderflowservice.order.dao.UserBaseMapper;
import com.ipath.orderflowservice.order.dao.bean.UserBase;
import com.ipath.orderflowservice.order.enums.CompanyConfig;
import com.ipath.orderflowservice.order.service.CacheService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserBaseMapper userBaseMapper;

    @Autowired
    private CacheFeign cacheFeign;

    @Override
    public List<Integer> getCarSource(Long companyId) {
        List<Integer> availableCarSourceList = null;
        Object sourceObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_SOURCE.replace("{}", String.valueOf(companyId));
        if (redisUtil.hasKey(key)) {
            sourceObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(sourceObject)) {
                isLoadFromCacheService = true;
                // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                redisUtil.delete(key);
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_SOURCE);
            cacheDto.setKeyParameterValue(companyId.toString());
            cacheDto.setSqlParameterValue(companyId.toString());
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (cacheResponse.getCode() != 0)
                return null;
            sourceObject = cacheResponse.getData();
        }

        if (null != sourceObject) {
            JSONArray jsonArray = JSONUtil.parseArray(sourceObject);
            if (null != jsonArray && jsonArray.size() > 0) {
                availableCarSourceList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    availableCarSourceList.add(jsonObject.getInt("sourceId"));
                }
            }
        }

        return availableCarSourceList;
    }

    @Override
    public List<Integer> getPriorSource() {
        Object obj = null;
        Boolean isLoadFromCacheService = false;

        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_PREFIX)) {
                obj = redisUtil.get(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_PREFIX);
                if (ObjectUtil.isNull(obj)) {
                    isLoadFromCacheService = true;
                    redisUtil.delete(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_PREFIX);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_ORDER_SOURCE_PRIOR_PREFIX);
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return null;

                obj = cacheResponse.getData();
            }

            if (ObjectUtil.isNull(obj)) {
                return null;
            }

            JSONObject cacheJsonObject = new JSONObject(obj);
            JSONArray priorJsonArray = cacheJsonObject.getJSONArray("cacheValue");

            return JSONUtil.toList(priorJsonArray, Integer.class);
           
        } catch (Exception ex) {
            log.error("获取优选运力出现异常【CacheService->getPriorSource】", ex);
        }

        return null;
    }

    @Override
    public String getDispatchMode(Long companyId) {
        Object useCarObject = null;
        Boolean isLoadFromCacheService = false;
        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR.replace("{}", String.valueOf(companyId));
        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(key)) {
                useCarObject = redisUtil.get(key);
                if (ObjectUtil.isEmpty(useCarObject)) {
                    isLoadFromCacheService = true;
                    // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                    redisUtil.delete(key);
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR);
                cacheDto.setKeyParameterValue(companyId.toString());
                cacheDto.setSqlParameterValue(companyId.toString());
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return null;

                useCarObject = cacheResponse.getData();
            }

            JSONArray jsonArray = JSONUtil.parseArray(useCarObject);
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.DISPATCH_MODE.getCode()
                            .equals(eachJsonObject.getStr("paraCode"))) {
                        return eachJsonObject.getStr("paraValue");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取企业派车模式配置出现异常【CacheService->getDispatchMode,companyId:{}】", companyId, ex);
        }

        return null;
    }

    @Override
    public boolean getAutoComplaint(Long companyId) {
        Object useCarObject = null;
        Boolean isLoadFromCacheService = false;

        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR.replace("{}", String.valueOf(companyId));
        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(key)) {
                useCarObject = redisUtil.get(key);
                if (ObjectUtil.isEmpty(useCarObject)) {
                    isLoadFromCacheService = true;
                    // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                    redisUtil.delete(key);
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR);
                cacheDto.setKeyParameterValue(companyId.toString());
                cacheDto.setSqlParameterValue(companyId.toString());
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return false;

                useCarObject = cacheResponse.getData();
            }

            JSONArray jsonArray = JSONUtil.parseArray(useCarObject);
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.AUTO_COMPLAINT_MODE.getCode()
                            .equals(eachJsonObject.getStr("paraCode"))) {
                        String val = eachJsonObject.getStr("paraValue");
                        if (StrUtil.isNotBlank(val) && "true".equalsIgnoreCase(val)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取企业自动投诉配置出现异常【CacheService->getAutoComplaint,companyId:{}】", companyId, ex);
        }

        return false;
    }

    @Override
    public boolean isOpenTakeDistance(Long companyId) {
        Object obj = null;
        Boolean isLoadFromCacheService = false;

        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_COMMON_CONFIG_TAKE_DISTANCE)) {
                obj = redisUtil.get(CacheConsts.REDIS_KEY_COMMON_CONFIG_TAKE_DISTANCE);
                if (ObjectUtil.isNull(obj)) {
                    isLoadFromCacheService = true;
                    redisUtil.delete(CacheConsts.REDIS_KEY_COMMON_CONFIG_TAKE_DISTANCE);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_COMMON_CONFIG_TAKE_DISTANCE);
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return false;

                obj = cacheResponse.getData();
            }

            if (ObjectUtil.isNull(obj)) {
                return false;
            }

            JSONObject cacheJsonObject = new JSONObject(obj);
            JSONObject takeDistanceJsonObject = cacheJsonObject.getJSONObject("cacheValue");
            boolean fullon = takeDistanceJsonObject.getBool("fullOn", false);// 开启接单等时
            if (fullon) {
                return true;
            }
            if (takeDistanceJsonObject.containsKey("openCompanys")) {
                JSONArray companyJsonArray = takeDistanceJsonObject.getJSONArray("openCompanys");
                for (int i = 0; i < companyJsonArray.size(); i++) {
                    if (companyId.equals(companyJsonArray.getLong(i))) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("是否开启接单距离出现异常【CacheService->isOpenTakeDistance,companyId:{}】", companyId, ex);
        }

        return false;
    }

    @Override
    public int getThresholdTimeOfMultiDriverTakeOrder(Long companyId) {
        Object useCarObject = null;
        Boolean isLoadFromCacheService = false;

        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR.replace("{}", String.valueOf(companyId));

        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(key)) {
                useCarObject = redisUtil.get(key);
                if (ObjectUtil.isEmpty(useCarObject)) {
                    isLoadFromCacheService = true;
                    // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                    redisUtil.delete(key);
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR);
                cacheDto.setKeyParameterValue(companyId.toString());
                cacheDto.setSqlParameterValue(companyId.toString());
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return 0;

                useCarObject = cacheResponse.getData();
            }

            JSONArray jsonArray = JSONUtil.parseArray(useCarObject);
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.DISPATCH_MODE.getCode()
                            .equals(eachJsonObject.getStr("paraCode"))) {
                        JSONObject dispatchJsonObject = eachJsonObject.getJSONObject("paraValue");
                        if (dispatchJsonObject.getBool("enabled", false)) {
                            return dispatchJsonObject.getInt("value");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取多司机接单门槛时间出现异常【CacheService->getThresholdTimeOfMultiDriverTakeOrder,companyId:{}】", companyId, ex);
        }

        return 0;
    }

    @Override
    public List<TaxFeeModel> getTaxFee(Long companyId) {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_BASE);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
            return null;

        List<TaxFeeModel> taxFeeModelList = new ArrayList<>();
        Map<Long, TaxFeeModel> taxFeeMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray(cacheResponse.getData());
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int index = 0; index < jsonArray.size(); index++) {
                JSONObject eachJsonObject = jsonArray.getJSONObject(index);
                if (StrUtil.isNotBlank(eachJsonObject.getStr("name"))
                        && eachJsonObject.getStr("name").startsWith(CompanyConfig.EXPENSE_SETTINGS.getCode())) {
                    Long groupId = eachJsonObject.getLong("groupId");
                    TaxFeeModel taxFeeModel = null;
                    if (taxFeeMap.containsKey(groupId)) {
                        taxFeeModel = taxFeeMap.get(groupId);
                    } else {
                        taxFeeModel = new TaxFeeModel();
                        taxFeeMap.put(groupId, taxFeeModel);
                    }
                    String name = eachJsonObject.getStr("name").replace(CompanyConfig.EXPENSE_SETTINGS.getCode() + ".",
                            "");
                    switch (name) {
                        case "feename":
                            taxFeeModel.setFeeName(eachJsonObject.getStr("value"));
                            break;
                        case "feemode":
                            taxFeeModel.setFeeMode(eachJsonObject.getInt("value"));
                            break;
                        case "feenames":
                            String value = eachJsonObject.getStr("value");
                            if (value != null && !"".equals(value)) {
                                String[] feeNameArray = value.split(",");
                                List<String> feeNames = new ArrayList<>();
                                for (String feename : feeNameArray) {
                                    feeNames.add(feename);
                                }
                                taxFeeModel.setFeeNames(feeNames);
                            }
                            break;
                        case "value":
                            taxFeeModel.setValue(eachJsonObject.getBigDecimal("value"));
                            break;
                        case "mergefeeddtail":
                            taxFeeModel.setMergeFeeDetail(eachJsonObject.getBool("value"));
                            break;
                        case "displayfeename":
                            taxFeeModel.setDisplayFeeName(eachJsonObject.getStr("value"));
                            break;
                        case "mergeestimate":
                            taxFeeModel.setMergeEstimate(eachJsonObject.getBool("value"));
                            break;
                    }
                }
            }
            for (Long key : taxFeeMap.keySet()) {
                taxFeeModelList.add(taxFeeMap.get(key));
            }
        }

        return taxFeeModelList;
    }

    @Override
    public boolean isOpenUpgrade(Long companyId) {
        Object vipObject = null;
        Boolean isLoadFromCacheService = false;
        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_VIP.replace("{}", String.valueOf(companyId));
        try {
            // 1.先通过缓存获取
            if (redisUtil.hasKey(key)) {
                vipObject = redisUtil.get(key);
                if (ObjectUtil.isEmpty(vipObject)) {
                    isLoadFromCacheService = true;
                    redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                }
            } else {
                isLoadFromCacheService = true;
            }

            // 2.如果1获取不到，再通过缓存服务获取
            if (isLoadFromCacheService) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_VIP);
                cacheDto.setKeyParameterValue(companyId.toString());
                cacheDto.setSqlParameterValue(companyId.toString());
                BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

                if (cacheResponse.getCode() != 0 || null == cacheResponse.getData())
                    return false;

                vipObject = cacheResponse.getData();
            }

            if (!StrUtil.isBlankIfStr(vipObject)) {
                JSONArray jsonArray = JSONUtil.parseArray(vipObject);
                if (null != jsonArray && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (CompanyConfig.SWITCH_FREE_UPGRADE.getCode().equals(jsonObject.getStr("name"))) {
                            return Boolean.valueOf(jsonObject.getStr("value"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("判断企业是否开启了免费升舱出现异常【CacheService->isOpenUpgrade,companyId:{}】", companyId, ex);
        }

        return false;
    }

    @Override
    public Map<String, Object> getUpgradeDelaySetting(Long companyId) {
        try {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_VIP);
            cacheDto.setKeyParameterValue(companyId.toString());
            cacheDto.setSqlParameterValue(companyId.toString());
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (cacheResponse.getCode() != 0)
                return null;

            Map<String, Object> map = new HashMap<>();
            if (!StrUtil.isBlankIfStr(cacheResponse.getData())) {
                JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
                if (null != jsonArray && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (CompanyConfig.UPGRADE_DELAY_TYPE.getCode().equals(jsonObject.getStr("name"))) {
                            map.put("calltype", jsonObject.getInt("value", 1));
                        } else if (CompanyConfig.UPGRADE_DELAY_SETTING.getCode().equals(jsonObject.getStr("name"))) {
                            JSONObject delayJsonObject = jsonObject.getJSONObject("value");
                            if (delayJsonObject.containsKey("delaytime")) {
                                map.put("delayTime", delayJsonObject.getInt("delaytime", 1));
                            }
                            if (delayJsonObject.containsKey("sort")) {
                                map.put("sort", delayJsonObject.getStr("sort"));
                            }
                        }
                    }
                }
            }

            return map;
        } catch (Exception ex) {
            log.error("获取免费升舱延时设置出现异常【CacheService->getUpgradeDelaySetting,companyId:{}】", companyId, ex);
        }
        return null;
    }

    @Override
    public boolean isPrepay(Long companyId) {
        Boolean isPrepay = false;
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_BASE);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0)
            return false;

        if (!StrUtil.isBlankIfStr(cacheResponse.getData())) {
            JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.SWITCH_PREPAY.getCode().equals(jsonObject.getStr("name"))) {
                        isPrepay = Boolean.valueOf(jsonObject.getStr("value"));
                        break;
                    }
                }
            }
        }

        return isPrepay;
    }

    @Override
    public BigDecimal getPrepayAmount(Long companyId) {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_BASE);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0) {
            return BigDecimal.ZERO;
        }

        if (!StrUtil.isBlankIfStr(cacheResponse.getData())) {
            JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.PREPAY_AMOUNT.getCode().equals(jsonObject.getStr("name"))) {
                        return jsonObject.getBigDecimal("value", BigDecimal.ZERO);
                    }
                }
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public String getEstimatePriceRule(Long companyId) {
        Object estimateRuleObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_CONFIG_COMPANY_ESTIMATEPRICERULE.replace("{}", String.valueOf(companyId));
        if (redisUtil.hasKey(key)) {
            estimateRuleObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(estimateRuleObject)) {
                isLoadFromCacheService = true;
                // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                redisUtil.delete(key);
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_ESTIMATEPRICERULE);
            cacheDto.setKeyParameterValue(companyId.toString());
            cacheDto.setSqlParameterValue(companyId.toString());
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);
            if (cacheResponse.getCode() != 0 || null == cacheResponse.getData()) {
                return null;
            }
            estimateRuleObject = cacheResponse.getData();
        }

        try {
            JSONArray jsonArray = JSONUtil.parseArray(estimateRuleObject);
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.ESTIAMTE_DISCOUNT.getCode()
                            .equals(eachJsonObject.getStr("paraCode"))) {
                        return eachJsonObject.getStr("paraValue");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取企业预估价格折扣配置出现异常【CacheService->getEstimatePriceRule,companyId:{}】", companyId, ex);
        }

        return null;
    }

    @Override
    public String getAutoComplaintConfig(Long companyId) {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_USE_CAR);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0 || null == cacheResponse.getData()) {
            return null;
        }

        try {
            JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.AUTO_COMPLAINT_FOR_CANCEL_FEE.getCode()
                            .equals(eachJsonObject.getStr("paraCode"))) {
                        return eachJsonObject.getStr("paraValue");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取企业取消费用自动投诉标识出现异常【CacheService->getAutoComplaintConfig,companyId:{}】", companyId, ex);
        }

        return null;
    }

    @Override
    public boolean isOpenHandshake(Long companyId) {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_VIP);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0)
            return false;

        if (!StrUtil.isBlankIfStr(cacheResponse.getData())) {
            JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (CompanyConfig.SWITCH_HAND_SHAKE.getCode().equals(jsonObject.getStr("name"))) {
                        return "true".equalsIgnoreCase(jsonObject.getStr("value"));
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean getExpenseFeeConfig(Long companyId) {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_COMPANY_BASE);
        cacheDto.setKeyParameterValue(companyId.toString());
        cacheDto.setSqlParameterValue(companyId.toString());
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0 || null == cacheResponse.getData()) {
            return false;
        }

        try {
            JSONArray jsonArray = JSONUtil.parseArray(cacheResponse.getData());
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject eachJsonObject = jsonArray.getJSONObject(i);
                    if (StrUtil.isNotBlank(eachJsonObject.getStr("paraCode"))
                            && eachJsonObject.getStr("paraCode").startsWith(CompanyConfig.EXPENSE_SETTINGS.getCode())) {
                        if (StrUtil.isBlank(eachJsonObject.getStr("paraValue"))) {
                            return false;
                        }

                        return Boolean.valueOf(eachJsonObject.getStr("paraValue"));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取企业配置费用出现异常【CacheService->getExpenseFeeConfig,companyId:{}】", companyId, ex);
        }

        return false;
    }

    @Override
    public JSONArray getExtralService() {
        RequestCacheDto cacheDto = new RequestCacheDto();
        cacheDto.setKey(CacheConsts.REDIS_KEY_MD_EXTRA);
        BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

        if (cacheResponse.getCode() != 0 || null == cacheResponse.getData()) {
            return null;
        }

        try {
            return JSONUtil.parseArray(cacheResponse.getData());
        } catch (Exception ex) {
            log.error("获取增值服务出现异常【CacheService->getExtralService】", ex);
        }

        return null;
    }

    @Override
    public CacheUserInfo getUserInfo(Long companyId, Long userId) {
        CacheUserInfo cacheUserInfo = null;

        RequestCacheDto userCacheDto = new RequestCacheDto();
        userCacheDto.setKey(CacheConsts.REDIS_KEY_MD_COMPANY_USER_MD);
        userCacheDto.setKeyParameterValue(String.valueOf(companyId));
        userCacheDto.setSqlParameterValue(String.valueOf(userId));
        userCacheDto.setItem(String.valueOf(userId));

        try {
            BaseResponse baseResponse = cacheFeign.getCache(userCacheDto);
            if (baseResponse.getCode() == 0 && null != baseResponse.getData()) {
                cacheUserInfo = JSONUtil.toBean(JSONUtil.toJsonStr(baseResponse.getData()), CacheUserInfo.class);
            }
        } catch (Exception ex) {
            log.error("获取用户信息出现异常【CacheService->getUserInfo,companyId:{},userId:{}】", companyId,userId, ex);
        }

        if (null == cacheUserInfo) {// 缓存未获取到时，再查一次数据库
            UserBase user = userBaseMapper.selectByPrimaryKey(userId);
            if (null != user) {
                cacheUserInfo = new CacheUserInfo();
                BeanUtil.copyProperties(user, cacheUserInfo, true);
                userCacheDto.setData(cacheUserInfo);
            }
        }

        return cacheUserInfo;
    }

    @Override
    public boolean isOpenIO(Long companyId) {
        Object ioObject = null;
        Boolean isLoadFromCacheService = false;

        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_CFG_IO_OPEN_PREFIX)) {
            ioObject = redisUtil.get(CacheConsts.REDIS_KEY_ORDER_CFG_IO_OPEN_PREFIX);
            if (ObjectUtil.isEmpty(ioObject)) {
                isLoadFromCacheService = true;
                // 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
                redisUtil.delete(CacheConsts.REDIS_KEY_ORDER_CFG_IO_OPEN_PREFIX);
            }
        } else {
            isLoadFromCacheService = true;
        }

        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_ORDER_CFG_IO_OPEN_PREFIX);
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);
            if (cacheResponse.getCode() != 0 || null == cacheResponse.getData()) {
                return false;
            }
            ioObject = cacheResponse.getData();
        }

        try {
            JSONObject cacheJsonObject = new JSONObject(ioObject);
            JSONArray jsonArray = cacheJsonObject.getJSONArray("cacheValue");

            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (companyId.equals(jsonArray.getLong(i))) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("获取io开启开关出现异常【CacheService->isOpenIO】,companyId:{}】", companyId, ex);
        }

        return false;
    }

    @Override
    public boolean hasIO(Long companyId, String costCenter, String legalEntity) {
        try {
            if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_CFG_IO_MAPPING_PREFIX)) {
                RequestCacheDto cacheDto = new RequestCacheDto();
                cacheDto.setKey(CacheConsts.REDIS_KEY_ORDER_CFG_IO_MAPPING_PREFIX);
                cacheFeign.getCache(cacheDto);
            }

            if (redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_CFG_IO_MAPPING_PREFIX)
                    && !redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_IO_MAPPING_PREFIX + companyId)) {

                Object mappingObject = redisUtil.get(CacheConsts.REDIS_KEY_ORDER_CFG_IO_MAPPING_PREFIX);
                if (ObjectUtil.isNotNull(mappingObject)) {
                    JSONObject cachJsonObject = JSONUtil.parseObj(mappingObject);
                    if (ObjectUtil.isNotNull(cachJsonObject)) {
                        JSONArray jsonArray = cachJsonObject.getJSONArray("cacheValue");
                        if (null != jsonArray) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject eacheJsonObject = jsonArray.getJSONObject(i);
                                if (companyId.equals(eacheJsonObject.getLong("companyId"))) {
                                    redisUtil.set(CacheConsts.REDIS_KEY_ORDER_IO_MAPPING_PREFIX + companyId,
                                            eacheJsonObject.getJSONArray("mapping"));
                                }
                            }
                        }
                    }
                }
            }

            if (!redisUtil.hasKey(CacheConsts.REDIS_KEY_ORDER_IO_MAPPING_PREFIX + companyId)) {
                return false;
            }

            JSONArray jsonArray = JSONUtil
                    .parseArray(redisUtil.get(CacheConsts.REDIS_KEY_ORDER_IO_MAPPING_PREFIX + companyId));

            if (null == jsonArray || jsonArray.size() == 0) {
                return false;
            }

            int type = costCenter.startsWith("2") ? 1 : 2;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject ioJsonObject = jsonArray.getJSONObject(i);
                int dbMappingType = ioJsonObject.getInt("type");
                if (dbMappingType == type) {
                    JSONArray detailJsonArray = ioJsonObject.getJSONArray("details");
                    for (int j = 0; j < detailJsonArray.size(); j++) {
                        JSONObject eachJsonObject = detailJsonArray.getJSONObject(j);
                        if (legalEntity.equals(eachJsonObject.getStr("legal_entity")))
                            return true;
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            log.error("获取io出现异常【CacheService->hasIO,companyId:{},costCenter:{},legalEntity:{}】", companyId, costCenter,
                    legalEntity, ex);
            return false;
        }
    }

    @Override
    public boolean isReportingLog(String interfacePath) {
        try {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_CONFIG_IGNORE_LOG_REPORTING);
            BaseResponse baseResponse = cacheFeign.getCache(cacheDto);

            if (baseResponse.getCode() == 0) {
                JSONObject cachJsonObject = JSONUtil.parseObj(baseResponse.getData());
                if (ObjectUtil.isNotNull(cachJsonObject)
                        && !StrUtil.isBlankIfStr(cachJsonObject.getObj("cacheValue"))) {
                    JSONArray jsonArray = cachJsonObject.getJSONArray("cacheValue");
                    List<String> ignoreList = JSONUtil.toList(jsonArray, String.class);
                    if (ObjectUtil.isNotNull(ignoreList) && ignoreList.contains(interfacePath)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            log.error("接口是否上报出现异常【CacheService->isReportingLog,interfacePath:{}】", interfacePath, ex);
            return true;
        }
    }

    @Override
    public CacheCompanyInfo getCompanyInfo(Long companyId) {
        CacheCompanyInfo companyInfo = null;
        Object companyObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_MD_COMPANY.replace("{}", String.valueOf(companyId));
        if (redisUtil.hasKey(key)) {
            companyObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(companyObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto requestCacheDto = new RequestCacheDto();
            requestCacheDto.setKey(CacheConsts.REDIS_KEY_MD_COMPANY);
            requestCacheDto.setKeyParameterValue(companyId.toString());
            requestCacheDto.setSqlParameterValue(companyId.toString());

            try {
                BaseResponse cacheResponse = cacheFeign.getCache(requestCacheDto);
                if (cacheResponse.getCode() != 0)
                    return null;
                companyObject = cacheResponse.getData();
            } catch (Exception ex) {
                log.error("获取公司信息出现异常【CacheService->getCompanyInfo,companyId:{}】", companyId, ex);
            }
        }

        if (null != companyObject) {
            companyInfo = JSONUtil.toBean(JSONUtil.toJsonStr(companyObject), CacheCompanyInfo.class);
        }

        return companyInfo;
    }

    public String getSourceName(Integer carSourceId, int language) {
        Object sourceObject = null;
        String carSourceName = null;
        Boolean isLoadFromCacheService = false;
        if (language == 1) {
            carSourceName = CarSource.getName(carSourceId);
            if (StrUtil.isNotBlank(carSourceName)) {
                return carSourceName;
            }
        }

        // 1.先通过缓存获取
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_MD_CARSOURCE)) {
            sourceObject = redisUtil.get(CacheConsts.REDIS_KEY_MD_CARSOURCE);
            if (ObjectUtil.isEmpty(sourceObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(CacheConsts.REDIS_KEY_MD_CARSOURCE);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto requestCacheDto = new RequestCacheDto();
            requestCacheDto.setKey(CacheConsts.REDIS_KEY_MD_CARSOURCE);

            try {
                BaseResponse cacheResponse = cacheFeign.getCache(requestCacheDto);
                if (cacheResponse.getCode() != 0) {
                    return null;
                }
                sourceObject = cacheResponse.getData();
            } catch (Exception ex) {
                log.error("获取运力名称出现异常【CacheService->getSourceName,carSourceId:{},language:{}】", carSourceId, language,
                        ex);
            }
        }
        if (redisUtil.hasKey(CacheConsts.REDIS_KEY_MD_CARSOURCE)) {
            JSONArray jsonArray = JSONUtil.parseArray(sourceObject);
            if (ObjectUtil.isNotNull(sourceObject)) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (carSourceId.equals(jsonObject.getInt("sourceCode", 0))) {
                        carSourceName = (language == 1 ? jsonObject.getStr("sourceName")
                                : jsonObject.getStr("sourceNameEn"));
                        break;
                    }
                }
            }
        }

        return carSourceName;
    }
}