package com.ipath.orderflowservice.order.service.impl;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.order.bean.bo.PartnerRegulationCompanyConfig;
import com.ipath.orderflowservice.order.bean.bo.RegulationBo;
import com.ipath.orderflowservice.order.bean.bo.RegulationConfigBo;
import com.ipath.orderflowservice.order.bean.bo.RegulationConfigInvalidationBo;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.dao.OrderLimitConfigMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import com.ipath.orderflowservice.order.service.RegulationService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class RegulationServiceImpl implements RegulationService {

    @Autowired
    private OrderLimitConfigMapper orderLimitConfigMapper;

    @Autowired
    private RedisUtil redisUtil;


    //参数校验========================================================================================================

    @Override
    public boolean isInvalidationCompany(Long companyId) {
        List<Long> invalidationCompanyList = this.getRegulationConfig().getInvalidationCompanyList();
        return invalidationCompanyList.contains(companyId);

    }


    @Override
    public RegulationConfigBo invalidationCompanyConfig(Long companyId) {
        List<Long> invalidationCompanyList = this.getRegulationConfig().getInvalidationCompanyList();
        boolean contains = invalidationCompanyList.contains(companyId);
        if (contains) {
            List<RegulationConfigBo> list = this.getRegulationConfig().getRegulationConfigList().stream().filter(o ->
                    o.getCompanyId().equals(companyId)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(list)){
                return list.get(0);
            }
        }
        return null;

    }

    @Override
    public boolean isInvalidationCompany(Long companyId, Short status) {
        List<Long> invalidationCompanyList = this.getRegulationConfig().getInvalidationCompanyList();
        boolean contains = invalidationCompanyList.contains(companyId);
        if (contains) {
            List<RegulationConfigBo> list = this.getRegulationConfig().getRegulationConfigList().stream().filter(o ->
                    o.getCompanyId().equals(companyId)
                            && o.getInvalidation().getStatus().contains(status)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(list)){
                return true;
            }
        }
        return false;

    }

    //获取配置========================================================================================================

    /**
     * 获取管控配置
     *
     * @return
     */
    @Override
    public RegulationBo getRegulationConfig() {
        String key = "regulationConfig";
        RegulationBo regulationBo = new RegulationBo();
        if (redisUtil.hasKey("regulationConfig")) {
            Object o = redisUtil.get("regulationConfig");
            regulationBo = (RegulationBo) o;
        } else {
            //开启自定义管控的公司
            this.setCompanyList(regulationBo);
            //开启由合作方管控的公司
            this.setPartnerCompanyList(regulationBo);
            //开启由合作方管控的公司
            this.setPartnerCompanyConfigList(regulationBo);
            //开启缓存失效配置的公司
            this.setInvalidationCompanyList(regulationBo);
            //缓存失效配置的
            this.setInvalidationCompanyConfigList(regulationBo);
            //不提示的返回编码
            this.setNotErrorCompanyList(regulationBo);
            this.setNotErrorCompanyConfigList(regulationBo);
            //推荐上车点
            this.setRecommendedLocationList(regulationBo);
            this.setRecommendedLocationConfigList(regulationBo);

            redisUtil.set(key, regulationBo, CacheConsts.STABLE_CACHE_EXPIRE_TIME);
        }

        return regulationBo;
    }


    private void setRecommendedLocationList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("recommendedLocation");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        regulation.setRecommendedLocationList(result);
    }

    private void setRecommendedLocationConfigList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("recommendedLocation");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
            List<JSONObject> list = new ArrayList<>();
            orderLimitConfigs.stream().forEach(o -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("companyId",o.getCompanyId());
                jsonObject.set("value",o.getValue());
                list.add(jsonObject);
            });
            regulation.setRecommendedLocationConfigList(list);
        }
    }

    /**
     * 获取管控配置(刷新)
     *
     * @return
     */
    @Override
    public RegulationBo reRegulationConfig() {
        String key = "regulationConfig";
        if (redisUtil.hasKey(key)) {
            redisUtil.delete(key);
        }

        return this.getRegulationConfig();
    }

    private void setNotErrorCompanyList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("notError");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        regulation.setNotErroCompanyList(result);
    }

    private void setNotErrorCompanyConfigList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("notError");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
            List<JSONObject> list = new ArrayList<>();
            orderLimitConfigs.stream().forEach(o -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("companyId",o.getCompanyId());
                jsonObject.set("code",o.getValue());
                list.add(jsonObject);
            });
            regulation.setNotErroCompanyListConfigList(list);
        }
    }

    @Override
    public boolean isErrorByCode(CreateOrderParam orderParam, Integer code) {
        RegulationBo regulationConfig = this.getRegulationConfig();
        List<Long> notErroCompanyList = regulationConfig.getNotErroCompanyList();
        List<JSONObject> notErroCompanyListConfigList = regulationConfig.getNotErroCompanyListConfigList();

        //排除不提示的返回编码
        if(CollectionUtils.isNotEmpty(notErroCompanyList)
                && notErroCompanyList.contains(orderParam.getCompanyId())
                && CollectionUtils.isNotEmpty(notErroCompanyListConfigList)){
            List<JSONObject> list = notErroCompanyListConfigList.stream().filter(o ->o.getLong("companyId").equals(orderParam.getCompanyId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(list)){
                return true;
            }
            JSONArray jsonArray = list.get(0).getJSONArray("code");
            if(!jsonArray.contains(code)){
                return true;
            }
        }

        return false;
    }

    /**
     * 开启自定义管控的公司
     *
     * @return
     */
    public void setCompanyList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("polc");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        regulation.setCompanyList(result);
    }

    /**
     * 开启由合作方管控的公司
     *
     * @return
     */
    public void setPartnerCompanyList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("partnerRegulationCompany");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        regulation.setPartnerRegulationCompanyList(result);
    }

    /**
     * 开启缓存失效配置的公司
     *
     * @return
     */
    public void setInvalidationCompanyList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("invalidationCompany");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        regulation.setInvalidationCompanyList(result);
    }

    /**
     * 缓存失效配置的
     *
     * @return
     */
    public void setInvalidationCompanyConfigList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("invalidationCompany");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
            List<RegulationConfigBo> regulationConfigList = new ArrayList<>();
            orderLimitConfigs.stream().forEach(o -> {
                RegulationConfigBo regulationConfigBo = new RegulationConfigBo();
                String value = o.getValue();
                Long companyId = o.getCompanyId();
                if (StringUtils.isNotBlank(value)) {
                    RegulationConfigInvalidationBo regulationConfigInvalidationBo = JSONUtil.toBean(value, RegulationConfigInvalidationBo.class);
                    regulationConfigBo.setCompanyId(companyId);
                    regulationConfigBo.setInvalidation(regulationConfigInvalidationBo);
                }
                regulationConfigList.add(regulationConfigBo);
            });
            regulation.setRegulationConfigList(regulationConfigList);
        }
    }


    /**
     * 缓存失效配置的
     *
     * @return
     */
    public void setPartnerCompanyConfigList(RegulationBo regulation) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("partnerRegulationCompany");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        if (CollectionUtils.isNotEmpty(orderLimitConfigs)) {
            List<PartnerRegulationCompanyConfig> list = new ArrayList<>();
            orderLimitConfigs.stream().forEach(o -> {
                PartnerRegulationCompanyConfig config = new PartnerRegulationCompanyConfig();
                String value = o.getValue();
                Long companyId = o.getCompanyId();
                if (StringUtils.isNotBlank(value)) {
                    JSONObject jsonObject = new JSONObject(value);
                    config.setCompanyId(companyId);
                    config.setCustomer(jsonObject.getStr("customer"));
                }
                list.add(config);
            });
            regulation.setPartnerRegulationCompanyCofigList(list);
        }
    }


}