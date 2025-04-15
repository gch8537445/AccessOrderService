package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RemoteCallDto {
    private String appId; // 要调用的目的方，不传表示请求core接口
    private String path;  // 请求路径
    private String content; // 请求参数，json字符串
    private Map<String, Object> mapParam; // 请求参数，map
    private Map<String,  List<String>> headers; // hearder请求参数

    public RemoteCallDto() {
    }

    public RemoteCallDto(String path, String content) {
        this.path = path;
        this.content = content;
    }
}
