package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.UserBase;

import java.util.List;

public interface UserBaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBase record);

    int insertSelective(UserBase record);

    UserBase selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBase record);

    int updateByPrimaryKey(UserBase record);

    UserBase selectBySocialAppUserId(String soicalAppUserId);

    List<UserBase> selectByUserIds(List<Long> userIdList);

    List<UserBase> selectByStaffCodes(List<String> staffCodeList);

    int updateByPrimaryKeys(List<Long> companyIds);
}