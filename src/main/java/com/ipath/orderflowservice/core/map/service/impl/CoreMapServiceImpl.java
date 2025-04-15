package com.ipath.orderflowservice.core.map.service.impl;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.core.map.bean.GetDistanceReq;
import com.ipath.orderflowservice.core.map.bean.GetDistanceRes;
import com.ipath.orderflowservice.core.map.bean.SearchReq;
import com.ipath.orderflowservice.core.map.bean.SearchRes;
import com.ipath.orderflowservice.core.map.service.CoreMapService;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.constants.RemoteCallFeignConstant;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * core端地图服务 serviceImpl
 */
@Service
public class CoreMapServiceImpl implements CoreMapService {

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    /**
     * 获取附近地址列表
     *
     * @param lng
     * @param lat
     * @return
     */
    @Override
    public List<SearchRes> search(String lng, String lat, Long companyId) {
        SearchReq searchReq = new SearchReq(lng, lat, "order-service", UUID.randomUUID().toString(), companyId);
        RemoteCallDto remoteCallDto = new RemoteCallDto(
                RemoteCallFeignConstant.MAP_SEARCH,
                JSONUtil.toJsonStr(searchReq));
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() == 0) {
            List<SearchRes> searchRes = JSONUtil.toList(JSONUtil.toJsonStr(response.getData()), SearchRes.class);
            return searchRes;
        }
        return null;
    }

    /**
     * 获取最大估计距离
     *
     * @param originsLng
     * @param originsLat
     * @param destinationLng
     * @param destinationLat
     * @return
     */
    @Override
    public Integer getdistance(String originsLng, String originsLat, String destinationLng, String destinationLat) {
        Integer result = null;
        RemoteCallDto remoteCallDto = new RemoteCallDto(
                RemoteCallFeignConstant.MAP_INNER_GETDISTANCE,
                JSONUtil.toJsonStr(new GetDistanceReq(
                        originsLng + "," + originsLat,
                        destinationLng + "," + destinationLat)));
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() == 0) {
            GetDistanceRes res = JSONUtil.toBean(JSONUtil.toJsonStr(response.getData()), GetDistanceRes.class);
            if (null != res && StringUtils.isNotBlank(res.getDistance())) {
                result = Integer.valueOf(res.getDistance());
            }
        }
        return result;
    }

    /**
     * 根据经纬度判断是否交通枢纽
     *
     * @param lng
     * @param lat
     * @return
     */
    @Override
    public boolean istraffichub(String lng, String lat, Long companyId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("lat", lat);
        jsonObject.set("lng", lng);
        jsonObject.set("companyId", companyId);

        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setContent(jsonObject.toString());
        remoteCallDto.setPath("/api/v1/map/istraffichub");
        BaseResponse baseResponse = remoteCallFeign.call(remoteCallDto);
        if (baseResponse.getCode() == 0) {
            JSONObject resultObject = new JSONObject(baseResponse.getData());
            return resultObject.getInt("isTrafficHub") == 1;
        }
        return false;
    }
}
