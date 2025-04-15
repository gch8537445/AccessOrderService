package com.ipath.orderflowservice.core.track.service;


import com.ipath.orderflowservice.core.track.bean.dto.LastDriverPositionDto;
import com.ipath.orderflowservice.order.bean.vo.DriverPosition;
import com.ipath.orderflowservice.order.bean.vo.DriverPositionVo;

import java.util.List;

public interface TrackService {

    /**
     * 获取司机点位
     * @param driverPosition
     * @return
     * @throws Exception
     */
    List<DriverPositionVo> selectDriverPosition(DriverPosition driverPosition) throws Exception;
    LastDriverPositionDto selectLastDriverPositionByJson(String coreOrderId) throws Exception;
}
