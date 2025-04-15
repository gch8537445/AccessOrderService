package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.ActivityLog;

public interface ActivityLogMapper {
    int insertSelective(ActivityLog record);

    int updateOrderId();
}