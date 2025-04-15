package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.UserCoupon;

public interface UserCouponMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCoupon record);

    int insertSelective(UserCoupon record);

    UserCoupon selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCoupon record);

    int updateByPrimaryKey(UserCoupon record);

    int selectAvailableCouponCount(Long userId);
}