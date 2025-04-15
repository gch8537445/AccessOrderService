package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.TempHistoryAddress;

import java.util.List;

public interface TempHistoryAddressMapper {
    int insert(TempHistoryAddress record);

    int insertSelective(TempHistoryAddress record);

    List<TempHistoryAddress> selectAll();
}