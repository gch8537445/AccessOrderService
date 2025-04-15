package com.ipath.orderflowservice.ka.remoteCall.service;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface RemoteCallService {


    BaseResponse call(String content, String path) throws Exception;

    BaseResponse callChannel(String content, String path) throws Exception;

    BaseResponse callByForm(String content, String path) throws Exception;

    BaseResponse callChannelDataStr(String content, String path) throws Exception;

}
