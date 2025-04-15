package com.ipath.orderflowservice.feignclient.constants;

/**
 * RemoteCallFeign配置
 */
public class RemoteCallFeignConstant {

    /**
     * 地址服务 map-service 接口路径
     */
    //获取附近地址
    public static final String MAP_SEARCH = "/api/v1/map/search";
    //获取最大估计距离
    public static final String MAP_INNER_GETDISTANCE = "/api/v1/map/inner/getdistance";

}
