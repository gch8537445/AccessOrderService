package com.ipath.orderflowservice.order.business.dispatch.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDispatchConfigVo {
    /**
     * 提交选项
     * 0:我再等等  1：加价调度
     */
    private String submitType;

    //弹出弹窗时间(秒)
    private Long popupTime;
    //弹出弹窗剩余时间(秒)
    private Long residuePopupTime;
    //弹窗读秒时间(秒)
    private Long outTime;
    //弹窗读秒剩余时间(秒)
    private Long residueOutTime;
    //调度费金额(元)
    private BigDecimal dispatchFeeAmount;

    //参与极速派单车型 运力id_车型等级
    private List<String> carList;

    private String cityCode;

    private double mileageStart;
    private double mileageEnd;

    private Long delayTime;

    private String timeContextStart;
    private String timeContextEnd;





}
