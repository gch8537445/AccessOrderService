package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.OrderSource;
import feign.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderSourceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderSource record);

    int insertSelective(OrderSource record);

    OrderSource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderSource record);

    int updateByPrimaryKey(OrderSource record);

    // 以下手工添加
    OrderSource selectByOrderId(Long orderId);
    OrderSource selectLatestByOrderId(Long orderId);
    OrderSource selectByICarDcOrderId(String coreOrderId);
    List<OrderSource> selectListByOrderId(@Param("list") List<Long> list);


    int rePlaceUpdateStateByOrderId(OrderSource record);


}