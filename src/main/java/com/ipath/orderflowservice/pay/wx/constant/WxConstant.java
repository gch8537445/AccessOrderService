package com.ipath.orderflowservice.pay.wx.constant;

/**
 * 微信支付 相关配置
 */
public class WxConstant {
    /**
     * 免密支付 接口
     */
    //小程序申请免密支付请求
    public static final String WX_ENTRUST_PAY_REQUESTAPPLETPAY = "pay/wxentrustpay/requestappletpay";
    //公众号拉起免密支付页面
    public static final String WX_ENTRUST_PAY_MMPAY = "pay/wxentrustpay/mmpay";
    //查询微信免密签约信息
    public static final String WX_ENTRUST_PAY_QUERYCONTRACT = "pay/wxentrustpay/querycontract";
    //申请解约免密支付
    public static final String WX_ENTRUST_PAY_DELETECONTRACT= "pay/wxentrustpay/deletecontract";
    //申请扣款
    public static final String WX_ENTRUST_PAY_APPLYFORDEDUCTIONS = "pay/wxentrustpay/applyfordeductions";
    //查询扣款订单信息
    public static final String WX_ENTRUST_PAY_PAPORDERQUERY = "pay/wxentrustpay/paporderquery";


}
