package com.ipath.orderflowservice.order.business.cartypelabel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.feignclient.CacheFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestCacheDto;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.CacheCarTypeLabelOrder;
import com.ipath.orderflowservice.order.bean.vo.CacheEstimateResult;
import com.ipath.orderflowservice.order.bean.vo.EstimateCar;
import com.ipath.orderflowservice.order.bean.vo.EstimatePriceResult;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.*;
import com.ipath.orderflowservice.order.business.cartypelabel.service.CarTypeLabelService;
import com.ipath.orderflowservice.order.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: qy
 * @create: 2024-10-15 14:39
 **/
@Service
@Slf4j
public class CarTypeLabelServiceImpl implements CarTypeLabelService {

    @Autowired
    private CacheFeign cacheFeign;

    @Autowired
    private SnowFlakeUtil snowFlakeUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Set<CarTypeLabelEstimateVo> getEstimateResponseNew(CreateOrderParam orderParam,
            List<CarTypeLabelVo> carTypeLabelEnable, List<TemplateVo> estimateResponses,
            List<CacheEstimateResult> carList) {
        Set<CarTypeLabelEstimateVo> estimateResponseList = new HashSet<>();
        try {
            // 先按标签分组
            estimateResponseList = carTypeLabelGroupByCars(orderParam, carTypeLabelEnable, carList);

            // 设置是否显示
            estimateResponseList = judgeCarTypeLabelTemplate(orderParam, estimateResponses, estimateResponseList);
        } catch (Exception e) {
            log.info("处理车型标签预估异常: ", e);
        }

        return estimateResponseList;
    }

    @Override
    public List<CacheCarTypeLabelOrder> getUserCarTypeLabelFinish(Long userId) {
        Object obj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_CONFIG_CAR_TYPE_USER_COMPLETE_ORDER,
                userId.toString(), null, null);
        if (null == obj)
            return null;

        JSONObject tempalteJsonObject = new JSONObject(obj);

        JSONArray templateJsonArray = tempalteJsonObject.getJSONArray("templates");

        if (null == templateJsonArray || templateJsonArray.size() == 0)
            return null;

        List<CacheCarTypeLabelOrder> templateDataList = JSONUtil.toList(templateJsonArray,
                CacheCarTypeLabelOrder.class);
        return templateDataList;
    }

    @Override
    public List<CacheCarTypeLabelOrder> getUserCarTypeLabelFinishNew(Long userId, Long companyId) {
        List<CacheCarTypeLabelOrder> result = new ArrayList<>();
        // 1. 获取用户维度预估信息
        Object userRuleObj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_FINISH_USER,
                StrUtil.toString(userId), null, null);
        if (ObjectUtil.isNotEmpty(userRuleObj)) {
            JSONObject data = JSONUtil.parseObj(userRuleObj);
            if (ObjectUtil.isNotEmpty(data)) {
                List<CacheCarTypeLabelOrder> temp = data.getBeanList("templates", CacheCarTypeLabelOrder.class);
                if (CollectionUtil.isNotEmpty(temp)) {
                    result.addAll(temp);
                }
            }
        }
        // 2. 获取企业维度的预估信息
        Object companyRuleObj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_FINISH_COMPANY,
                StrUtil.toString(companyId), null, null);
        if (ObjectUtil.isNotEmpty(companyRuleObj)) {
            List<CarTypeLabelCompanyLogicVo> companyRuleList = JSONUtil.toList(JSONUtil.toJsonStr(companyRuleObj),
                    CarTypeLabelCompanyLogicVo.class);
            // 解析企业维度预估信息
            if (CollectionUtil.isNotEmpty(companyRuleList)) {
                for (CarTypeLabelCompanyLogicVo companyRule : companyRuleList) {
                    Long configId = ObjectUtil.isEmpty(companyRule.getTemplateId())
                            ? companyRule.getTemplateVo().getId()
                            : companyRule.getTemplateId();
                    String key = StrUtil.format("{}_{}_{}", companyRule.getLogicType(), configId, userId);

                    boolean hasKey = redisUtil
                            .hasKey(StrUtil.format(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_FINISH_USER_SIMPLE, key));

                    if (NumberUtil.equals(companyRule.getLogicType(), 1)) {
                        // 排除用户
                        // 如果 缓存中有这个key, 则不保存这个配置
                        // 如果 缓存中没有这个key, 则保存这个配置
                        if (!hasKey) {
                            result.add(
                                    BeanUtil.copyProperties(companyRule.getTemplateVo(), CacheCarTypeLabelOrder.class));
                        }
                    } else if (NumberUtil.equals(companyRule.getLogicType(), 2)) {
                        // 包含用户
                        // 如果 缓存中有这个key, 则保存这个配置
                        // 如果 缓存中没有这个key, 则不保存这个配置
                        if (hasKey) {
                            result.add(
                                    BeanUtil.copyProperties(companyRule.getTemplateVo(), CacheCarTypeLabelOrder.class));
                        }
                    }
                }
            }
        }

        if (CollectionUtil.isNotEmpty(result)) {
            // 排序, 有用户id放在前面, 没有用户id的按照id 正序,保证先创建的在前面
            result.sort(Comparator
                    .comparing((CacheCarTypeLabelOrder item) -> ObjectUtil.isNotEmpty(item.getUserId()),
                            Comparator.reverseOrder())
                    .thenComparing(CacheCarTypeLabelOrder::getId));
        }

        return result;
    }

    /**
     * 按企业标签车型分组
     *
     * @param carTypeLabelEnable
     * @param carList
     * @return
     */
    private Set<CarTypeLabelEstimateVo> carTypeLabelGroupByCars(CreateOrderParam orderParam,
            List<CarTypeLabelVo> carTypeLabelEnable, List<CacheEstimateResult> carList) {
        log.info("按企业标签分组开始...");
        Set<CarTypeLabelEstimateVo> result = new HashSet<>();
        if (ObjectUtil.isNotEmpty(carTypeLabelEnable) && ObjectUtil.isNotEmpty(carList)) {

            // cityCode判断
            carTypeLabelEnable = carTypeLabelEnable.stream().filter(item -> {
                List<String> cityCodes = JSONUtil.toList(item.getCityCode(), String.class);
                if (CollectionUtil.isEmpty(cityCodes)) {
                    return true;
                } else {
                    if (CollectionUtil.isEmpty(cityCodes) || cityCodes.contains(null)) {
                        return true;
                    }
                    if (cityCodes.contains(orderParam.getDepartCityCode())) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());

            // 获取预估返回的所有可用运力列表
            List<EstimateCar> cars = carList.stream().map(CacheEstimateResult::getList).flatMap(List::stream)
                    .collect(Collectors.toList());
            for (CarTypeLabelVo carTypeLabelVo : carTypeLabelEnable) {

                CarTypeLabelEstimateVo carTypeLabelEstimate = result.stream()
                        .filter(item -> item.getLabelCode().equals(carTypeLabelVo.getLabelCode())).findFirst()
                        .orElse(new CarTypeLabelEstimateVo());
                carTypeLabelEstimate.setId(snowFlakeUtil.getNextId());
                carTypeLabelEstimate.setCheckAll(false);
                // 提前创建并复用列表
                List<EstimateCar> tempEstimateCarList = carTypeLabelEstimate.getList();
                if (ObjectUtil.isEmpty(tempEstimateCarList)) {
                    tempEstimateCarList = new ArrayList<>();
                    carTypeLabelEstimate.setList(tempEstimateCarList);
                }

                // 判断预估返回的运力是否在 标签内
                for (EstimateCar estimateCar : cars) {
                    if (ObjectUtil.equals(estimateCar.getSubCarType(), carTypeLabelVo.getCarTypeCode())
                            && ObjectUtil.equals(estimateCar.getIpathCode(), carTypeLabelVo.getIpathCode())
                            && ObjectUtil.equals(StrUtil.toString(estimateCar.getCarSourceId()),
                                    carTypeLabelVo.getSourceCode())

                    ) {
                        log.info("预估返回运力在标签内 :labelCode: {}, carSource: {}, subCarType: {}",
                                carTypeLabelVo.getLabelCode(), estimateCar.getCarSourceId(),
                                estimateCar.getSubCarType());
                        // 判断返回的运力是否在城市规则显示内
                        // 从此处移除加入到获取缓存结果后立即验证
                        /*
                         * if (!checkCityMapping(orderParam, estimateCar, cityMapping)) {
                         * log.info("城市校验不通过: 运力信息: carSource: {}, subCarType: {}",
                         * estimateCar.getCarSourceId(), estimateCar.getSubCarType());
                         * continue;
                         * }
                         */

                        // 重新copy一份运力信息
                        EstimateCar carTypeEstimateCar = BeanUtil.copyProperties(estimateCar, EstimateCar.class);
                        carTypeEstimateCar.setLabelCode(carTypeLabelVo.getLabelCode());
                        carTypeEstimateCar.setLabelName(carTypeLabelVo.getLabelName());
                        // carTypeEstimateCar.setCarSource(StrUtil.format("{}{}",
                        // carTypeLabelVo.getSourceName(), carTypeLabelVo.getCarTypeName()));
                        tempEstimateCarList.add(carTypeEstimateCar);

                        carTypeLabelEstimate.setLabelName(carTypeLabelVo.getLabelName());
                        carTypeLabelEstimate.setLabelDesc(carTypeLabelVo.getLabelDesc());
                        carTypeLabelEstimate.setLabelCode(carTypeLabelVo.getLabelCode());

                    }
                }
                if (CollectionUtil.isNotEmpty(carTypeLabelEstimate.getList())) {
                    BigDecimal max = carTypeLabelEstimate.getList().stream().map(EstimateCar::getEstimatePrice)
                            .distinct().max(BigDecimal::compareTo).get();
                    BigDecimal min = carTypeLabelEstimate.getList().stream().map(EstimateCar::getEstimatePrice)
                            .distinct().min(BigDecimal::compareTo).get();
                    carTypeLabelEstimate.setPriceLab(
                            NumberUtil.equals(max, min) ? StrUtil.toString(max) : StrUtil.format("{}-{}", min, max));
                    List<EstimateCar> collect = carTypeLabelEstimate.getList().stream()
                            .sorted(Comparator.comparing(EstimateCar::getEstimatePrice)).collect(Collectors.toList());
                    carTypeLabelEstimate.setList(collect);
                    result.add(carTypeLabelEstimate);
                }
            }

        }
        log.info("按企业标签分组结束: {}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 验证城市规则是否通过
     *
     * @param estimateCar
     * @param cityMapping
     * @return
     */
    @Override
    public boolean checkCityMapping(CreateOrderParam orderParam, EstimatePriceResult estimateCar,
            List<CarTypeLabelCityMapping> cityMapping) {
        boolean result = true;
        // 帅选有没有相同城市的规则, 没有就筛选所有城市code为空的规则
        if (ObjectUtil.isNotEmpty(cityMapping)) {

            List<CarTypeLabelCityMapping> tempCityMapping = cityMapping.stream()
                    .filter(item -> StrUtil.equals(orderParam.getDepartCityCode(), item.getCityCode()))
                    .collect(Collectors.toList());
            if (ObjectUtil.isEmpty(tempCityMapping)) {
                tempCityMapping = cityMapping.stream().filter(item -> StrUtil.isBlank(item.getCityCode()))
                        .collect(Collectors.toList());
            }

            if (ObjectUtil.isNotEmpty(tempCityMapping)) {
                // log.info("城市规则, 城市信息命中: city: {}", orderParam.getDepartCityName());
                // 先筛选SourceCode
                tempCityMapping = tempCityMapping.stream()
                        .filter(item -> StrUtil.equals(estimateCar.getCarSource().toString(), item.getSourceCode()))
                        .collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(tempCityMapping)) {
                    // log.info("城市规则, carSource 信息命中: carSource: {}", estimateCar.getCarSource());
                    tempCityMapping = tempCityMapping.stream()
                            .filter(item -> ObjectUtil.equals(estimateCar.getSubCarType(), item.getCarTypeCode()))
                            .collect(Collectors.toList());
                    if (ObjectUtil.isEmpty(tempCityMapping)) {
                        result = false;
                        // log.info("城市规则 subCarType 信息未命中 : {}", estimateCar.getSubCarType());
                    } else {
                        // log.info("城市规则 subCarType 信息命中");
                    }
                } else {
                    // log.info("城市规则, carSource 信息未命中: carSource: {}", estimateCar.getCarSource().toString());
                }
            } else {
                // log.info("城市规则, 城市信息未命中: city: {}",
                //         StrUtil.format("{} - {}", orderParam.getDepartCityCode(), orderParam.getDepartCityName()));
            }

            // result = false;
            // for (CarTypeLabelCityMapping temp : tempCityMapping) {
            // // 判断平台是否符合
            // if (StrUtil.isNotBlank(temp.getSourceCode()) &&
            // !StrUtil.equals(temp.getSourceCode(),
            // StrUtil.toString(estimateCar.getCarSourceId()))
            // ) {
            // continue;
            // }
            //
            // // 车型判断
            // if (StrUtil.isNotBlank(temp.getCarTypeCode()) &&
            // !ObjectUtil.equals(estimateCar.getSubCarType(), temp.getCarTypeCode())) {
            // continue;
            // }
            // result = true;
            // log.info("城市校验通过: 运力信息: carSource: {}, subCarType: {}, cityId: {}",
            // estimateCar.getCarSourceId(), estimateCar.getSubCarType(), temp.getId());
            // break;
            // }
        }

        return result;
    }

    /**
     * 校验标签车型是否命中规则并设置参数
     *
     * @param estimateResponses
     * @param labelSet
     * @return
     */
    private Set<CarTypeLabelEstimateVo> judgeCarTypeLabelTemplate(CreateOrderParam orderParam,
            List<TemplateVo> estimateResponses, Set<CarTypeLabelEstimateVo> labelSet) {
        if (ObjectUtil.isNotEmpty(labelSet)) {
            labelSet = labelSet.stream()
                    .sorted(Comparator.comparing((CarTypeLabelEstimateVo item) -> item.getList().stream()
                            .min(Comparator.comparing(EstimateCar::getEstimatePrice))
                            .map(EstimateCar::getEstimatePrice)
                            .orElse(BigDecimal.ZERO))
                            .thenComparing(CarTypeLabelEstimateVo::getLabelCode))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            if (ObjectUtil.isNotEmpty(estimateResponses)) {
                for (CarTypeLabelEstimateVo carTypeLabelEstimateVo : labelSet) {
                    ls: for (TemplateVo estimateResponse : estimateResponses) {
                        List<String> labelCodes = StrUtil.split(estimateResponse.getOutItem1(), ",");
                        List<String> selecteds = StrUtil.split(estimateResponse.getOutItem2(), ",");

                        if (ObjectUtil.isNotEmpty(labelCodes) && ObjectUtil.isNotEmpty(selecteds)
                                && labelCodes.size() == selecteds.size()) {

                            // 如果标签code和预估参数code一致，则设置选中状态
                            if (checkSourceParam(orderParam, estimateResponse, carTypeLabelEstimateVo.getList())) {
                                for (int i = 0; i < labelCodes.size(); i++) {
                                    if (StrUtil.equals(carTypeLabelEstimateVo.getLabelCode(), labelCodes.get(i))) {
                                        carTypeLabelEstimateVo.setCheckAll(BooleanUtil.toBoolean(selecteds.get(i)));
                                        break ls;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        return labelSet;
    }

    /**
     * 校验预估运力参数
     *
     * @param estimateCars
     * @param estimateResponse
     * @return
     */
    private boolean checkSourceParam(CreateOrderParam orderParam, TemplateVo estimateResponse,
            List<EstimateCar> estimateCars) {
        // if (StrUtil.isNotEmpty(estimateResponse.getUserId())) {
        // return true;
        // }

        // 城市code校验
        if (StrUtil.isNotBlank(estimateResponse.getCityCode())
                && !StrUtil.equals(estimateResponse.getCityCode(), orderParam.getDepartCityCode())) {
            log.info(StrUtil.format("城市code校验不通过: 规则id: {}, 参数cityCode: {}, 规则cityCode: {}", estimateResponse.getId(),
                    orderParam.getDepartCityCode(), estimateResponse.getCityCode()));
            return false;
        }

        // 运力区间参数校验
        String orderAmount = estimateResponse.getOrderAmount();
        String distance = estimateResponse.getDistance();
        String duration = estimateResponse.getDuration();
        boolean result = false;

        try {
            for (EstimateCar estimateCar : estimateCars) {

                if (StrUtil.isNotBlank(orderAmount)) {
                    if (!intervalJudgment(estimateCar.getEstimatePrice(), orderAmount)) {
                        log.info("运力: {}, 规则id: {}, 预估金额区间判断不满足: 规则区间: {}, 预估信息: {}",
                                StrUtil.format("{} - {}", estimateCar.getCarSourceId(), estimateCar.getSubCarType()),
                                estimateResponse.getId(), orderAmount, JSONUtil.toJsonStr(estimateCar));
                        continue;
                    }
                }

                if (StrUtil.isNotBlank(distance)) {
                    if (!intervalJudgment(estimateCar.getEstimateDistance(), distance)) {
                        log.info("运力: {},规则id: {}, 预估距离区间判断不满足: 规则区间: {}, 预估信息: {}",
                                StrUtil.format("{} - {}", estimateCar.getCarSourceId(), estimateCar.getSubCarType()),
                                estimateResponse.getId(), distance, JSONUtil.toJsonStr(estimateCar));
                        continue;
                    }
                }

                if (StrUtil.isNotBlank(duration)) {
                    if (!intervalJudgment(estimateCar.getEstimateTime(), duration)) {
                        log.info("运力: {},规则id: {}, 预估时间区间判断不满足: 规则区间: {}, 预估信息: {}",
                                StrUtil.format("{} - {}", estimateCar.getCarSourceId(), estimateCar.getSubCarType()),
                                estimateResponse.getId(), duration, JSONUtil.toJsonStr(estimateCar));
                        continue;
                    }
                }

                result = true;
                log.info("运力: {}, 规则id: {}, 选中验证通过: 预估信息: {}",
                        StrUtil.format("{} - {}", estimateCar.getCarSourceId(), estimateCar.getSubCarType()),
                        estimateResponse.getId(), JSONUtil.toJsonStr(estimateResponse));
                break;
            }
        } catch (Exception e) {
            log.info("预估参数区间判断异常:", e);
        }

        return result;
    }

    private static boolean intervalJudgment(BigDecimal numerical, String interval) {
        try {
            if (StrUtil.isNotEmpty(interval)) {
                String[] orderAmountStrArr = interval.split("-");
                BigDecimal min = new BigDecimal(StrUtil.trim(orderAmountStrArr[0]));
                if (orderAmountStrArr.length == 2) {
                    BigDecimal max = new BigDecimal(StrUtil.trim(orderAmountStrArr[1]));
                    return NumberUtil.isGreaterOrEqual(numerical, min) && NumberUtil.isLessOrEqual(numerical, max);
                } else {
                    return NumberUtil.isGreater(numerical, min);
                }
            }
            return false;
        } catch (Exception e) {
            log.info("区间判断异常: 区间值: {},", interval);
            return false;
        }
    }

    /**
     * 获取标签内容配置
     *
     * @return
     */
    @Override
    public List<CarTypeLabelVo> getCarTypeLabelEnable(Long companyId) {
        // 减少文件日志输出 modify by huzhen 2025/04/08
        Object carTypeLabelObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_ENABLE.replace("{}", String.valueOf(companyId));
        if (redisUtil.hasKey(key)) {
            carTypeLabelObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(carTypeLabelObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_ENABLE);
            cacheDto.setKeyParameterValue(String.valueOf(companyId));
            cacheDto.setSqlParameterValue(String.valueOf(companyId));
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (ObjectUtil.isNull(cacheResponse) || cacheResponse.getCode() != 0) {
                return null;
            }

            carTypeLabelObject = cacheResponse.getData();
        }

        return JSON.parseArray(JSONUtil.toJsonStr(carTypeLabelObject), CarTypeLabelVo.class);
    }

    /**
     * 获取符合标签车型预估条件的用户画像信息
     *
     * @param userId 用户id
     * @return
     */
    public List<TemplateVo> getUserCarTypeLabelEstimate(Long userId) {
        RequestCacheDto dto = new RequestCacheDto();
        dto.setKey(CacheConsts.REDIS_KEY_COMPANY_CAR_TYPE_LABEL_ESTIMATE_USER);
        dto.setKeyParameterValue(String.valueOf(userId));
        dto.setSqlParameterValue(String.valueOf(userId));
        BaseResponse baseResponse = cacheFeign.getCache(dto);
        if (ObjectUtil.isNotEmpty(baseResponse) && baseResponse.getCode() == 0) {
            JSONObject data = JSONUtil.parseObj(baseResponse.getData());
            if (ObjectUtil.isNotEmpty(data)) {
                List<TemplateVo> templates = data.getBeanList("templates", TemplateVo.class);
                // 排序, 有用户id放在前面, 没有用户id的按照id 正序,保证先创建的在前面
                templates.sort(Comparator
                        .comparing((TemplateVo item) -> ObjectUtil.isNotEmpty(item.getUserId()),
                                Comparator.reverseOrder())
                        .thenComparing(TemplateVo::getId));
                return templates;
            }
        }
        return null;
    }

    @Override
    public List<TemplateVo> getUserCarTypeLabelEstimateNew(Long userId, Long companyId) {
        List<TemplateVo> result = new ArrayList<>();
        // 1. 获取用户维度预估信息
        Object userRuleObj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_USER,
                StrUtil.toString(userId), null, null);
        if (ObjectUtil.isNotEmpty(userRuleObj)) {
            JSONObject data = JSONUtil.parseObj(userRuleObj);
            if (ObjectUtil.isNotEmpty(data)) {
                List<TemplateVo> temp = data.getBeanList("templates", TemplateVo.class);
                if (CollectionUtil.isNotEmpty(temp)) {
                    result.addAll(temp);
                }
            }
        }
        // 2. 获取企业维度的预估信息
        Object companyRuleObj = cacheUtil.getCacheInfo(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_COMPANY,
                StrUtil.toString(companyId), null, null);
        if (ObjectUtil.isNotEmpty(companyRuleObj)) {
            List<CarTypeLabelCompanyLogicVo> companyRuleList = JSONUtil.toList(JSONUtil.toJsonStr(companyRuleObj),
                    CarTypeLabelCompanyLogicVo.class);
            // 解析企业维度预估信息
            if (CollectionUtil.isNotEmpty(companyRuleList)) {
                for (CarTypeLabelCompanyLogicVo companyRule : companyRuleList) {
                    Long configId = ObjectUtil.isEmpty(companyRule.getTemplateId())
                            ? companyRule.getTemplateVo().getId()
                            : companyRule.getTemplateId();
                    String key = StrUtil.format("{}_{}_{}", companyRule.getLogicType(), configId, userId);

                    boolean hasKey = redisUtil
                            .hasKey(StrUtil.format(CacheConsts.REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_USER_SIMPLE, key));

                    if (NumberUtil.equals(companyRule.getLogicType(), 1)) {
                        // 排除用户
                        // 如果 缓存中有这个key, 则不保存这个配置
                        // 如果 缓存中没有这个key, 则保存这个配置
                        if (!hasKey) {
                            result.add(companyRule.getTemplateVo());
                        }
                    } else if (NumberUtil.equals(companyRule.getLogicType(), 2)) {
                        // 包含用户
                        // 如果 缓存中有这个key, 则保存这个配置
                        // 如果 缓存中没有这个key, 则不保存这个配置
                        if (hasKey) {
                            result.add(companyRule.getTemplateVo());
                        }
                    }
                }
            }
        }

        // 排序, 有用户id放在前面, 没有用户id的按照id 正序,保证先创建的在前面
        if (CollectionUtil.isNotEmpty(result)) {
            result.sort(Comparator
                    .comparing((TemplateVo item) -> ObjectUtil.isNotEmpty(item.getUserId()), Comparator.reverseOrder())
                    .thenComparing(TemplateVo::getId));
        }

        return result;
    }

    /**
     * 获取标签车型城市映射关系
     *
     * @param companyId
     * @return
     */
    @Override
    public List<CarTypeLabelCityMapping> getCityCarTypeLabelEstimate(Long companyId) {

        // 减少文件日志输出 modify by huzhen 2025/04/08
        Object carTypeLabelCityMappingObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_COMPANY_CAR_TYPE_LABEL_CITY_ENABLE;
        if (redisUtil.hasKey(key)) {
            carTypeLabelCityMappingObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(carTypeLabelCityMappingObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(key);
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (ObjectUtil.isNull(cacheResponse) || cacheResponse.getCode() != 0) {
                return null;
            }

            carTypeLabelCityMappingObject = cacheResponse.getData();
        }

        return JSON.parseArray(JSONUtil.toJsonStr(carTypeLabelCityMappingObject), CarTypeLabelCityMapping.class);
    }

    @Override
    public List<CarBaseVo> getBaseInfo() {
        RequestCacheDto dto = new RequestCacheDto();
        dto.setKey(CacheConsts.REDIS_KEY_BASE_INFO);
        BaseResponse baseResponse = cacheFeign.getCache(dto);
        if (ObjectUtil.isNotEmpty(baseResponse) && baseResponse.getCode() == 0) {
            return JSON.parseArray(JSONUtil.toJsonStr(baseResponse.getData()), CarBaseVo.class);
        }
        return null;
    }

    @Override
    public List<CarSourceVo> getSourceInfo() {
        // 减少文件日志输出 modify by huzhen 2025/04/08
        Object carSourceObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_SOURCE_INFO;
        if (redisUtil.hasKey(key)) {
            carSourceObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(carSourceObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(key);
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (ObjectUtil.isNull(cacheResponse) || cacheResponse.getCode() != 0) {
                return null;
            }

            carSourceObject = cacheResponse.getData();
        }

        return JSON.parseArray(JSONUtil.toJsonStr(carSourceObject), CarSourceVo.class);
    }

    @Override
    public List<CarLevelVo> getCarLevelInfo() {

        // 减少文件日志输出 modify by huzhen 2025/04/08
        Object carLevelObject = null;
        Boolean isLoadFromCacheService = false;

        // 1.先通过缓存获取
        String key = CacheConsts.REDIS_KEY_CAR_LEVEL_INFO;
        if (redisUtil.hasKey(key)) {
            carLevelObject = redisUtil.get(key);
            if (ObjectUtil.isEmpty(carLevelObject)) {
                isLoadFromCacheService = true;
                redisUtil.delete(key);// 确保缓存服务可以正常获取到信息，先删除此key的缓存信息
            }
        } else {
            isLoadFromCacheService = true;
        }

        // 2.如果1获取不到，再通过缓存服务获取
        if (isLoadFromCacheService) {
            RequestCacheDto cacheDto = new RequestCacheDto();
            cacheDto.setKey(key);
            BaseResponse cacheResponse = cacheFeign.getCache(cacheDto);

            if (ObjectUtil.isNull(cacheResponse) || cacheResponse.getCode() != 0) {
                return null;
            }

            carLevelObject = cacheResponse.getData();
        }

        return JSON.parseArray(JSONUtil.toJsonStr(carLevelObject), CarLevelVo.class);
    }
}
