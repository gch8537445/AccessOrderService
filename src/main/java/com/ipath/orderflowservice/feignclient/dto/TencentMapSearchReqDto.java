package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class TencentMapSearchReqDto {

    private String key;
    private String keyword;
    private String boundary;
    private String getSubpois;
    private String filter;
    private String addedFields;
    private String orderby;
    private String pageSize;
    private String pageIndex;
    private String output;
    private String callback;
}
