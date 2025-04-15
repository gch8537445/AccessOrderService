package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
//import com.ipath.orderflowservice.feignclient.dto.RequestCouponConsumeDto;
import com.ipath.orderflowservice.feignclient.dto.RequestCompanyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestUserCouponDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ipath.orderflowservice.feignclient.dto.RequestChangeCouponState;

@FeignClient(value = "coupon-consume-service")
public interface CouponConsumeFeign {
    /**
     * 获取用户可用优惠券
     */
    @RequestMapping(value = "/api/v2/couponconsume/GetCouponsOfUser", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getUserCoupons(@RequestBody RequestUserCouponDto requestUserCouponDto);

    /**
     * 订单取消时，通知解冻优惠券
     */
    @RequestMapping(value = "/api/v2/couponconsume/ChangeCouponState", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse changeCouponState(@RequestBody RequestChangeCouponState requestChangeCouponState);

    /**
     * 获取公司可用的增值服务费用
     */
    @RequestMapping(value = "/api/v2/agent/extra/datalist", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getCompanyExtraList(@RequestBody RequestCompanyDto requestCompanyDto);
}
