package com.ipath.orderflowservice.core.mdm.service;



import com.ipath.common.bean.BaseResponse;

import java.util.Map;


public interface PoiService {

    BaseResponse getStationPoiList();

    Map<Object, Object> reOrderRedisStationPoi();

    String getStationTypeByTpoiId(String tPoiId);
}
