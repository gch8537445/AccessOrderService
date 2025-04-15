package com.ipath.orderflowservice.order.service;


import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.order.bean.param.*;
import com.ipath.orderflowservice.order.dao.vo.OrderForReport;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;

import cn.hutool.json.JSONObject;

public interface OrderService {
    /**
     * 估价
     */
    Map<String, Object> estimate(CreateOrderParam orderParam, Boolean isAppendEstimate, List<SelectedCar> excludeCars) throws Exception;

    /**
     * 包车预估
     * @param orderParam
     * @return
     * @throws Exception
     */
    Map<String, Object> estimateForRental(CreateOrderParam orderParam) throws Exception;

    /**
     * 下单
     */
    Map<String, Object> placeOrder(CreateOrderParam orderParam,Boolean isCheckPrePay,Long prePayOrderId) throws Exception;

    /**
     * 包车下单
     * @param orderParam
     * @return
     * @throws Exception
     */
    Map<String, Object> placeOrderForRental(CreateOrderParam orderParam) throws Exception;

    /**
     * 订单修改目的地重新估价估价
     */
    Map<String, Object> changeDestEstimate(CreateOrderParam orderParam) throws Exception;

    /**
     * 订单修改目的地
     */
    int changeDest(CreateOrderParam orderParam) throws Exception;
    
    /**
     * 追加车型预估价格
     * 调用中台预估接口 估价结果中剔除掉本单已经选择过的车型和本单无法选择的车型(价格过高)
     */
    Map<String, Object> appendEstimate(Long userId, Long orderId) throws Exception;
    
    /**
     * 追加车型下单
     * @param appendCarTypeParam
     * @param isPrePay
     * @return
     * @throws Exception
     */
    BaseResponse appendPlaceOrder(AppendCarTypeParam appendCarTypeParam,boolean isPrePay) throws Exception;

    /**
     * 取消订单
     * @param userId
     * @param orderId
     * @param type
     * @param reason
     * @return
     * @throws Exception
     */
    Map<String, Object> cancelOrder(Long userId, Long orderId, int type, String reason) throws Exception;

    /**
     * 支付中心通知支付状态变化
     * @param param
     * @throws Exception
     */
    void orderPayNotify(OrderPayNotifyParam param) throws Exception;

    /**
     * 状态变更
     * @param param
     * @throws Exception
     */
    void updateOrderDetails(JSONObject param) throws Exception;

    /**
     * 获取司机位置
     * @param coreOrderId
     * @return
     * @throws Exception
     */
    JSONObject getDriverLocation(String coreOrderId) throws Exception;

    /**
     * 大额预付自动下单
     * @param estimateParam
     * @param orderId
     * @throws Exception
     */
    void autoPlaceOrder(CreateOrderParam estimateParam, Long orderId) throws Exception;

    /**
     * 订单取消后自动下单
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> rePlaceOrder(ReorderParam param) throws Exception;

    /**
     * 接收预约管家回调通知
     * @param dataObject
     * @throws Exception
     */
    void receiveBookingResult(JSONObject dataObject) throws Exception;

    /**
     * 自动重新派单（平台/司机取消）
     * @param dataObject
     * @param orderId
     * @return
     * @throws Exception
     */
    boolean rePlaceOrderAfterCoreCancelAfreshPlaceOrder(JSONObject dataObject, Long orderId) throws Exception;

    /**
     * 自动重新派单 （屏蔽同一个司机，单个平台下单 平台取消 ）
     * @param dataObject
     * @param orderId
     * @return
     * @throws Exception
     */
    boolean rePlaceOrderOneSource(JSONObject dataObject, Long orderId) throws Exception;

    /**
     * 订单状态变更
     * @param orderId
     * @param previousOrderState
     * @param orderState
     * @param replaceOrderFlag
     * @param phoneForUser
     * @throws Exception
     */
    @Async
    void notifyOrderStatus(Long orderId, Short previousOrderState, Short orderState, Boolean replaceOrderFlag,String phoneForUser) throws Exception; 

    /**
     * 结算成功回调
     * @param settleResultJsonObject
     * @throws Exception
     */
    void processSettleResult(JSONObject settleResultJsonObject) throws Exception;

    OrderForReport getOrderForReportById(Long orderId);

}
