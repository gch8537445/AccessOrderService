package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class RequestPublishMessageDto {
    /**
     * 消息id
     */
    private String messageId;
    /**
     * 公司id
     */
    private Long companyId ;
    /**
     * 消息 Topic
     */
    private String topic;
    /**
     * 消息 Tag
     */
    private String tag ;
    /**
     * 消息 Key
     */
    private String key ;
    /**
     * 消息体
     */
    private String messageBody ;
}
