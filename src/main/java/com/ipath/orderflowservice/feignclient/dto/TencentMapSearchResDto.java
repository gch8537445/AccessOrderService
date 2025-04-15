package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;

@Data
public class TencentMapSearchResDto {

    private int status;

    private String message;

    private int count;

    private String request_id;

    private List<TencentMapSearchResData> data ;
}
