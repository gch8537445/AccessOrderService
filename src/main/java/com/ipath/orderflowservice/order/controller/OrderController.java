package com.ipath.orderflowservice.order.controller;

import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseController;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.bean.CustomException;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.constant.UserConstant;
import com.ipath.orderflowservice.order.bean.param.*;
import com.ipath.orderflowservice.order.bean.vo.DriverInfo;
import com.ipath.orderflowservice.order.business.cartypelabel.service.CarTypeLabelService;
import com.ipath.orderflowservice.order.business.dispatch.bean.param.SubmitDispatchParam;
import com.ipath.orderflowservice.order.business.dispatch.service.DispatchService;
import com.ipath.orderflowservice.order.service.*;
import com.ipath.orderflowservice.order.util.CacheUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DispatchService dispatchService;
    @Autowired
    private LogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheService cacheService;

    /**
     * 预估价格
     */
    @PostMapping("/estimate")
    @ResponseBody
    public BaseResponse estimate(@RequestBody CreateOrderParam orderParam) throws Exception {
        SaSession session = StpUtil.getSession();
        orderParam.setUserId(StpUtil.getLoginIdAsLong());
        orderParam.setCompanyId((Long) session.get("companyId"));
        return BaseResponse.Builder
                .build(orderParam.getServiceType() == (short) 20 ? orderService.estimateForRental(orderParam)
                        : orderService.estimate(orderParam, false, null));
    }

    /**
     * 追加车型预估价格
     * 调用中台预估接口 估价结果中剔除掉本单已经选择过的车型和本单无法选择的车型(价格过高)
     */
    @PostMapping("/appendEstimate")
    @ResponseBody
    public BaseResponse appendEstimate(@RequestBody OrderIdParam param) throws Exception {
        return BaseResponse.Builder.build(orderService.appendEstimate(StpUtil.getLoginIdAsLong(), param.getOrderId()));
    }

    /**
     * 修改目的地重新估价，必须上传orderId
     */
    @PostMapping("/changeDestEstimate")
    @ResponseBody
    public BaseResponse changeDestEstimate(@RequestBody CreateOrderParam orderParam) throws Exception {
        SaSession session = StpUtil.getSession();
        orderParam.setUserId(StpUtil.getLoginIdAsLong());
        orderParam.setCompanyId((Long) session.get("companyId"));
        return BaseResponse.Builder.build(orderService.changeDestEstimate(orderParam));
    }

    /**
     * 下单
     */
    @PostMapping("/placeOrder")
    @ResponseBody
    public BaseResponse placeOrder(@RequestBody CreateOrderParam orderParam) throws Exception {
        SaSession session = StpUtil.getSession();
        orderParam.setUserId(StpUtil.getLoginIdAsLong());
        orderParam.setCompanyId((Long) session.get("companyId"));
        // 日志(1、创建)
        Log iLog = logService.getLog((Long) session.get("companyId"), StpUtil.getLoginIdAsLong(), null,
                InterfaceEnum.ORDER_PLACEORDER);
        // 日志(2、请求参数)
        iLog.setBody(JSONUtil.toJsonStr(orderParam));

        // 防止重复提交锁
        String lockKey = "orderflowserviceplaceOrder:" + orderParam.getUserId() + ":" + orderParam.getEstimateId()
                + orderParam.getConfirm();
        boolean isLock = redisUtil.setLock(lockKey, "下单防重复锁", 1);
        if (!isLock && redisUtil.hasKey(lockKey)) {
            // 日志(保存错误)
            iLog.setErrorMsg("您已提交过相同订单，不可重复下单");
            logService.saveErrorLogAsync(iLog, "{response:\"您已提交过相同订单，不可重复下单\"}");
            throw new BusinessException("您已提交过相同订单，不可重复下单");
        }

        try {
            Map<String, Object> map;
            if (orderParam.getServiceType() == (short) 20) {
                map = orderService.placeOrderForRental(orderParam);
            } else {
                map = orderService.placeOrder(orderParam, true, 0L);
            }
            if (null != map && null != map.get("orderId")) {
                orderParam.setOrderId(Long.valueOf(map.get("orderId").toString()));
            }
            return BaseResponse.Builder.build(map);
        } catch (CustomException e) {
            log.error("placeOrder ===> {}", e);
            throw e;
        } catch (BusinessException e) {
            log.error("placeOrder ===> {}", e);
            throw e;
        } catch (Exception e) {
            log.error("placeOrder ===> {}", e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
    }

    /**
     * 追加车型下单
     */
    @PostMapping("/appendPlaceOrder")
    @ResponseBody
    public BaseResponse appendPlaceOrder(@RequestBody AppendCarTypeParam appendCarTypeParam) throws Exception {
        SaSession session = StpUtil.getSession();
        appendCarTypeParam.setUserId(StpUtil.getLoginIdAsLong());
        appendCarTypeParam.setCompanyId((Long) session.get("companyId"));

        return BaseResponse.Builder.build(orderService.appendPlaceOrder(appendCarTypeParam, false));
    }

    /**
     * 修改目的地
     */
    @PostMapping("/changeDest")
    @ResponseBody
    public BaseResponse changeDest(@RequestBody CreateOrderParam orderParam) throws Exception {
        SaSession session = StpUtil.getSession();
        orderParam.setUserId(StpUtil.getLoginIdAsLong());
        orderParam.setCompanyId((Long) session.get("companyId"));

        long startTime = System.currentTimeMillis();
        Log iLog = logService.getLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId(),
                InterfaceEnum.ORDER_CHAGE_DEST_PLACEORDER);
        iLog.setBody(JSONUtil.toJsonStr(orderParam));

        try {
            orderService.changeDest(orderParam);
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveLogAsync(iLog, null);
            return BaseResponse.Builder.success();
        } catch (CustomException e) {
            log.error("changeDest ===>修改目的地异常[CustomException]", e);
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            throw e;
        } catch (BusinessException e) {
            log.error("changeDest ===> 修改目的地异常[BusinessException]", e);
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            throw e;
        } catch (Exception e) {
            log.error("changeDest ===>修改目的地异常[Exception]", e);
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
    }

    /**
     * 极速派提交
     */
    @PostMapping("/submitDispatchParam")
    @ResponseBody
    public BaseResponse submitDispatchParam(@RequestBody SubmitDispatchParam submitDispatchParam) throws Exception {
        SaSession session = StpUtil.getSession();
        submitDispatchParam.setUserId(StpUtil.getLoginIdAsLong());
        submitDispatchParam.setCompanyId((Long) session.get("companyId"));
        // 需求:极速派单调度 提交
        dispatchService.submitDispatch(submitDispatchParam);
        return BaseResponse.Builder.success();
    }

    /**
     * 取消当前用户订单
     * 此接口只给前端用，其他地方需要取消的，提供单独的方法，走特殊验签规则。
     */
    @PostMapping("/cancelOrder")
    @ResponseBody
    public BaseResponse cancelOrder(@RequestBody OrderReasonParam orderReason) throws Exception {
        orderReason.setUserId(StpUtil.getLoginIdAsLong());
        orderReason.setCompanyId((Long) StpUtil.getSession().get("companyId"));

        long startTime = System.currentTimeMillis();
        Log iLog = logService.getLog(orderReason.getCompanyId(), orderReason.getUserId(), orderReason.getOrderId(),
                InterfaceEnum.RECEIVE_APP_CANCELORDER);
        iLog.setBody(JSONUtil.toJsonStr(orderReason));
        try {
            Map<String, Object> result = orderService.cancelOrder(orderReason.getUserId(), orderReason.getOrderId(), 1,
                    orderReason.getReason());
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveLogAsync(iLog, JSONUtil.toJsonStr(BaseResponse.Builder.build(result)));
            return BaseResponse.Builder.build(result);
        } catch (BusinessException e) {
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            log.error("cancelOrder ===> {}", e);
            throw e;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            log.error("cancelOrder ===> {}", e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
    }

    /**
     * 取消订单
     * 非用户前端调用接口
     *
     * @param orderReason
     * @return
     * @throws Exception
     */
    @PostMapping("/cancelOrderForOpe")
    @ResponseBody
    public BaseResponse cancelOrderForOpe(@RequestBody OrderReasonParam orderReason) throws Exception {
        long startTime = System.currentTimeMillis();
        Log iLog = logService.getLog(orderReason.getCompanyId(), orderReason.getUserId(), orderReason.getOrderId(),
                InterfaceEnum.RECEIVE_APP_CANCELORDERFOROPE);
        iLog.setBody(JSONUtil.toJsonStr(orderReason));

        try {
            Map<String, Object> result = orderService.cancelOrder(
                    orderReason.getUserId(),
                    orderReason.getOrderId(),
                    1, orderReason.getReason());
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveLogAsync(iLog, JSONUtil.toJsonStr(BaseResponse.Builder.build(result)));
            return BaseResponse.Builder.build(result);
        } catch (BusinessException e) {
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            log.error("cancelOrder ===> {}", e);
            throw e;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            iLog.setResMillsecond(endTime - startTime);
            logService.saveErrorLogAsync(iLog, e);
            log.error("cancelOrder ===> {}", e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
    }

    /**
     * 重新叫车（仅当运力平台取消订单时可以重新叫车）
     */
    @PostMapping("/reorder")
    @ResponseBody
    public BaseResponse reorder(@RequestBody ReorderParam param) throws Exception {
        return BaseResponse.Builder.build(orderService.rePlaceOrder(param));
    }

    /**
     * @param dataObject
     * @return
     * @throws Exception
     */
    @PostMapping("/rep")
    public BaseResponse rep(@RequestBody JSONObject dataObject) throws Exception {
        boolean orderId = orderService.rePlaceOrderAfterCoreCancelAfreshPlaceOrder(dataObject,
                dataObject.getLong("orderId"));
        return BaseResponse.Builder.build(orderId);
    }

    /**
     * 支付中心通知支付状态变化
     */
    @PostMapping("/orderPayNotify")
    @ResponseBody
    public BaseResponse orderPayNotify(@RequestBody OrderPayNotifyParam param) throws Exception {
        Log iLog = logService.getLog(param.getCompanyId(), null,
                ObjectUtil.isNotNull(param.getOrderIds()) ? param.getOrderIds().get(0) : null,
                InterfaceEnum.RECEIVE_BILL_PAY);
        iLog.setBody(JSONUtil.toJsonStr(param));
        logService.saveLogAsync(iLog, null);

        orderService.orderPayNotify(param);

        return BaseResponse.Builder.success();
    }

    /**
     * 接收预约管家推送的消息
     */
    @PostMapping("/booking/callback")
    @ResponseBody
    public BaseResponse notifyBookingResult(@RequestBody JSONObject dataObject) throws Exception {
        orderService.receiveBookingResult(dataObject);
        return BaseResponse.Builder.success();
    }

    /**
     * 结算成功回调
     * 
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/settle/callback")
    @ResponseBody
    public Map<String, Object> notifySettleResult(@RequestBody JSONObject param) throws Exception {
        Map<String, Object> result = new HashMap<>(1);

        try {
            orderService.processSettleResult(param);
            result.put("code", 0);
        } catch (Exception e) {
            result.put("code", -1);
        }
        return result;
    }

    /**
     * 回调接口 提供给中台通知订单状态变更
     * 参数名是中台定义的，不要修改
     * status: 1 等待司机接单 2 等待司机 3 司机到达 4 行程中 5 服务结束，待支付 6 订单完成，已支付 7 已取消
     */
    @PostMapping("/notifyOrderStatus")
    @ResponseBody
    public Map<String, Object> notifyOrderStatus(@RequestBody JSONObject param) throws Exception {
        Long orderId = null;
        try {
            JSONObject orderDetailJsonObject = param.getJSONObject("orderDetail");
            if (null != orderDetailJsonObject && orderDetailJsonObject.containsKey("userData")) {
                orderId = orderDetailJsonObject.getJSONObject("userData").getLong("originalOrderId");
            }
        } catch (Exception ex) {
            log.error(
                    "接收中台状态变更出现异常【OrderController->notifyOrderStatus.getOrderDetail,para:{}】",
                    JSONUtil.toJsonStr(param), ex);
        }
        if (null == orderId) {
            orderId = param.getLong("partnerOrderId", null);
        }

        Short orderStatus = param.getShort("status", null);
        Long userId = param.getLong("userId", null);
        Long companyId = param.getLong("companyId", null);
        Log iLog = logService.getLog(companyId, userId, orderId, InterfaceEnum.RECEIVE_CORE_ORDER_STATUS);
        iLog.setInterfaceName(iLog.getInterfaceName() + orderStatus);
        iLog.setBody(JSONUtil.toJsonStr(param));

        Map<String, Object> result = new HashMap<>(1);
        try {
            orderService.updateOrderDetails(param);
            logService.saveLogAsync(iLog, "success");
            result.put("code", 0);
        } catch (Exception e) {
            logService.saveErrorLogAsync(iLog, e);
            log.error("接收中台状态变更出现异常【OrderController->notifyOrderStatus,para:{}】", JSONUtil.toJsonStr(param), e);
            result.put("code", -1);
        }
        return result;
    }

    @PostMapping("/checkUserDispatch")
    @ResponseBody
    public BaseResponse checkUserDispatch(@RequestBody JSONObject param) {
        return BaseResponse.Builder.build(
                dispatchService.checkUserTemp(param.get("companyId", Long.class), param.get("userId", Long.class)));
    }

    @PostMapping("/handleMsgByNotifyOrderStatus")
    @ResponseBody
    public BaseResponse handleMsgByNotifyOrderStatus(@RequestBody JSONObject param) throws Exception {
        orderService.notifyOrderStatus(
                param.getLong("orderId"),
                param.getShort("previousOrderState"),
                param.getShort("orderState"),
                param.getBool("replaceOrderFlag", false),
                param.getStr("phoneForUser"));
        return BaseResponse.Builder.success();
    }

    @PostMapping("/test")
    @ResponseBody
    public BaseResponse test(@RequestBody JSONObject param) {
        return BaseResponse.Builder.build(cacheService.getPriorSource());
    }
}
