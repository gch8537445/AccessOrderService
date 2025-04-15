package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.ComSceneUsed;

import java.util.List;

public interface ComSceneUsedMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ComSceneUsed record);

    int insertSelective(ComSceneUsed record);

    ComSceneUsed selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ComSceneUsed record);

    int updateByPrimaryKey(ComSceneUsed record);

    List<ComSceneUsed> selectSceneUsedBySceneId(Long sceneId);
    int updateByPrimaryKeys(List<Long> sceneIds);
}