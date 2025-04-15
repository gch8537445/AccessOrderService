package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.util.List;

import com.ipath.orderflowservice.feignclient.dto.ReimModel;

@Data
public class CacheEstimateResult {
    private Integer id;             // 车型代码
    private String nameCn;          // 车型中文名
    private String nameEn;          // 车型英文名
    private String reimbursementCn; // 混合支付策略中文描述
    private String reimbursementEn; // 混合支付策略英文描述
    private String priceLab;        // 车型估价区间
    private List<EstimateCar> list; // 车型所含运力平台估价详情数组,数组应使用estimatePrice正向排序
    private ReimModel reimModel;    // 报销策略
    private List<KeyValueVo> deductionCn;
    private Boolean checkAll;


    //自定义
    private Boolean selfPayingUpgradeCarType;
    private String selfPayingRate;
    private Long selfPayingAmount;



}
