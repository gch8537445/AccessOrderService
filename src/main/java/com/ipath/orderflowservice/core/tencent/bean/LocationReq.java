package com.ipath.orderflowservice.core.tencent.bean;

import lombok.Data;


@Data
public class LocationReq {
    private String location;
    private String getPoI = "0";
    private String poioptions;
    private String outPut;
    private String callBack;
}
