package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderPersonalPayment;

import java.util.List;

public interface OrderPersonalPaymentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderPersonalPayment record);

    int insertSelective(OrderPersonalPayment record);

    OrderPersonalPayment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderPersonalPayment record);

    int updateByPrimaryKey(OrderPersonalPayment record);

    //以下手工添加
    OrderPersonalPayment selectByOrderId(Long id);
    List<OrderPersonalPayment> selectByOrderIds(List<Long> orderId);
}