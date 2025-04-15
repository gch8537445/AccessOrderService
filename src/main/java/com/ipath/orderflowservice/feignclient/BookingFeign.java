package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestGetUserBaseInfoDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSetUserMobileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "booking-service")
public interface BookingFeign {


    /**
     * 预约管家开始服务
     */
    @RequestMapping(value = "/api/v2/booking/StartService", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse startService(@RequestBody RequestSetUserMobileDto requestSetUserMobileDto);

    /**
     * 预约管家取消服务
     */
    @RequestMapping(value = "/api/v2/booking/CancleService", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse cancelService(@RequestBody RequestSetUserMobileDto requestSetUserMobileDto);

    /**
     * 预约关键回调接口
     */
    @RequestMapping(value = "/api/v2/booking/CallBack", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse callBack(@RequestBody RequestGetUserBaseInfoDto requestGetUserBaseInfoDto);

}
