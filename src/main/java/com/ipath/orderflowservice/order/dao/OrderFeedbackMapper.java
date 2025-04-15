package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderFeedback;

public interface OrderFeedbackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderFeedback record);

    int insertSelective(OrderFeedback record);

    OrderFeedback selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderFeedback record);

    int updateByPrimaryKey(OrderFeedback record);

    // 以下手工添加
    OrderFeedback selectByOrderId(Long orderId);
}