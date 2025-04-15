package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestCheckOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestGetUserBaseInfoDto;
import com.ipath.orderflowservice.feignclient.dto.SendSmsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "order-validation-service")
public interface OrderValidationFeign {

    /**
     * 预估前下单条件判断
     */
    @RequestMapping(value = "/orderValidation/check", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse checkOrder(@RequestBody RequestCheckOrderDto requestCheckOrderDto);
}
