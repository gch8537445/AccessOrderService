package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderComplaint;

public interface OrderComplaintMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderComplaint record);

    int insertSelective(OrderComplaint record);

    OrderComplaint selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderComplaint record);

    int updateByPrimaryKey(OrderComplaint record);

    // 以下手工添加
    OrderComplaint selectByOrderId(Long orderId);

    int selectCntByOrderId(Long orderId);
}