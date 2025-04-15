package com.ipath.orderflowservice.order.bean.param.ordercore;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.feignclient.dto.RequestPlaceOrderExtraParamsDto;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 重新下单接口 请求参数
 */
@Data
public class RePlaceOrderReq {
    /**
     * 中台子订单的订单号
     **/
    private String coreOrderId;
    /**
     * 预估主键
     **/
    private String estimateId;
    /**
     * 用车时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date departTime;

    private String destPoi;

    private String departPoi;

    /**
     * 选择的车型数组
     */
    private List<SelectedCar> cars;

    private List<RequestPlaceOrderExtraParamsDto> extraParams;


}