package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 查询扣款订单信息 请求参数
 */
@Data
public class PaporderqueryReq {

    /**
     * 客户端订单号
     */
    @JSONField(name="out_trade_no")
    private String outTradeNo;
    /**
     * 小程序appid
     */
    @JSONField(name="appid")
    private String appId;
    /**
     * 配置ID。例如："minipquanzi"
     */
    @JSONField(name="configID")
    private String configId;
    /**
     * 签名
     */
    @JSONField(name="sign")
    private String sign;






}
