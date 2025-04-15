package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestCompanyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestGetUserBaseInfoDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSetUserMobileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "file-manage-service")
public interface FileManageFeign {

    @RequestMapping(value = "/filemgt/token", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse getTempToken(@RequestBody RequestCompanyDto requestCompanyDto);
}
