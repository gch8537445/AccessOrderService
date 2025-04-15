package com.ipath.orderflowservice.feignclient;

import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "report-service")
public interface ReportFeign {

    @RequestMapping(value = "/report/mobile/getOrderListByMonth", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getOrderListByMonth(@RequestBody RequestOrderListByMonthDto requestOrderListByMonthDto);

    @RequestMapping(value = "/report/Report/GetOrderList", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getOrderList(@RequestBody JSONObject jsonObject);

    @RequestMapping(value = "/report/queryCompanyBearAmountByOrderId", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse queryCompanyBearAmountByOrderId(@RequestBody RequestOrderParamDto orderParamDto);
}
