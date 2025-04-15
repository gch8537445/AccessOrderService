package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

/**
 * 推荐上车地点 详情 经纬度 bean Vo
 */
@Data
public class RecommendedLocationInfoLocationVo {

    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;

}
