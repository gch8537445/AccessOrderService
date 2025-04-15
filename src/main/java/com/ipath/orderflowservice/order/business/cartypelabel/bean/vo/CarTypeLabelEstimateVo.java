package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import com.ipath.orderflowservice.order.bean.vo.EstimateCar;
import lombok.Data;

import java.util.List;

@Data
public class CarTypeLabelEstimateVo {
    private Long id;
    private String labelCode;
    private String labelName;
    private String labelDesc;
    private String priceLab;        // 车型估价区间
    private boolean checkAll;    // 是否选中
    private List<EstimateCar> list; // 车型所含运力平台估价详情数组,数组应使用estimatePrice正向排序

}
