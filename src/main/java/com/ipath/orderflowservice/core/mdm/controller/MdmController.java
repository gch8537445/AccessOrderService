package com.ipath.orderflowservice.core.mdm.controller;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.core.mdm.service.PoiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "运力接口")
@RestController
@RequestMapping("/order/mdm")
public class MdmController {


    @Autowired
    private PoiService poiService;

    @PostMapping("/reOrderRedisStationPoi")
    public BaseResponse reOrderRedisStationPoi() throws Exception {
        Map<Object, Object> objectObjectMap = poiService.reOrderRedisStationPoi();
        return BaseResponse.Builder.build(objectObjectMap);
    }

    @PostMapping("/getStationTypeByTpoiId")
    public BaseResponse getStationTypeByTpoiId(String tPoiId) throws Exception {
        String stationType = poiService.getStationTypeByTpoiId(tPoiId);
        return BaseResponse.Builder.build(stationType);
    }
}
