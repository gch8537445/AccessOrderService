package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CostCenter;

public interface CostCenterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CostCenter record);

    int insertSelective(CostCenter record);

    CostCenter selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CostCenter record);

    int updateByPrimaryKey(CostCenter record);
}