package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 小程序申请免密支付请求 请求参数
 */
@Data
public class RequestappletpayReq {
    /**
     * 客户免密支付协议ID，需唯一。客户端生成保存，后期接口使用。 不能超过Int64的范围（9223372036854775807）
     */
    @JSONField(name="requestSerial")
    private String requestSerial;
    /**
     * 签约用户的名称，用于页面展示，参数值不支持UTF8非3字节编码的字符，例如表情符号，所以请勿传微信昵称到该字段
     */
    @JSONField(name="userName")
    private String userName;
    /**
     * 签约协议号
     */
    @JSONField(name="contract_code")
    private String contractCode;
    /**
     * 商户侧用户标识
     */
    @JSONField(name="outerid")
    private String outerId;
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
     * 协议模板id。目前固定"140998"
     */
    @JSONField(name="templeteID")
    private String templeteId;
    /**
     * 用户数据，回调时原样返回
     */
    @JSONField(name="attachInfo")
    private String attachInfo;
    /**
     * 签名
     */
    @JSONField(name="sign")
    private String sign;

}
