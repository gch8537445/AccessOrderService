package com.ipath.orderflowservice.feignclient.dto;


import lombok.Data;

import java.util.List;

@Data
public class RequestChangeCouponState {
    private List<Long> couponIds;
    private Integer couponState;
}
