package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderChangeDestLog;

public interface OrderChangeDestLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderChangeDestLog record);

    int insertSelective(OrderChangeDestLog record);

    OrderChangeDestLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderChangeDestLog record);

    int updateByPrimaryKey(OrderChangeDestLog record);

    // 以下手工添加
    OrderChangeDestLog selectByOrderId(Long orderId);
}