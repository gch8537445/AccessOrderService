package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 推荐上车地点 详情 bean Vo
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class RecommendedLocationInfoVo {

    private String id;

    private String title;
    /**
     * 经纬度
     */
    private RecommendedLocationInfoLocationVo location;
    /**
     * 实际距离
     */
    private Integer distance;
    /**
     * 直线距离
     */
    private Integer line_distance;
    /**
     * 下单次数
     */
    private Integer score;

    private Integer park_time;

    private String adcode;

    private String ext;
    /**
     * 点位来源：
     *    A类点：A
     *    B类点：B
     *    C类点：C
     */
    private String source;

    /**
     * 是否吸附
     *  是：true
     *  否：false
     */
    private boolean adsorb;

}
