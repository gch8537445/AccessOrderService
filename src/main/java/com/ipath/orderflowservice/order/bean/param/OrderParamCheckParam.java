package com.ipath.orderflowservice.order.bean.param;


import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class OrderParamCheckParam implements Serializable {

    //校验条目
    private List<String> checkItems;
    private Long userId;
    private Long companyId;
    //场景id
    private Long sceneId;
    //出发地城市代码
    private String departCityCode;
    //出发地城市名称
    private String departCityName;
    //出发地纬度
    private String departLat;
    //出发地经度
    private String departLng;
    //目的地城市代码
    private String destCityCode;
    //目的地城市名称
    private String destCityName;
    //目的地纬度
    private String destLat;
    //目的地经度
    private String destLng;


}
