package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WxEntrustAuthNotifyReq {
    /**
     * 客户免密支付协议ID，需唯一。客户端生成保存，后期接口使用。 不能超过Int64的范围（9223372036854775807）
     */
    @JSONField(name="requestSerial")
    private String requestSerial;
    /**
     * 签约类型：ADD：签约   DELETE：解约
     */
    @JSONField(name="ChangeType")
    private String changeType;
    /**
     * 0：已签约   1：未签约  9：签约进行中
     */
    @JSONField(name="ResultCode")
    private String resultCode;
    /**
     * 签名
     */
    @JSONField(name="sign")
    private String sign;
    /**
     * 用户数据，回调时原样返回
     */
    @JSONField(name="AttachInfo")
    private String attachInfo;

}
