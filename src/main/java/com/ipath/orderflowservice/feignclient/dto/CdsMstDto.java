package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONObject;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.EstimatePriceResult;
import lombok.Data;

import java.util.List;


@Data
public class CdsMstDto {

    private Long orderId;

    private CreateOrderParam orderParam;

    private RequestCheckOrderDto checkOrderParam;

    private RequestEstimateDto requestEstimateDto;

    private List<EstimatePriceResult> estimatePriceResultList;

    private String customer;
    private Boolean autoPay;

}
