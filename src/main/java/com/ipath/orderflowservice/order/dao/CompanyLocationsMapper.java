package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanyLocations;

import java.util.List;

public interface CompanyLocationsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CompanyLocations record);

    int insertSelective(CompanyLocations record);

    CompanyLocations selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanyLocations record);

    int updateByPrimaryKey(CompanyLocations record);

    List<CompanyLocations> selectByCompanyId(Long companyId);
}