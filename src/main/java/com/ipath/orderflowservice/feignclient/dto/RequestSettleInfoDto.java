package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestSettleInfoDto {
    private List<String> orderidList;
}
