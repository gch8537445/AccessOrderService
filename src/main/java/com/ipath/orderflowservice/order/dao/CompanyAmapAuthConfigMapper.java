package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanyAmapAuthConfig;

import java.util.List;

public interface CompanyAmapAuthConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CompanyAmapAuthConfig record);

    int insertSelective(CompanyAmapAuthConfig record);

    CompanyAmapAuthConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanyAmapAuthConfig record);

    int updateByPrimaryKey(CompanyAmapAuthConfig record);

    CompanyAmapAuthConfig selectByCompanyAuthId(String companyAuthId);

    List<CompanyAmapAuthConfig> selectAll();
}