package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.ComScenePara;

import java.util.List;

public interface ComSceneParaMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ComScenePara record);

    int insertSelective(ComScenePara record);

    ComScenePara selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ComScenePara record);

    int updateByPrimaryKey(ComScenePara record);

    List<ComScenePara> selectByParaCodes(List<String> paraCodes);
}