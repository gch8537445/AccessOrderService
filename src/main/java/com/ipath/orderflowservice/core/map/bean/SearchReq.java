package com.ipath.orderflowservice.core.map.bean;

import lombok.Data;


/**
 * 获取附近地址 请求参数 bean
 *
 */
@Data
public class SearchReq {
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 经度
     */
    private String Lng;
    /**
     * 纬度
     */
    private String Lat;

    /**
     * 用户id
     */
    private String UserId;

    /**
     * 用户id
     */
    private String traceId;

    public SearchReq() {
    }

    public SearchReq(String lng, String lat, String userId , String traceId, Long companyId) {
        this.Lng = lng;
        this.Lat = lat;
        this.UserId = userId;
        this.traceId = traceId;
        this.companyId = companyId;
    }
}
