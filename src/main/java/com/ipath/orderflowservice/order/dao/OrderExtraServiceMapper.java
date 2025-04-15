package com.ipath.orderflowservice.order.dao;

import java.math.BigDecimal;
import java.util.List;

import com.ipath.orderflowservice.order.dao.bean.OrderExtraService;

public interface OrderExtraServiceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderExtraService record);

    int insertSelective(OrderExtraService record);

    OrderExtraService selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderExtraService record);

    int updateByPrimaryKey(OrderExtraService record);

    // 以下手工添加
    BigDecimal selectAmountSumByOrderId(Long orderId);
    List<OrderExtraService> selectByOrderId(Long orderId);
}