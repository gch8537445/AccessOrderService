package com.ipath.orderflowservice.log.controller;


import com.ipath.common.bean.BaseController;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.log.aop.AopLog;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.service.LogService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "日志处理")
@RestController
@RequestMapping("/order/log")
public class LogController extends BaseController {

    @Autowired
    private LogService logService;
    @AopLog(interfaceName = "设置日志权限",logName = "设置日志权限",classType = 1,companyId = "0")
    @PostMapping("/setLogEnabled")
    public BaseResponse setLogEnabled(boolean enabled, int level, String passWord, @RequestBody Log log) {
        if (!StringUtils.equals(passWord, "kakarotto")) {
            return BaseResponse.Builder.build("该用户没有操作权限！！！");
        }
        return BaseResponse.Builder.build(logService.setLogEnabled(enabled, level,log));
    }
}
