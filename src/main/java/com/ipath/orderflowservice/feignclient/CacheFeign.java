package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "hcgl-service-cache")
public interface CacheFeign {

    @RequestMapping(value = "/hcgl/cache/get", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getCache(@RequestBody RequestCacheDto requestCacheDto);

    @RequestMapping(value = "/hcgl/cache/save", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse saveCache(@RequestBody RequestCacheDto requestCacheDto);

    @RequestMapping(value = "/hcgl/cache/delete", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse deleteCache(@RequestBody RequestCacheDto requestCacheDto);
}
