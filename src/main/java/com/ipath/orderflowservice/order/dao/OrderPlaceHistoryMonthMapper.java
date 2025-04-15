package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.bean.OrderPlaceHistoryMonthCount;
import com.ipath.orderflowservice.order.dao.bean.OrderPlaceHistoryMonth;
import feign.Param;

import java.util.List;

public interface OrderPlaceHistoryMonthMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderPlaceHistoryMonth record);

    int insertSelective(OrderPlaceHistoryMonth record);

    OrderPlaceHistoryMonth selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderPlaceHistoryMonth record);

    int updateByPrimaryKey(OrderPlaceHistoryMonth record);

    List<OrderPlaceHistoryMonth> selectByuserId(Long id);

    /**
     * 根据用户id获取地址记录 按 pickup_location_name 分组 取起点坐标
     * @param userId
     * @return
     */
    List<OrderPlaceHistoryMonthCount> selectPickupCountInfoByuserId(@Param("userId") Long userId);

    int insertOrder();

    int deleteHistoryOrder();

    /**
     * 根据用户id获取地址记录 按 dest_location_name 分组 取终点坐标
     * @param userId
     * @return
     */
    List<OrderPlaceHistoryMonthCount> selectDestCountInfoByuserId(Long userId);


}