package com.ipath.orderflowservice.core.track.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.core.track.bean.dto.LastDriverPositionDto;
import com.ipath.orderflowservice.core.track.constant.TrackPathConstant;
import com.ipath.orderflowservice.core.track.service.TrackService;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.ka.remoteCall.service.RemoteCallService;
import com.ipath.orderflowservice.order.bean.vo.DriverPosition;
import com.ipath.orderflowservice.order.bean.vo.DriverPositionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrackServiceImpl implements TrackService {

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    @Autowired
    private RemoteCallService remoteCallService;

    @Override
    public List<DriverPositionVo> selectDriverPosition(DriverPosition driverPosition) throws Exception{
        JSONObject paramObject = new JSONObject();
        paramObject.set("coreOrderId",driverPosition.getCoreOrderId());
        paramObject.set("start",driverPosition.getStart());
        BaseResponse response = remoteCallService.call(paramObject.toString(), TrackPathConstant.TRACK_PATH_SELECTDRIVERPOSITION);
        Object data = response.getData();
        List<DriverPositionVo> driverPositionVos = JSONUtil.toList(JSONUtil.toJsonStr(data) , DriverPositionVo.class);
        return  driverPositionVos;
    }

    @Override
    public LastDriverPositionDto selectLastDriverPositionByJson(String coreOrderId) throws Exception {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.set("coreOrderId",coreOrderId);
            BaseResponse response = remoteCallService.call(paramObject.toString(), TrackPathConstant.TRACK_PATH_SELECTDRIVERLASTPOSITION);

            if(response.getCode() == 0){
                LastDriverPositionDto lastDriverPositionDto = BeanUtil.copyProperties(response.getData(), LastDriverPositionDto.class);
                return  lastDriverPositionDto;
            }

        } catch (Exception e) {
            log.error("selectLastDriverPositionByJson <===> 异常:{}", e);
        }
      return null;
    }

}