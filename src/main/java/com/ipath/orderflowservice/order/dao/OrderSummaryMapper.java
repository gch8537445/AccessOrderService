package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.bean.param.StatementOfAccountParam;
import com.ipath.orderflowservice.order.dao.bean.OrderSummary;
import org.apache.ibatis.annotations.Param;

public interface OrderSummaryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderSummary record);

    int insertSelective(OrderSummary record);

    OrderSummary selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderSummary record);

    int updateByPrimaryKey(OrderSummary record);

}