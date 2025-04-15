package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanySetting;

import java.util.List;

public interface CompanySettingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CompanySetting record);

    int insertSelective(CompanySetting record);

    CompanySetting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanySetting record);

    int updateByPrimaryKey(CompanySetting record);

    List<CompanySetting> selectCompanySettingByName(String name);

    int updateByCompanyIds(List<Long> companyIds);
}