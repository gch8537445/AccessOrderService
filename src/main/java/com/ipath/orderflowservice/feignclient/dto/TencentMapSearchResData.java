package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class TencentMapSearchResData {

    private String id;

    private String title;

    private String address;

    private String tel;

    private String category;

    private TencentMapSearchResDataLocation location;

    private TencentMapSearchResDataAdInfo ad_info;

    private int _distance;
}
