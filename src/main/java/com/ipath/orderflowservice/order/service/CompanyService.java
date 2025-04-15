package com.ipath.orderflowservice.order.service;


import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceConfigVo;
import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceUpgradeConfigVo;
import com.ipath.orderflowservice.order.dao.bean.CompanyHolidaysConfig;
import com.ipath.orderflowservice.order.dao.bean.CompanyLocations;

import java.util.Date;
import java.util.List;

/**
 * 企业 Service
 */
public interface CompanyService {

    /**
     * 查询企业 节假日配置
     *
     * @param qurey
     * @return
     */
    List<CompanyHolidaysConfig> selectCompanyHolidaysConfig(CompanyHolidaysConfig qurey);

    /**
     * 查询企业 指定日期是否为节假日
     *
     * @param companyId
     * @param date
     * @return
     */
    boolean isHoliday(Long companyId, Date date);

    /**
     * 查询企业 人工标注点列表（b类点）
     *
     * @param companyId
     * @return
     */
    List<CompanyLocations> getCompanyRecommendedLocationByCompanyId(Long companyId);

    /**
     * 查询企业 自动重新派单配置
     *
     * @param companyId
     * @return
     */
    CompanyRePlaceConfigVo getCompanyRePlaceConfig(Long companyId);

    /**
     * 查询企业 重新派车自动升舱配置
     *
     * @param companyId
     * @return
     */
    CompanyRePlaceUpgradeConfigVo getCompanyRePlaceUpgradeConfig(Long companyId);
}
