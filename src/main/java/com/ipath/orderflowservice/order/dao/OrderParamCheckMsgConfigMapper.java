package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderParamCheckMsgConfig;

public interface OrderParamCheckMsgConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderParamCheckMsgConfig record);

    int insertSelective(OrderParamCheckMsgConfig record);

    OrderParamCheckMsgConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderParamCheckMsgConfig record);

    int updateByPrimaryKey(OrderParamCheckMsgConfig record);

    OrderParamCheckMsgConfig selectByCompanyId(Long companyId);
}