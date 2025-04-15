package com.ipath.orderflowservice.order.bean.vo;

import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarTypeLabelEstimateVo;
import lombok.Data;

import java.util.Set;

@Data
public class CacheCarTypeLabelEstimateResult {
    private String dynamicCode;             // 车型代码
    private String nameCn;          // 车型中文名
    private String nameEn;          // 车型英文名
    private Set<CarTypeLabelEstimateVo> data; // 车型所含运力平台估价详情数组,数组应使用estimatePrice正向排序

}
