package com.ipath.orderflowservice.core.tencent.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrafficInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;

}
