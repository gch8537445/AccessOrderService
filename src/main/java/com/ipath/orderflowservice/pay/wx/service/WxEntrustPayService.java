package com.ipath.orderflowservice.pay.wx.service;


import com.ipath.orderflowservice.pay.wx.bean.wxentrustpay.PaporderqueryResData;
import com.ipath.orderflowservice.pay.wx.bean.wxentrustpay.QuerycontractResData;
import com.ipath.orderflowservice.pay.wx.bean.wxentrustpay.WxEntrustAuthNotifyReq;
import org.springframework.web.servlet.view.RedirectView;


/**
 *  微信免密支付 service
 */
public interface WxEntrustPayService {

//    /**
//     * 小程序申请免密支付请求
//     * @param requestappletpayReq
//     * @throws Exception
//     */
//    void requestappletpay(RequestappletpayReq requestappletpayReq, Long userId, Long companyId) throws Exception;

    /**
     * 公众号拉起免密支付页面
     * @param userId
     * @return
     * @throws Exception
     */
    RedirectView mmpay(Long userId) throws Exception;

    /**
     * 申请解约免密支付
     * @param userId
     * @return
     * @throws Exception
     */
    boolean deletecontract(Long userId) throws Exception;

    /**
     * 查询微信免密签约信息
     * @param userId
     * @return
     * @throws Exception
     */
    QuerycontractResData querycontract(Long userId) throws Exception;

    /**申请扣款
     *
     * @param userId
     * @param orderId
     * @return
     * @throws Exception
     */
    boolean vxEntrustApplyPay(Long userId, Long orderId) throws Exception;
    /**
     * 查询扣款订单信息
     * @param orderId
     * @return
     * @throws Exception
     */
    PaporderqueryResData vxEntrustaQueryPay(Long userId, Long orderId) throws Exception;

    /**
     * 微信免密支付-协议授权结果回调
     * @param param
     * @return
     * @throws Exception
     */
    boolean wxEntrustAuthNotify(WxEntrustAuthNotifyReq param) throws Exception;
}
