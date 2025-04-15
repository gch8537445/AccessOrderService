package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 申请解约免密支付请求
 */
@Data
public class WxentrustpayRequest {
    /**
     * 客户免密支付协议ID，需唯一。客户端生成保存，后期接口使用。 不能超过Int64的范围（9223372036854775807）
     */
    @JSONField(name="requestSerial")
    private String requestSerial;
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
