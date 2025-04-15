package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.feignclient.SystemFeign;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.CompanyConstant;
import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceConfigVo;
import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceUpgradeConfigVo;
import com.ipath.orderflowservice.order.dao.CompanyHolidaysConfigMapper;
import com.ipath.orderflowservice.order.dao.CompanyLocationsMapper;
import com.ipath.orderflowservice.order.dao.bean.CompanyHolidaysConfig;
import com.ipath.orderflowservice.order.dao.bean.CompanyLocations;
import com.ipath.orderflowservice.order.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业 ServiceImpl
 */
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    @Resource
    private CompanyHolidaysConfigMapper companyHolidaysConfigMapper;
    @Autowired
    private CompanyLocationsMapper companyLocationsMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private SystemFeign systemFeign;

    /**
     * 查询企业节假日配置
     *
     * @param qurey
     * @return
     */
    @Override
    public List<CompanyHolidaysConfig> selectCompanyHolidaysConfig(CompanyHolidaysConfig qurey) {
        try {
            String key = CompanyConstant.COMPANY_CONFIG_HOLIDAYS;
            String item = qurey.getCompanyId().toString() + "_" + qurey.getYear();
            List<CompanyHolidaysConfig> companyHolidaysConfigs = null;
            boolean has = redisUtil.hashHasKey(key, item);
            if (has) {
                String value = JSONUtil.toJsonStr(redisUtil.hashGet(key, item));
                companyHolidaysConfigs = JSONUtil.toList(value, CompanyHolidaysConfig.class);
            } else {
                companyHolidaysConfigs = companyHolidaysConfigMapper.selectCompanyHolidaysConfig(qurey);
                redisUtil.hashPut(key, item, JSONUtil.toJsonStr(companyHolidaysConfigs), CacheConsts.ONE_YEAR);
            }
            return companyHolidaysConfigs;
        } catch (Exception e) {
            log.error("selectCompanyHolidaysConfig ===> 查询异常", e.toString());
        }
        return null;
    }

    /**
     * 查询企业指定日期是否为节假日
     *
     * @param companyId
     * @param date
     * @return
     */
    @Override
    public boolean isHoliday(Long companyId, Date date) {
        try {
            CompanyHolidaysConfig qurey = new CompanyHolidaysConfig();
            qurey.setCompanyId(companyId);
            qurey.setYear(DateFormatUtils.format(date, "yyyy"));
            List<CompanyHolidaysConfig> companyHolidaysConfigs = this.selectCompanyHolidaysConfig(qurey);
            List<CompanyHolidaysConfig> collect = companyHolidaysConfigs.stream()
                    .filter(o -> DateUtils.isSameDay(date, o.getDate())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                return true;
            }
        } catch (Exception e) {
            log.error("isHoliday ===> 查询异常", e.toString());
        }
        return false;
    }

    /**
     * 查询企业人工标注点列表（b类点）
     *
     * @param companyId
     * @return
     */
    @Override
    public List<CompanyLocations> getCompanyRecommendedLocationByCompanyId(Long companyId) {
        List<CompanyLocations> companyLocations = companyLocationsMapper.selectByCompanyId(companyId);
        return companyLocations;
    }

    /**
     * 查询企业 自动重新派单配置
     *
     * @param companyId
     * @return
     */
    @Override
    public CompanyRePlaceConfigVo getCompanyRePlaceConfig(Long companyId) {
        CompanyRePlaceConfigVo config = new CompanyRePlaceConfigVo();
        config.setCompanyId(11L);
        config.setEnable(true);
        config.setCloseTime(1);
        return config;
    }

    /**
     * 查询企业 重新派车自动升舱配置
     *
     * @param companyId
     * @return
     */
    @Override
    public CompanyRePlaceUpgradeConfigVo getCompanyRePlaceUpgradeConfig(Long companyId) {
        CompanyRePlaceUpgradeConfigVo config = new CompanyRePlaceUpgradeConfigVo();
        config.setCompanyId(11L);
        config.setEnable(true);
        config.setMoney(1);
        return config;
    }

}