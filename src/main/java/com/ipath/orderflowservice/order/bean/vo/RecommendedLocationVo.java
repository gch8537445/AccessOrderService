package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.util.List;

/**
 * 推荐上车地点 bean Vo
 */
@Data
public class RecommendedLocationVo {

    /**
     * 推荐地址信息
     */
    List<RecommendedLocationInfoVo> recommendedLocationInfos;

}
