package com.ipath.orderflowservice.core.tencent.controller;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.core.tencent.bean.TrafficInfo;
import com.ipath.orderflowservice.core.tencent.service.CoreTencentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "core端腾讯服务")
@RestController
@RequestMapping("/order/core/tencent")
public class CoreTencentController {

    @Autowired
    private CoreTencentService coreTencentService;


    /**
     *
     * @param lat
     * @param lng
     * @param r  米
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "点位半径范围内是否存在交通枢纽")
    @GetMapping("/hasTrafficHub")
    public BaseResponse hasTrafficHub(String lat, String lng, int r) throws Exception {
        boolean has = coreTencentService.hasTrafficHub(lat, lng, r);
        return BaseResponse.Builder.build(has);
    }

    @ApiOperation(value = "获取交通枢纽信息（根据经纬度）")
    @GetMapping("/getTrafficInfoByPoint")
    public BaseResponse getTrafficInfoByPoint(String lat, String lng) throws Exception {
        TrafficInfo trafficInfoByPoint1 = coreTencentService.getTrafficInfoByPoint(lat, lng);
        return BaseResponse.Builder.build(trafficInfoByPoint1);
    }



}
