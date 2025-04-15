package com.ipath.orderflowservice.order.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description: 推荐上车点配置
 * @author: qy
 * @create: 2024-07-08 10:39
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendedLocationConfigVo {

    /**
     * 类型:
     * 1. 首页定位；
     * 2. 地图上拖动移位；
     * 3. 选择历史地址；
     * 4. 选择检索的地址
     */
    private int type;

    /**
     * 选用的逻辑
     */
    private int logic;

    /**
     * 筛选半径
     */
    private int radius;

    /**
     * 吸附距离
     */
    private Integer distance;

    /**
     * 吸附次数 (type = 2,使用)
     */
    private Integer count;

}