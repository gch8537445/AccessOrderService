package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.ComScene;
import com.ipath.orderflowservice.order.dao.vo.SceneParaVo;

import java.util.List;
import java.util.Map;

public interface ComSceneMapper {
    ComScene selectByPrimaryKey(Long id);

    SceneParaVo selectSceneAmountByPublishId(Long publishId);

    int updateByPrimaryKeys(List<Long> ids);

    int selectCnt(Map<String,Object> map);

    List<ComScene> selectListByCompanyId(ComScene comScene);
}