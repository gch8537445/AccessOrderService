package com.ipath.orderflowservice.order.business.dispatch.service;


import cn.hutool.json.JSONObject;
import com.ipath.orderflowservice.feignclient.dto.RequestAppendPlaceOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestBillNotifySettleOrderDto;
import com.ipath.orderflowservice.feignclient.dto.RequestPlaceOrderDto;
import com.ipath.orderflowservice.feignclient.dto.SettleReq;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.OrderBaseInfoParam;
import com.ipath.orderflowservice.order.business.dispatch.bean.param.SubmitDispatchParam;
import com.ipath.orderflowservice.order.business.dispatch.bean.vo.OrderDispatchConfigVo;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;


/**
 * 极速派单调度 service
 */
public interface DispatchService {

    /**
     * 需求:极速派单调度 下单
     */
    String placeOrder(RequestPlaceOrderDto requestPlaceOrderDto, CreateOrderParam orderParam);

    /**
     * 需求:极速派单调度 下单(mq 延时)
     */
    String depayPlaceOrder(long orderId, long coreOrderId);

    /**
     * 需求:极速派单调度 下单(取消/提交 立即)
     */
    String immediatelyPlaceOrder(long orderId, boolean isdelay);

    /**
     * 需求:极速派单调度 下单(追加车型)
     */
    boolean appendPlaceOrder(RequestAppendPlaceOrderDto requestAppendPlaceOrderDto, OrderBase orderBase, JSONObject customObject);

    /**
     *  需求:极速派单调度 提交
     */
    void submitDispatch(SubmitDispatchParam param);

    /**
     *  需求:极速派单调度 中台状态5通知 （结算处理）
     */
    void notifyOrderStatus5Settle(RequestBillNotifySettleOrderDto requestSettleOrderDto, OrderBase orderBase, OrderSource orderSource);
    /**
     *  需求:极速派单调度 中台状态5通知 （合规预警处理）
     */
    void notifyOrderStatus5AbnormalOrder(OrderBase orderBase, OrderSource orderSource);
    /**
     * 需求:极速派单调度 取消
     */
    void cancelOrder(OrderBase orderBase, OrderSource orderSource);

    /**
     *  // 需求:极速派单调度 查询订单基础信息 设置极速派单配置
     */
    void setOrderBaseInfoDispatchConfig(OrderBaseInfoParam param,OrderBase orderBase);

    void notifyOrderStatus5SettleNew(SettleReq settleReq, OrderBase orderBase, OrderSource orderSource);

    boolean checkUserTemp(Long companyId, Long userId);
}
