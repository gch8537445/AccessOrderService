package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class SendSmsDto {
    private String phoneNum;//手机号码
    private String templateId;//模板Id
    private String paramsArray;//参数，多个用‘|’分割
    private Long companyId;
}
