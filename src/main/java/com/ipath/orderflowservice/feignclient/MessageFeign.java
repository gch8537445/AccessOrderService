package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "message-service")
public interface MessageFeign {
    
    /**
     * 发送短信
     */
    @RequestMapping(value = "/api/v2/message/SendBySms", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse sendSms(@RequestBody SendSmsDto sendSmsDto);

    /**
     * 发送应用内消息
     */
    @RequestMapping(value = "/api/v2/message/SendByApp", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse sendMsg(@RequestBody SendMsgDto sendMsgDto);


    /**
     * 发送email
     */
    @RequestMapping(value = "/api/v2/message/SendByEmail", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse sendByEmail(@RequestBody SendMailDto sendMailDto);
}
