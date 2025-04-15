package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.AmgenPoScene;

public interface AmgenPoSceneMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AmgenPoScene record);

    int insertSelective(AmgenPoScene record);

    AmgenPoScene selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AmgenPoScene record);

    int updateByPrimaryKey(AmgenPoScene record);

    String selectPoAndCostCenterBySceneId(Long sceneId);
}