package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONArray;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;

import java.util.List;

@Data
public class RequestAppendPlaceOrderDto {
    private Long coreOrderId;
    private String estimateId;
    private String destPoi;
    private String departPoi;
    private List<SelectedCar> cars;
    private JSONArray passingPoints;
}
