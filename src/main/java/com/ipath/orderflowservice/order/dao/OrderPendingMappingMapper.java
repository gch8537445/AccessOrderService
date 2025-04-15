package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderPendingMapping;

import java.util.List;

public interface OrderPendingMappingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderPendingMapping record);

    int insertSelective(OrderPendingMapping record);

    OrderPendingMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderPendingMapping record);

    int updateByPrimaryKey(OrderPendingMapping record);

    List<OrderPendingMapping> selectByUserId(Long orderId);
}