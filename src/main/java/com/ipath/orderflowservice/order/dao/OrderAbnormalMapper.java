package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderAbnormal;

public interface OrderAbnormalMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderAbnormal record);

    int insertSelective(OrderAbnormal record);

    OrderAbnormal selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderAbnormal record);

    int updateByPrimaryKey(OrderAbnormal record);
}