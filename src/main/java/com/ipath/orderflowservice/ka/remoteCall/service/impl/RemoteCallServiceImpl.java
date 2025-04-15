package com.ipath.orderflowservice.ka.remoteCall.service.impl;


import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.ka.remoteCall.service.RemoteCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class RemoteCallServiceImpl implements RemoteCallService {

    @Autowired
    private RemoteCallFeign remoteCallFeign;

    @Override
    public BaseResponse call(String content, String path) throws Exception {
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setContent(content);
        remoteCallDto.setPath(path);
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            throw new BusinessException(response.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse callChannel(String content, String path) throws Exception {
        return null;
    }

    @Override
    public BaseResponse callByForm(String content, String path) throws Exception {
        return null;
    }

    @Override
    public BaseResponse callChannelDataStr(String content, String path) throws Exception {
        return null;
    }
}
