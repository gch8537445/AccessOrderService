package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "configuration-service")
public interface ConfigurationFeign {

    /**
     * 查询场景列表
     */
    @RequestMapping(value = "/api/v2/configuration/scene/getSceneList", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getSceneList(@RequestBody RequestSceneDto requestSceneDto);

    /**
     * 查询场景详情
     * 返回
     * { data: {
     *   scene: {
     *    availableAmount
     *    maxAmountPerOrder
     *   },
     *   reimModel: [
     *    { carLevel    车型
     *      reimModel   1:按金额 2:按比例
     *      value       报销比例或金额
     *    } ]
     *  } }
     */
    @RequestMapping(value = "/api/v2/configuration/scene/getSceneInfo", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getSceneInfo(@RequestBody RequestSceneInfoDto requestSceneInfoDto);

    @RequestMapping(value = "/api/v2/configuration/scene/getSceneInfo", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getSceneBase(@RequestBody RequestSceneInfoDto requestSceneInfoDto);

    /**
     * 预估前下单条件判断
     */
    @RequestMapping(value = "/api/v2/configuration/Common/CheckOrder", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse checkOrder(@RequestBody RequestCheckOrderDto requestCheckOrderDto);

}
