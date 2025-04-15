package com.ipath.orderflowservice.order.bean.bo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 企业用户自定义管控企业配置
 */
@Data
public class RegulationBo {
    /**
     * 开启管控配置企业
     */
    private List<Long> companyList;

    /**
     * 开启由合作方管控配置企业
     */
    private List<Long> partnerRegulationCompanyList = new ArrayList<>();
    /**
     * 开启由合作方管控配置企业 配置
     */
    private List<PartnerRegulationCompanyConfig> partnerRegulationCompanyCofigList;
    /**
     * 开启缓存失效配置的公司
     */
    private List<Long> invalidationCompanyList;

    /**
     * 限制企业配置
     */
    private List<RegulationConfigBo> regulationConfigList;


    /**
     * 开启超限个别不提示的公司的配置的公司
     */
    private List<Long> notErroCompanyList;

    /**
     * 开启超限个别不提示的公司的配置的公司
     */
    private List<JSONObject>  notErroCompanyListConfigList;

    /**
     * 推荐上车点配置
     */
    private List<Long> recommendedLocationList;

    /**
     * 推荐上车点配置
     */
    private List<JSONObject>  recommendedLocationConfigList;


}
