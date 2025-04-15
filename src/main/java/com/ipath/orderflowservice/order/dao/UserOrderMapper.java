package com.ipath.orderflowservice.order.dao;


import com.ipath.orderflowservice.order.dao.bean.UserOrder;

public interface UserOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    // 以下手工添加
    UserOrder selectByUserId(Long userId);
}