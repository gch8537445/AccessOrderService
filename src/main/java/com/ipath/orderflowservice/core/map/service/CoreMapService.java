package com.ipath.orderflowservice.core.map.service;


import com.ipath.orderflowservice.core.map.bean.SearchRes;

import java.util.List;

/**
 * core端地图服务 service
 */
public interface CoreMapService {

    /**获取附近地址列表*/
    List<SearchRes> search(String lng, String lat,Long companyId);

    /**获取最大估计距离*/
    Integer getdistance(String originsLng, String originsLat, String destinationLng, String destinationLat);

    /**根据经纬度判断是否交通枢纽*/
    boolean istraffichub(String lng, String lat,Long companyId);
}
