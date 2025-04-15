package com.ipath.orderflowservice.order.controller;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.order.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单服务监控")
@RestController
@RequestMapping("/order/monitoring")
public class MonitoringController {

    @Autowired
    private UserService userService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public BaseResponse health(Boolean checkAll) throws Exception {
        if(null != checkAll && checkAll){
            return BaseResponse.Builder.build(true);
        }else {
            return BaseResponse.Builder.success();
        }
    }
}
