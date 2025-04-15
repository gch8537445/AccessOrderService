package com.ipath.orderflowservice.core.order.service;



import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestEstimateDto;
import com.ipath.orderflowservice.feignclient.dto.RequestPlaceOrderDto;
import com.ipath.orderflowservice.order.bean.CoreOrderDetail;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.SelectTakeOrderParam;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import com.ipath.orderflowservice.order.bean.param.ordercore.RePlaceOrderReq;

import java.util.List;
import java.util.Map;

/**
 * core端订单服务 Service
 */
public interface CoreOrderService {

    /**
     * 下单
     * @return
     */
    BaseResponse placeOrder(RequestPlaceOrderDto requestPlaceOrderDto, CreateOrderParam orderParam) throws Exception;

    BaseResponse placeOrderRes(RequestPlaceOrderDto req) throws Exception;

    /**
     * 预估
     * @return
     */
    JSONObject estimate(RequestEstimateDto requestEstimateDto, CreateOrderParam orderParam,String traceId,Boolean isAppendEstimate) throws Exception;


    /**
     * core端通知取消后 重新下单接口
     * @param rePlaceOrderReq
     * @return
     * @throws Exception
     */
    Boolean afterCoreCancelAfreshPlaceOrder(RePlaceOrderReq rePlaceOrderReq, Long orderId) throws Exception;

    /**
     * core端通知取消后 取消下单接口
     *
     * @param coreOrderId
     * @return
     * @throws Exception
     */
    Boolean afterCoreCancelClose(String coreOrderId, Long orderId) throws Exception;

    /**
     * core端通知取消后 重新下单 次数校验是否通过接口
     * <p>
     * 说明：
     * 重复下单次数 十分钟内达到1次后终止重复下单，取消通知core端服务
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    Boolean afterCoreCancelAfreshPlaceOrderCheck(Long orderId) throws Exception;


    Boolean afterCoreCancelAfreshPlaceOrderCheckMax(Long orderId) throws Exception;

    Map<String, Object> estimate(CreateOrderParam orderParam, List<SelectedCar> excludeCars) throws Exception;

    /**
     * 根据coreOrderId获取 core端订单信息
     * @param coreOrderId
     * @return
     */
    JSONObject getOrderDetailJson(String coreOrderId);

    /**
     * 根据coreOrderId获取 core端订单信息
     * @param coreOrderId
     * @return
     */
    CoreOrderDetail getOrderDetail(String coreOrderId);


    BaseResponse changeDest(CreateOrderParam orderParam, String coreOrderId);

    BaseResponse cancelOrder(Long companyId,Long userId,Long orderId,String coreOrderId, String reason,Short preState);
}
