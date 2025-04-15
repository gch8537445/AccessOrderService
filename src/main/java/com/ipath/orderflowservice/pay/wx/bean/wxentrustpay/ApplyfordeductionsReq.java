package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 申请扣款 请求参数
 */
@Data
public class ApplyfordeductionsReq {
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
     * 用户数据，回调时原样返回
     */
    @JSONField(name="attachInfo")
    private String attachInfo;
    /**
     * 签名
     */
    @JSONField(name="sign")
    private String sign;

    /**
     * 商品信息
     */
    @JSONField(name="goodsBody")
    private String goodsBody;

    /**
     * 商品明细信息
     */
    @JSONField(name="goodsDetail")
    private String goodsDetail;

    /**
     * 客户端订单号
     */
    @JSONField(name="outTradeNo")
    private String outTradeNo;

    /**
     * 订单总金额，单位为分，只能为整数
     */
    @JSONField(name="total_fee")
    private Integer totalFee;

}
