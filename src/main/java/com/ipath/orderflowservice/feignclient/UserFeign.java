package com.ipath.orderflowservice.feignclient;

import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestGetUserBaseInfoDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSetUserMobileDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user-service")
public interface UserFeign {

    /**
     * 如果用户没有设置过手机号则设置手机号
     */
    @RequestMapping(value = "/user/setUserMobileIfAbsense", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse setUserMobileIfAbsense(@RequestBody RequestSetUserMobileDto requestSetUserMobileDto);

    /**
     * 查询用户基础信息
     */
    @RequestMapping(value = "/user/getUserBaseInfo", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse getUserBaseInfo(@RequestBody RequestGetUserBaseInfoDto requestGetUserBaseInfoDto);

    /**
     * 查询公司配置信息
     */
    @RequestMapping(value = "/user/getCompanyCommonConfig", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse getCompanyCommonConfig(@RequestBody JSONObject para);
}
