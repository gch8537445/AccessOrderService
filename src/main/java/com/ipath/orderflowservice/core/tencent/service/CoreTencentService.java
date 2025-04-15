package com.ipath.orderflowservice.core.tencent.service;


import com.ipath.orderflowservice.core.tencent.bean.LocationResResult;
import com.ipath.orderflowservice.core.tencent.bean.TrafficInfo;

/**
 * core端腾讯服务 service
 */
public interface CoreTencentService {

    /**
     * 点位半径范围内是否存在交通枢纽
     * @param lat
     * @param lng
     * @param r
     * @return
     */
    boolean hasTrafficHub(String lat, String lng, int r);

    /**
     * 逆解析
     * @param location
     * @return
     */
    LocationResResult location(String location) throws Exception;

    /**
     * 获取交通枢纽信息（通过坐标点）
     * @param lat
     * @param lng
     * @return
     * @throws Exception
     */
    TrafficInfo getTrafficInfoByPoint(String lat, String lng) throws Exception;
}
