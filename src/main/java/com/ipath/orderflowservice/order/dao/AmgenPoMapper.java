package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.AmgenPo;

public interface AmgenPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AmgenPo record);

    int insertSelective(AmgenPo record);

    AmgenPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AmgenPo record);

    int updateByPrimaryKey(AmgenPo record);
}