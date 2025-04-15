package com.ipath.orderflowservice.order.dao;

import java.util.Map;

import com.ipath.orderflowservice.order.dao.bean.UserBusinessTrip;

public interface UserBusinessTripMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBusinessTrip record);

    int insertSelective(UserBusinessTrip record);

    UserBusinessTrip selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBusinessTrip record);

    int updateByPrimaryKey(UserBusinessTrip record);

    int selectCnt(Map<String,Object> map);
}