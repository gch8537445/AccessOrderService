package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class SendMailDto {
    
	private String toEmail;
	private String title;
	private String bodyInfo;
	private Long companyId;
}
