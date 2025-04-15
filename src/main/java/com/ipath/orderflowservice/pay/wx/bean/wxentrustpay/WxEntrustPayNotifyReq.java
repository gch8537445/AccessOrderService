package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WxEntrustPayNotifyReq {

    /**
     * 客户端订单号
     */
    @JSONField(name="OutTradeNo")
    private String outTradeNo;
    /**
     * 交易状态：1扣款成功  其他值则交易失败
     */
    @JSONField(name="TradeState")
    private Integer tradeState;
    /**
     * 支付时间，有可能为null。TradeState为1时不为null
     */
    @JSONField(name="PayTimeEnd")
    private String payTimeEnd;
    /**
     * 最终支付金额，以分为单位。TradeState为1时不为null
     */
    @JSONField(name="TotalFee")
    private Integer totalFee;
    /**
     * 用户数据，回调时原样返回
     */
    @JSONField(name="AttachInfo")
    private String attachInfo;
    /**
     * 签名
     */
    @JSONField(name="sign")
    private String sign;
}
