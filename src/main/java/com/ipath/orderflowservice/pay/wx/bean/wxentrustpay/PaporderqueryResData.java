package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class PaporderqueryResData {

    /**
     * 签约对应公众号的openid。小程序对应outerid
     */
    @JSONField(name="openid")
    private String openid;

    /**
     * 交易状态
     * SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * ACCEPT：已接收，等待扣款
     * PAY_FAIL：支付失败(其他原因，如银行返回失败)
     */
    @JSONField(name="trade_state")
    private String tradeState;

    /**
     * 订单总金额，单位为分
     */
    @JSONField(name="total_fee")
    private String totalFee;

    /**
     * 客户端订单号
     */
    @JSONField(name="out_trade_no")
    private String outTradeNo;

    /**
     * 订单支付时间，格式为yyyyMMddHHmmss
     */
    @JSONField(name="time_end")
    private String timeEnd;
}
