package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "cds-service-meituan")
public interface CdsMstFeign {
    /**
     * 可用车型
     */
    @RequestMapping(value = "ipathCall/regulation/filterCarType", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse filterCarType(@RequestBody CdsMstDto cdsMstDto);
    /**
     * 提单 (管控校验)
     */
    @RequestMapping(value = "ipathCall/regulation/createOrder", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse createOrder(@RequestBody CdsMstDto cdsMstDto);
    /**
     * 获取用户自费金额及管控费用明细
     */
    @RequestMapping(value = "ipathCall/regulation/getPayDetail", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getPayDetail(@RequestBody CdsMstDto cdsMstDto);
    /**
     * 订单支付
     */
    @RequestMapping(value = "ipathCall/ordePay", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse ordePay(@RequestBody CdsMstDto cdsMstDto);

    @RequestMapping(value = "ipathCall/ordeQuery", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse ordeQuery(@RequestBody CdsMstDto cdsMstDto);


}
