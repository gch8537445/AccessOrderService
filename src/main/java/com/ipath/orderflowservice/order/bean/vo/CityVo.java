package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CityVo implements Serializable {
    private String cityCode;// 城市代码(对应CityID)
    private String cityName;// 城市名称
    private String cityPinYin;// 拼音
    private String cityGroupName;// 声母分组名
    private String cityDistrict; // 座机区号
}
