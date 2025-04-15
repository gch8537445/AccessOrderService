package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CityMapping;

import java.util.List;

public interface CityMappingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CityMapping record);

    int insertSelective(CityMapping record);

    CityMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CityMapping record);

    int updateByPrimaryKey(CityMapping record);

    List<CityMapping> selectByCompanyId(Long companyId);
}