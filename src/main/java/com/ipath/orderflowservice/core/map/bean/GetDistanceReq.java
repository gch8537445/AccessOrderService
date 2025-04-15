package com.ipath.orderflowservice.core.map.bean;

import lombok.Data;

/**
 * 获取最大估计距离 请求参数 bean
 *
 */
@Data
public class GetDistanceReq {
    /**
     * 起点经纬度
     */
    private String origins;
    /**
     * 终点经纬度
     */
    private String destination;


    public GetDistanceReq() {

    }

    public GetDistanceReq(String origins, String destination) {
        this.origins = origins;
        this.destination = destination;
    }
}
