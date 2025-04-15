package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "remote-call-service")
public interface RemoteCallFeign {
    
    /**
     * 调用core接口
     */
    @RequestMapping(value = "/remotecall/call", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse call(@RequestBody RemoteCallDto remoteCallDto);

    @RequestMapping(value = "/remotecall/callchannel", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse callChannel(@RequestBody RemoteCallDto remoteCallDto);

    @RequestMapping(value = "/remotecall/callByForm", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse callByForm(@RequestBody RemoteCallDto remoteCallDto);

    @RequestMapping(value = "/remotecall/callChannelDataStr", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse callChannelDataStr(@RequestBody RemoteCallDto remoteCallDto);

}
