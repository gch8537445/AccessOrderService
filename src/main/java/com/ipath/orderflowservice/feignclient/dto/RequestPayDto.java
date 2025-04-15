package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.PayOrder;
import com.sun.el.parser.BooleanNode;
import lombok.Data;

import java.util.List;

@Data
public class RequestPayDto {
    private Long companyId;
    private Long userId;
    private List<PayOrder> orders;
    private String goodsName;
    private String goodsDetail;
    private String miniAppid;
    private String openId;
    //private Boolean isMiniApp;
    //新增字段
    private Integer paymentType;
    private String redirectUri;
}
