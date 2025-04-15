package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanyUserConfig;

import java.util.List;

public interface CompanyUserConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CompanyUserConfig record);

    int insertSelective(CompanyUserConfig record);

    CompanyUserConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanyUserConfig record);
    int insertSelectiveJsonP(CompanyUserConfig record);
    int updateByPrimaryKeySelectiveJsonP(CompanyUserConfig record);
    int updateByPrimaryKey(CompanyUserConfig record);

    CompanyUserConfig selectOne(CompanyUserConfig record);

    List<CompanyUserConfig> selectList(CompanyUserConfig record);
}