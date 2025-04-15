package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanyCallbackConfig;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitMappingVo;

import java.util.List;

public interface CompanyCallbackConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CompanyCallbackConfig record);

    int insertSelective(CompanyCallbackConfig record);

    CompanyCallbackConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompanyCallbackConfig record);

    int updateByPrimaryKey(CompanyCallbackConfig record);

    //以下手工添加
    List<CompanyCallbackConfig> selectCompanyCallbackConfigMappingByCompanyId(Long companyId);
}