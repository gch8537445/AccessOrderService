package com.ipath.orderflowservice.feignclient;

import com.ipath.common.bean.BaseResponse;
import com.ipath.dao.param.QueryOrderCriteria;
import com.ipath.orderflowservice.feignclient.dto.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "bill-service")
public interface BillFeign {
    /**
     * 大额预付费
     */
    @RequestMapping(value = "/api/v2/bill/UserPayments/PrepaymentPay", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse prepaymentPay(@RequestBody RequestPayDto requestPrePayDto);

    /**
     * 获取大额预付需支付金额
     */
    /*@RequestMapping(value = "/api/v2/bill/Settle/EvaluateOrderSettle", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse evaluateOrderSettle(@RequestBody RequestSettlementDto requestSettlementDto);*/
    @RequestMapping(value = "/api/v2/bill/Settle/EvaluateOrderSettle", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse evaluateOrderSettle(@RequestBody RequestBillNotifySettleOrderDto requestBillNotifySettleOrderDto);

    /**
     * 获取订单结算详情
     */
    @RequestMapping(value = "/api/v2/bill/Settle/GetOrderSettleInfo", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getOrderSettleInfo(@RequestBody RequestSettleInfoDto requestSettleInfoDto);

    /**
     * 待支付列表
     */
    @RequestMapping(value = "/api/v2/bill/UserPayments/GetPayOrders", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getPayOrders(@RequestBody RequestPayOrderDto requestPayOrderDto);

    /**
     * 获取订单结算详情
     */
    @RequestMapping(value = "/api/v2/bill/UserPayments/SelectWaitPayOrderCount", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse selectWaitPayOrderCount(@RequestBody RequestSelectWaitPayOrderCountDto requestSelectWaitPayOrderCountDto);


    /**
     * 获取公司的默认账户 ka的接口
     * @param comid
     * @return
     */
    @RequestMapping(value = "/api/v2/bill/GetCompanyDefaultAccount/{comid}", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getCompanyDefaultAccount(@PathVariable Long comid);

    /**
     * 保存人员账户关系 
     * @param list
     * @return
     */
    @RequestMapping(value = "/api/v2/bill/CompanyAccount/SaveAccountUser", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse saveAccountUser(@RequestBody List<RequestCompanyAccountUserDto> list);

    //以下暂时用不到

    /**
     * 按月获取用户订单金额
     */
    @RequestMapping(value = "/api/v2/bill/UserPayments/GetOrderTotalAmountByMonth", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse getOrderTotalAmountByMonth(@RequestBody QueryOrderCriteria requestQueryOrderCriteria);

    /**
     * 订单结算
     */
    @RequestMapping(value = "/api/v2/bill/Settle/SettleOrder", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse settleOrder(@RequestBody RequestOrderNotifyDto requestOrderNotifyDto);

    /**
     * 个人支付
     */
    @RequestMapping(value = "/api/v2/bill/UserPayments/Pay", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
    BaseResponse userPay(@RequestBody RequestPayDto requestPrePayDto);

//
//    /**
//     * 获取大额预付详情
//     */
//    @RequestMapping(value = "/api/v2/bill/UserPayments/PrepaymentOrderInfo", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
//    BaseResponse prepaymentOrderInfo(@RequestBody RequestPrepaymentOrderInfoDto requestPrepaymentOrderInfoDto);
//

//    /**
//     * 获取状态
//     */
//    @RequestMapping(value = "/api/v2/bill/UserPayments/PMOrderByPayOrderid", method = RequestMethod.POST, headers = {"content-type=application/json", "platform=11"}, consumes = "application/json")
//    BaseResponse pmOrderByPayOrderid(RequestGetPayStateDto requestGetPayStateDto);
}
