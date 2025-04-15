package com.ipath.orderflowservice.core.map.bean;

import lombok.Data;

/**
 * 获取附近地址 请求结果 bean
 *
 */
@Data
public class SearchRes {
    /**
     * id
     */
    private String id;
    /**
     * 地址名称
     */
    private String title;
    /**
     * 点位
     */
    private Point location;
    /**
     * 距离
     */
    private Integer distance;

}
