package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;

@Data
public class SendMsgDto {
    private String content;
    private List<String> userIds;
    private Long companyId;

    // 腾讯推文添加参数
    private String templateID;

    private String url;

    private List<String> keywords;
}
