package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;

import java.util.List;

public interface OrderLimitConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderLimitConfig record);

    int insertSelective(OrderLimitConfig record);

    OrderLimitConfig selectByPrimaryKey(Long id);

    OrderLimitConfig selectOne(OrderLimitConfig record);

    List<OrderLimitConfig> selectList(OrderLimitConfig record);

    int updateByPrimaryKeySelective(OrderLimitConfig record);

    int updateByPrimaryKey(OrderLimitConfig record);
}