package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderAirportPickup;

public interface OrderAirportPickupMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderAirportPickup record);

    int insertSelective(OrderAirportPickup record);

    OrderAirportPickup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderAirportPickup record);

    int updateByPrimaryKey(OrderAirportPickup record);
}